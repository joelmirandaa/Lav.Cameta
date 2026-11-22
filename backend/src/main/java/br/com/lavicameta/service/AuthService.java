package br.com.lavicameta.service;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.dto.AuthDtos.GoogleLoginRequest;
import br.com.lavicameta.dto.AuthDtos.LoginRequest;
import br.com.lavicameta.dto.AuthDtos.RegistroRequest;
import br.com.lavicameta.dto.AuthDtos.SessaoResponse;
import br.com.lavicameta.dto.DtoMapper;
import br.com.lavicameta.entity.TipoUsuario;
import br.com.lavicameta.entity.Usuario;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.UsuarioRepository;
import br.com.lavicameta.security.GoogleTokenVerifier;
import br.com.lavicameta.security.GoogleTokenVerifier.IdentidadeGoogle;
import br.com.lavicameta.security.TokenService;
import br.com.lavicameta.security.TokenService.TokenEmitido;
import br.com.lavicameta.util.Contatos;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastro e autenticação de clientes e administradores (RF01–RF03, RF17, UC02, RNF10). */
@Service
public class AuthService {

    private static final String MSG_CREDENCIAIS = "E-mail/telefone ou senha incorretos.";

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final GoogleTokenVerifier googleVerifier;
    private final Clock clock;
    private final int maxTentativas;
    private final Duration bloqueio;
    /** Hash usado quando o usuário não existe, para manter tempo de resposta semelhante. */
    private final String hashFicticio;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder, TokenService tokenService,
                       GoogleTokenVerifier googleVerifier, Clock clock, LavicametaProperties propriedades) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.googleVerifier = googleVerifier;
        this.clock = clock;
        this.maxTentativas = propriedades.seguranca().maxTentativasLogin();
        this.bloqueio = Duration.ofMinutes(propriedades.seguranca().bloqueioMinutos());
        this.hashFicticio = passwordEncoder.encode("lavicameta-sem-usuario");
    }

    @Transactional
    public SessaoResponse registrar(RegistroRequest req) {
        String email = Contatos.normalizarEmail(req.email());
        String telefone = Contatos.normalizarTelefone(req.telefone());
        if (email != null && usuarios.existsByEmail(email)) {
            throw ApiException.conflito("EMAIL_EM_USO", "Este e-mail já está cadastrado.");
        }
        if (telefone != null && usuarios.existsByTelefone(telefone)) {
            throw ApiException.conflito("TELEFONE_EM_USO", "Este telefone já está cadastrado.");
        }
        Instant agora = clock.instant();
        Usuario usuario = new Usuario(req.nome().trim(), email, telefone, TipoUsuario.CLIENTE, agora);
        usuario.definirSenhaHash(passwordEncoder.encode(req.senha()), agora);
        usuarios.save(usuario);
        return sessao(usuario);
    }

    /** Falhas de senha precisam ser gravadas mesmo quando a resposta é um erro. */
    @Transactional(noRollbackFor = ApiException.class)
    public SessaoResponse loginCliente(LoginRequest req) {
        Usuario usuario = autenticarComSenha(buscarPorIdentificador(req.identificador()), req.senha());
        if (usuario.isAdmin()) {
            throw ApiException.proibido("Administradores devem usar o Acesso Administrativo.");
        }
        return sessao(usuario);
    }

    @Transactional(noRollbackFor = ApiException.class)
    public SessaoResponse loginAdmin(LoginRequest req) {
        Optional<Usuario> encontrado = buscarPorIdentificador(req.identificador());
        Usuario usuario = autenticarComSenha(encontrado, req.senha());
        if (!usuario.isAdmin()) {
            throw ApiException.proibido("Acesso restrito a administradores.");
        }
        return sessao(usuario);
    }

    @Transactional
    public SessaoResponse loginGoogle(GoogleLoginRequest req) {
        IdentidadeGoogle identidade = googleVerifier.verificar(req.idToken());
        Instant agora = clock.instant();
        Usuario usuario = usuarios.findByGoogleId(identidade.googleId())
                .or(() -> vincularPorEmail(identidade, agora))
                .orElseGet(() -> criarComGoogle(identidade, agora));
        if (usuario.isAdmin()) {
            throw ApiException.proibido("Administradores devem usar o Acesso Administrativo.");
        }
        return sessao(usuario);
    }

    private Optional<Usuario> vincularPorEmail(IdentidadeGoogle identidade, Instant agora) {
        if (identidade.email() == null) {
            return Optional.empty();
        }
        return usuarios.findByEmail(Contatos.normalizarEmail(identidade.email())).map(u -> {
            u.vincularGoogle(identidade.googleId(), agora);
            return u;
        });
    }

    private Usuario criarComGoogle(IdentidadeGoogle identidade, Instant agora) {
        if (identidade.email() == null) {
            throw ApiException.invalido("GOOGLE_SEM_EMAIL", "A conta Google precisa de um e-mail verificado.");
        }
        String nome = identidade.nome() == null || identidade.nome().isBlank() ? identidade.email() : identidade.nome();
        Usuario usuario = new Usuario(nome.length() > 120 ? nome.substring(0, 120) : nome,
                Contatos.normalizarEmail(identidade.email()), null, TipoUsuario.CLIENTE, agora);
        usuario.vincularGoogle(identidade.googleId(), agora);
        return usuarios.save(usuario);
    }

    private Optional<Usuario> buscarPorIdentificador(String identificador) {
        if (Contatos.pareceEmail(identificador)) {
            return usuarios.findByEmail(Contatos.normalizarEmail(identificador));
        }
        return usuarios.findByTelefone(Contatos.normalizarTelefone(identificador));
    }

    private Usuario autenticarComSenha(Optional<Usuario> encontrado, String senha) {
        Instant agora = clock.instant();
        if (encontrado.isEmpty()) {
            passwordEncoder.matches(senha, hashFicticio);
            throw ApiException.naoAutorizado("CREDENCIAIS_INVALIDAS", MSG_CREDENCIAIS);
        }
        Usuario usuario = encontrado.get();
        if (usuario.estaBloqueado(agora)) {
            throw contaBloqueada(usuario, agora);
        }
        if (usuario.getSenhaHash() == null || !passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            usuario.registrarFalhaLogin(maxTentativas, bloqueio, agora);
            if (usuario.estaBloqueado(agora)) {
                throw contaBloqueada(usuario, agora);
            }
            throw ApiException.naoAutorizado("CREDENCIAIS_INVALIDAS", MSG_CREDENCIAIS);
        }
        usuario.registrarLoginComSucesso(agora);
        return usuario;
    }

    private static ApiException contaBloqueada(Usuario usuario, Instant agora) {
        long minutos = Math.max(1, (long) Math.ceil(Duration.between(agora, usuario.getBloqueadoAte()).getSeconds() / 60.0));
        return new ApiException(HttpStatus.LOCKED, "CONTA_BLOQUEADA",
                "Muitas tentativas sem sucesso. Tente novamente em " + minutos + " min.");
    }

    private SessaoResponse sessao(Usuario usuario) {
        TokenEmitido token = tokenService.emitir(usuario);
        return new SessaoResponse(token.token(), token.expiraEm(), DtoMapper.usuario(usuario));
    }
}

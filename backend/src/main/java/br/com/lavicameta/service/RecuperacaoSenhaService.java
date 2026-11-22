package br.com.lavicameta.service;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.entity.CodigoRecuperacaoSenha;
import br.com.lavicameta.entity.Usuario;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.CodigoRecuperacaoSenhaRepository;
import br.com.lavicameta.repository.UsuarioRepository;
import br.com.lavicameta.util.Contatos;
import br.com.lavicameta.util.Hashes;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Recuperação de senha por código de uso único enviado por e-mail (RF04, CT05).
 * A resposta não revela se o e-mail está cadastrado.
 */
@Service
public class RecuperacaoSenhaService {

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TAMANHO_CODIGO = 8;
    private static final Duration INTERVALO_MINIMO_ENVIO = Duration.ofSeconds(60);
    private static final String MSG_CODIGO_INVALIDO = "Código inválido ou expirado. Solicite um novo código.";

    private final UsuarioRepository usuarios;
    private final CodigoRecuperacaoSenhaRepository codigos;
    private final NotificadorRecuperacaoSenha notificador;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final Duration validade;
    private final int maxTentativas;
    private final SecureRandom aleatorio = new SecureRandom();

    public RecuperacaoSenhaService(UsuarioRepository usuarios, CodigoRecuperacaoSenhaRepository codigos,
                                   NotificadorRecuperacaoSenha notificador, PasswordEncoder passwordEncoder,
                                   Clock clock, LavicametaProperties propriedades) {
        this.usuarios = usuarios;
        this.codigos = codigos;
        this.notificador = notificador;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.validade = Duration.ofMinutes(propriedades.recuperacaoSenha().validadeMinutos());
        this.maxTentativas = propriedades.recuperacaoSenha().maxTentativas();
    }

    @Transactional
    public void solicitar(String emailInformado) {
        if (!notificador.disponivel()) {
            throw NotificadorRecuperacaoSenha.indisponivel();
        }
        Optional<Usuario> encontrado = usuarios.findByEmail(Contatos.normalizarEmail(emailInformado))
                .filter(u -> !u.isAdmin());
        if (encontrado.isEmpty()) {
            return;
        }
        Usuario usuario = encontrado.get();
        Instant agora = clock.instant();
        boolean enviadoRecentemente = codigos.findFirstByUsuarioOrderByCriadoEmDesc(usuario)
                .map(c -> Duration.between(c.getCriadoEm(), agora).compareTo(INTERVALO_MINIMO_ENVIO) < 0)
                .orElse(false);
        if (enviadoRecentemente) {
            return;
        }
        codigos.findByUsuarioAndUsadoEmIsNull(usuario).forEach(c -> c.invalidar(agora));
        String codigo = gerarCodigo();
        codigos.save(new CodigoRecuperacaoSenha(usuario, Hashes.sha256Hex(codigo), agora.plus(validade), agora));
        notificador.enviar(usuario.getEmail(), usuario.getNome(), codigo);
    }

    @Transactional(noRollbackFor = ApiException.class)
    public void redefinir(String emailInformado, String codigoInformado, String novaSenha) {
        Instant agora = clock.instant();
        Usuario usuario = usuarios.findByEmail(Contatos.normalizarEmail(emailInformado))
                .filter(u -> !u.isAdmin())
                .orElseThrow(() -> ApiException.invalido("CODIGO_INVALIDO", MSG_CODIGO_INVALIDO));
        CodigoRecuperacaoSenha codigo = codigos.findFirstByUsuarioOrderByCriadoEmDesc(usuario)
                .filter(c -> c.podeSerUsado(agora, maxTentativas))
                .orElseThrow(() -> ApiException.invalido("CODIGO_INVALIDO", MSG_CODIGO_INVALIDO));
        String hashInformado = Hashes.sha256Hex(codigoInformado.trim().toUpperCase(Locale.ROOT));
        if (!Hashes.iguaisEmTempoConstante(hashInformado, codigo.getCodigoHash())) {
            codigo.registrarTentativaInvalida();
            throw ApiException.invalido("CODIGO_INVALIDO", MSG_CODIGO_INVALIDO);
        }
        codigo.marcarUsado(agora);
        usuario.definirSenhaHash(passwordEncoder.encode(novaSenha), agora);
    }

    private String gerarCodigo() {
        StringBuilder sb = new StringBuilder(TAMANHO_CODIGO);
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            sb.append(ALFABETO.charAt(aleatorio.nextInt(ALFABETO.length())));
        }
        return sb.toString();
    }
}

package br.com.lavicameta.service;

import br.com.lavicameta.dto.AuthDtos.AlterarSenhaRequest;
import br.com.lavicameta.dto.AuthDtos.AtualizarPerfilRequest;
import br.com.lavicameta.dto.AuthDtos.UsuarioResponse;
import br.com.lavicameta.dto.DtoMapper;
import br.com.lavicameta.entity.Usuario;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.UsuarioRepository;
import br.com.lavicameta.util.Contatos;
import java.time.Clock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Perfil do cliente (RF16, UC11). */
@Service
public class PerfilService {

    private final UsuarioRepository usuarios;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public PerfilService(UsuarioRepository usuarios, UsuarioService usuarioService, PasswordEncoder passwordEncoder,
                         Clock clock) {
        this.usuarios = usuarios;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obter(String usuarioId) {
        return DtoMapper.usuario(usuarioService.buscar(usuarioId));
    }

    @Transactional
    public UsuarioResponse atualizar(String usuarioId, AtualizarPerfilRequest req) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        String email = Contatos.normalizarEmail(req.email());
        String telefone = Contatos.normalizarTelefone(req.telefone());
        if (email != null && usuarios.existsByEmailAndIdNot(email, usuarioId)) {
            throw ApiException.conflito("EMAIL_EM_USO", "Este e-mail já está cadastrado.");
        }
        if (telefone != null && usuarios.existsByTelefoneAndIdNot(telefone, usuarioId)) {
            throw ApiException.conflito("TELEFONE_EM_USO", "Este telefone já está cadastrado.");
        }
        usuario.atualizarPerfil(req.nome().trim(), email, telefone, clock.instant());
        return DtoMapper.usuario(usuario);
    }

    @Transactional
    public void alterarSenha(String usuarioId, AlterarSenhaRequest req) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        if (usuario.getSenhaHash() != null
                && (req.senhaAtual() == null || !passwordEncoder.matches(req.senhaAtual(), usuario.getSenhaHash()))) {
            throw ApiException.invalido("SENHA_ATUAL_INCORRETA", "A senha atual está incorreta.");
        }
        usuario.definirSenhaHash(passwordEncoder.encode(req.novaSenha()), clock.instant());
    }
}

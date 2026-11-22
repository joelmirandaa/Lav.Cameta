package br.com.lavicameta.service;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.entity.TipoUsuario;
import br.com.lavicameta.entity.Usuario;
import br.com.lavicameta.repository.UsuarioRepository;
import br.com.lavicameta.util.Contatos;
import java.time.Clock;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria o primeiro administrador a partir de variáveis de ambiente quando não há nenhum.
 * Não existe senha padrão: sem ADMIN_BOOTSTRAP_EMAIL/PASSWORD nada é criado.
 */
@Component
public class AdminInicialService implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AdminInicialService.class);
    private static final int TAMANHO_MINIMO_SENHA = 10;

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final LavicametaProperties.AdminInicial config;

    public AdminInicialService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder, Clock clock,
                               LavicametaProperties propriedades) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.config = propriedades.adminInicial();
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarios.existsByTipo(TipoUsuario.ADMIN)) {
            return;
        }
        String email = Contatos.normalizarEmail(config.email());
        String senha = config.senha();
        if (email == null || senha == null || senha.isBlank()) {
            LOG.warn("Nenhum administrador cadastrado. Defina ADMIN_BOOTSTRAP_EMAIL e ADMIN_BOOTSTRAP_PASSWORD.");
            return;
        }
        if (senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalStateException("ADMIN_BOOTSTRAP_PASSWORD deve ter ao menos 10 caracteres.");
        }
        if (usuarios.existsByEmail(email)) {
            throw new IllegalStateException("ADMIN_BOOTSTRAP_EMAIL já pertence a uma conta de cliente.");
        }
        Instant agora = clock.instant();
        Usuario admin = new Usuario(config.nome(), email, null, TipoUsuario.ADMIN, agora);
        admin.definirSenhaHash(passwordEncoder.encode(senha), agora);
        usuarios.save(admin);
        LOG.info("Administrador inicial criado.");
    }
}

package br.com.lavicameta.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.lavicameta.entity.TipoUsuario;
import br.com.lavicameta.entity.Usuario;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/** CT03: bloqueio por 15 minutos após 5 senhas erradas. */
class UsuarioBloqueioTest {

    private final Instant agora = Instant.parse("2026-11-20T12:00:00Z");

    @Test
    void bloqueiaNaQuintaFalhaEPorQuinzeMinutos() {
        Usuario usuario = new Usuario("Ana", "ana@x.com", null, TipoUsuario.CLIENTE, agora);
        for (int i = 0; i < 4; i++) {
            usuario.registrarFalhaLogin(5, Duration.ofMinutes(15), agora);
            assertFalse(usuario.estaBloqueado(agora));
        }
        usuario.registrarFalhaLogin(5, Duration.ofMinutes(15), agora);
        assertTrue(usuario.estaBloqueado(agora.plusSeconds(14 * 60)));
        assertFalse(usuario.estaBloqueado(agora.plusSeconds(15 * 60)));
    }

    @Test
    void loginComSucessoZeraTentativas() {
        Usuario usuario = new Usuario("Ana", "ana@x.com", null, TipoUsuario.CLIENTE, agora);
        usuario.registrarFalhaLogin(5, Duration.ofMinutes(15), agora);
        usuario.registrarLoginComSucesso(agora);
        for (int i = 0; i < 4; i++) {
            usuario.registrarFalhaLogin(5, Duration.ofMinutes(15), agora);
        }
        assertFalse(usuario.estaBloqueado(agora));
    }
}

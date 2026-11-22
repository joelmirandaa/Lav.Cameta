package br.com.lavicameta.config;

import java.time.ZoneId;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lavicameta")
public record LavicametaProperties(
        String zonaHoraria,
        Seguranca seguranca,
        AdminInicial adminInicial,
        RecuperacaoSenha recuperacaoSenha,
        Pedidos pedidos,
        Pagamento pagamento,
        Maquinas maquinas) {

    public ZoneId zona() {
        return ZoneId.of(zonaHoraria);
    }

    public record Seguranca(String jwtSegredo, long jwtValidadeMinutos, int maxTentativasLogin,
                            long bloqueioMinutos, String googleClientId) {
    }

    public record AdminInicial(String nome, String email, String senha) {
    }

    public record RecuperacaoSenha(String remetente, long validadeMinutos, int maxTentativas) {
    }

    public record Pedidos(long reservaMinutos) {
    }

    public record Pagamento(String provedor, String webhookSegredo, String urlPublica) {
    }

    public record Maquinas(String controlador) {
    }
}

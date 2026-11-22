package br.com.lavicameta.security;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.exception.ApiException;
import java.util.List;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

/** Valida ID tokens do Google (RF03) pela assinatura JWKS, emissor e audiência. */
@Component
public class GoogleTokenVerifier {

    private static final String JWKS_GOOGLE = "https://www.googleapis.com/oauth2/v3/certs";
    private static final List<String> EMISSORES = List.of("accounts.google.com", "https://accounts.google.com");

    private final String clientId;
    private volatile JwtDecoder decoder;

    public GoogleTokenVerifier(LavicametaProperties propriedades) {
        this.clientId = propriedades.seguranca().googleClientId();
    }

    public boolean habilitado() {
        return clientId != null && !clientId.isBlank();
    }

    public IdentidadeGoogle verificar(String idToken) {
        if (!habilitado()) {
            throw ApiException.indisponivel("GOOGLE_DESATIVADO", "Login com Google não está configurado.");
        }
        try {
            Jwt jwt = decoder().decode(idToken);
            Boolean emailVerificado = jwt.getClaimAsBoolean("email_verified");
            String email = Boolean.TRUE.equals(emailVerificado) ? jwt.getClaimAsString("email") : null;
            String nome = jwt.getClaimAsString("name");
            return new IdentidadeGoogle(jwt.getSubject(), email, nome);
        } catch (JwtException ex) {
            throw ApiException.naoAutorizado("GOOGLE_TOKEN_INVALIDO", "Não foi possível validar a conta Google.");
        }
    }

    private JwtDecoder decoder() {
        if (decoder == null) {
            NimbusJwtDecoder novo = NimbusJwtDecoder.withJwkSetUri(JWKS_GOOGLE).build();
            OAuth2TokenValidator<Jwt> emissor = jwt -> EMISSORES.contains(jwt.getClaimAsString("iss"))
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Emissor inválido", null));
            OAuth2TokenValidator<Jwt> audiencia = jwt -> jwt.getAudience() != null && jwt.getAudience().contains(clientId)
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Audiência inválida", null));
            novo.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(), emissor, audiencia));
            decoder = novo;
        }
        return decoder;
    }

    public record IdentidadeGoogle(String googleId, String email, String nome) {
    }
}

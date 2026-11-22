package br.com.lavicameta.security;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.entity.Usuario;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final String EMISSOR = "lavicameta";

    private final JwtEncoder encoder;
    private final Clock clock;
    private final Duration validade;

    public TokenService(JwtEncoder encoder, Clock clock, LavicametaProperties propriedades) {
        this.encoder = encoder;
        this.clock = clock;
        this.validade = Duration.ofMinutes(propriedades.seguranca().jwtValidadeMinutos());
    }

    public TokenEmitido emitir(Usuario usuario) {
        Instant agora = clock.instant();
        Instant expira = agora.plus(validade);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(EMISSOR)
                .subject(usuario.getId())
                .issuedAt(agora)
                .expiresAt(expira)
                .claim(SecurityConfig.CLAIM_PAPEL, usuario.getTipo().name())
                .build();
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
        return new TokenEmitido(token, expira);
    }

    public record TokenEmitido(String token, Instant expiraEm) {
    }
}

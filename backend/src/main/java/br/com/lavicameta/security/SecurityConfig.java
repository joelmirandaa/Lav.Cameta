package br.com.lavicameta.security;

import br.com.lavicameta.config.LavicametaProperties;
import br.com.lavicameta.dto.ErroResponse;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    static final String CLAIM_PAPEL = "papel";
    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JsonMapper jsonMapper) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/health", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**", "/api/v1/admin/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/payments/webhook/**").permitAll()
                        .requestMatchers("/api/v1/sandbox/checkout/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/machines/**", "/api/v1/services").authenticated()
                        .requestMatchers("/api/v1/**").hasRole("CLIENTE")
                        .anyRequest().denyAll())
                .oauth2ResourceServer(rs -> rs
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(conversorPapeis()))
                        .authenticationEntryPoint((req, res, ex) -> escreverErro(res, jsonMapper,
                                HttpServletResponse.SC_UNAUTHORIZED, "NAO_AUTENTICADO",
                                "Sessão inválida ou expirada. Entre novamente."))
                        .accessDeniedHandler((req, res, ex) -> escreverErro(res, jsonMapper,
                                HttpServletResponse.SC_FORBIDDEN, "ACESSO_NEGADO",
                                "Você não tem permissão para acessar este recurso.")))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> escreverErro(res, jsonMapper,
                                HttpServletResponse.SC_UNAUTHORIZED, "NAO_AUTENTICADO",
                                "Entre na sua conta para continuar."))
                        .accessDeniedHandler((req, res, e) -> escreverErro(res, jsonMapper,
                                HttpServletResponse.SC_FORBIDDEN, "ACESSO_NEGADO",
                                "Você não tem permissão para acessar este recurso.")));
        return http.build();
    }

    private static JwtAuthenticationConverter conversorPapeis() {
        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(jwt -> {
            String papel = jwt.getClaimAsString(CLAIM_PAPEL);
            return papel == null ? List.of() : List.of(new SimpleGrantedAuthority("ROLE_" + papel));
        });
        return conversor;
    }

    private static void escreverErro(HttpServletResponse res, JsonMapper mapper, int status, String codigo,
                                     String mensagem) throws java.io.IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        mapper.writeValue(res.getOutputStream(), ErroResponse.de(codigo, mensagem));
    }

    @Bean
    public SecretKey chaveJwt(LavicametaProperties propriedades) {
        String segredo = propriedades.seguranca().jwtSegredo();
        if (segredo == null || segredo.getBytes(StandardCharsets.UTF_8).length < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalStateException("JWT_SECRET deve ser definido com pelo menos 32 bytes.");
        }
        return new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey chaveJwt) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chaveJwt));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey chaveJwt) {
        return NimbusJwtDecoder.withSecretKey(chaveJwt).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

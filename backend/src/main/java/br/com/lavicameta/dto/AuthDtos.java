package br.com.lavicameta.dto;

import br.com.lavicameta.entity.TipoUsuario;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/** Contratos de autenticação, cadastro e recuperação de senha. */
public final class AuthDtos {

    public static final String REGEX_TELEFONE = "^\\+?[0-9 ()-]{10,20}$";
    public static final String MSG_SENHA = "A senha deve ter de 8 a 72 caracteres, com letras e números.";
    public static final String REGEX_SENHA = "^(?=.*[A-Za-z])(?=.*[0-9]).{8,72}$";

    private AuthDtos() {
    }

    public record RegistroRequest(
            @NotBlank(message = "Informe seu nome.") @Size(max = 120, message = "Nome muito longo.") String nome,
            @Email(message = "E-mail inválido.") @Size(max = 150, message = "E-mail muito longo.") String email,
            @Pattern(regexp = REGEX_TELEFONE, message = "Telefone inválido. Use DDD e número.") String telefone,
            @NotBlank(message = "Informe uma senha.") @Pattern(regexp = REGEX_SENHA, message = MSG_SENHA) String senha) {

        @AssertTrue(message = "Informe e-mail ou telefone.")
        public boolean isContatoInformado() {
            return (email != null && !email.isBlank()) || (telefone != null && !telefone.isBlank());
        }
    }

    public record LoginRequest(
            @NotBlank(message = "Informe e-mail ou telefone.") @Size(max = 150) String identificador,
            @NotBlank(message = "Informe a senha.") @Size(max = 72) String senha) {
    }

    public record GoogleLoginRequest(@NotBlank(message = "Token do Google ausente.") String idToken) {
    }

    public record SolicitarRecuperacaoRequest(
            @NotBlank(message = "Informe seu e-mail.") @Email(message = "E-mail inválido.") String email) {
    }

    public record RedefinirSenhaRequest(
            @NotBlank(message = "Informe seu e-mail.") @Email(message = "E-mail inválido.") String email,
            @NotBlank(message = "Informe o código recebido.") @Size(max = 20) String codigo,
            @NotBlank(message = "Informe a nova senha.") @Pattern(regexp = REGEX_SENHA, message = MSG_SENHA) String novaSenha) {
    }

    public record SessaoResponse(String token, Instant expiraEm, UsuarioResponse usuario) {
    }

    public record UsuarioResponse(String id, String nome, String email, String telefone, TipoUsuario tipo,
                                  boolean possuiSenha, boolean vinculadoGoogle) {
    }

    public record MensagemResponse(String mensagem) {
    }

    public record AtualizarPerfilRequest(
            @NotBlank(message = "Informe seu nome.") @Size(max = 120, message = "Nome muito longo.") String nome,
            @Email(message = "E-mail inválido.") @Size(max = 150, message = "E-mail muito longo.") String email,
            @Pattern(regexp = REGEX_TELEFONE, message = "Telefone inválido. Use DDD e número.") String telefone) {

        @AssertTrue(message = "Informe e-mail ou telefone.")
        public boolean isContatoInformado() {
            return (email != null && !email.isBlank()) || (telefone != null && !telefone.isBlank());
        }
    }

    public record AlterarSenhaRequest(
            @Size(max = 72) String senhaAtual,
            @NotBlank(message = "Informe a nova senha.") @Pattern(regexp = REGEX_SENHA, message = MSG_SENHA) String novaSenha) {
    }
}

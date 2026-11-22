package br.com.lavicameta.dto;

import br.com.lavicameta.entity.StatusMaquina;
import br.com.lavicameta.entity.TipoMaquina;
import br.com.lavicameta.entity.TipoServico;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/** Contratos de máquinas e serviços (cliente e administração). */
public final class CatalogoDtos {

    private CatalogoDtos() {
    }

    public enum FiltroMaquina { LAVAR, SECAR, AMBOS }

    /**
     * {@code reservada} indica reserva de 5 minutos em andamento; {@code contratavel} resume RN02/RN07.
     */
    public record MaquinaResponse(Integer id, String nome, TipoMaquina tipo, BigDecimal capacidadeKg,
                                  StatusMaquina status, Integer minutosRestantes, boolean reservada,
                                  boolean contratavel, List<String> recursos) {
    }

    public record MaquinaDetalheResponse(MaquinaResponse maquina, List<ServicoResponse> servicos) {
    }

    public record ServicoResponse(Integer id, TipoServico nome, String descricao, BigDecimal preco, int duracaoMin) {
    }

    public record MaquinaRequest(
            @NotBlank(message = "Informe o nome.") @Size(max = 50, message = "Nome muito longo.") String nome,
            @NotNull(message = "Informe o tipo.") TipoMaquina tipo,
            @NotNull(message = "Informe a capacidade.")
            @DecimalMin(value = "0.1", message = "Capacidade deve ser maior que zero.")
            @DecimalMax(value = "999.9", message = "Capacidade muito alta.")
            @Digits(integer = 3, fraction = 1, message = "Use no máximo uma casa decimal.") BigDecimal capacidadeKg,
            @Size(max = 200, message = "Recursos: no máximo 200 caracteres.") String recursos) {
    }

    public record StatusMaquinaRequest(@NotNull(message = "Informe o status.") StatusMaquina status) {
    }

    public record ServicoRequest(
            @NotBlank(message = "Informe a descrição.") @Size(max = 200, message = "Descrição muito longa.") String descricao,
            @NotNull(message = "Informe o preço.")
            @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
            @Digits(integer = 6, fraction = 2, message = "Preço inválido.") BigDecimal preco,
            @Min(value = 1, message = "Duração mínima de 1 minuto.")
            @Max(value = 600, message = "Duração máxima de 600 minutos.") int duracaoMin) {
    }
}

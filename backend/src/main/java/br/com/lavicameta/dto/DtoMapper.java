package br.com.lavicameta.dto;

import br.com.lavicameta.dto.AuthDtos.UsuarioResponse;
import br.com.lavicameta.dto.CatalogoDtos.MaquinaResponse;
import br.com.lavicameta.dto.CatalogoDtos.ServicoResponse;
import br.com.lavicameta.dto.PedidoDtos.CicloResponse;
import br.com.lavicameta.dto.PedidoDtos.MetodoSalvoResponse;
import br.com.lavicameta.dto.PedidoDtos.PagamentoResponse;
import br.com.lavicameta.entity.Ciclo;
import br.com.lavicameta.entity.Maquina;
import br.com.lavicameta.entity.MetodoPagamentoSalvo;
import br.com.lavicameta.entity.Pagamento;
import br.com.lavicameta.entity.Servico;
import br.com.lavicameta.entity.StatusMaquina;
import br.com.lavicameta.entity.Usuario;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/** Conversões entidade → DTO; impede exposição de campos internos (senha, tokens). */
public final class DtoMapper {

    private DtoMapper() {
    }

    public static UsuarioResponse usuario(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.getTipo(),
                u.getSenhaHash() != null, u.getGoogleId() != null);
    }

    public static MaquinaResponse maquina(Maquina m, boolean reservada, Integer minutosRestantes) {
        boolean contratavel = m.getStatus() == StatusMaquina.DISPONIVEL && !reservada;
        Integer minutos = m.getStatus() == StatusMaquina.EM_USO ? minutosRestantes : null;
        return new MaquinaResponse(m.getId(), m.getNome(), m.getTipo(), m.getCapacidadeKg(), m.getStatus(),
                minutos, reservada, contratavel, recursos(m.getRecursos()));
    }

    public static List<String> recursos(String recursos) {
        if (recursos == null || recursos.isBlank()) {
            return List.of();
        }
        return Arrays.stream(recursos.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    public static ServicoResponse servico(Servico s) {
        return new ServicoResponse(s.getId(), s.getNome(), s.getDescricao(), s.getPreco(), s.getDuracaoMin());
    }

    public static PagamentoResponse pagamento(Pagamento p) {
        if (p == null) {
            return null;
        }
        return new PagamentoResponse(p.getId(), p.getMetodo(), p.getStatus(), p.getValor(), p.getCodigoPix(),
                p.getCheckoutUrl(), p.getMotivo(), p.getPagoEm(), p.getCriadoEm());
    }

    public static CicloResponse ciclo(Ciclo c, Instant agora) {
        return new CicloResponse(c.getId(), c.getEtapa(), c.getStatus(), c.getMaquina().getId(),
                c.getMaquina().getNome(), c.getInicioEm(), c.getFimPrevistoEm(), c.getFimEm(),
                c.minutosRestantes(agora));
    }

    public static MetodoSalvoResponse metodoSalvo(MetodoPagamentoSalvo m) {
        return new MetodoSalvoResponse(m.getId(), m.getBandeira(), m.getUltimos4(), m.getCriadoEm());
    }
}

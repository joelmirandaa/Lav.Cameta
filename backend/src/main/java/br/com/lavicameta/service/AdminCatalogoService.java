package br.com.lavicameta.service;

import br.com.lavicameta.dto.CatalogoDtos.MaquinaRequest;
import br.com.lavicameta.dto.CatalogoDtos.ServicoRequest;
import br.com.lavicameta.dto.CatalogoDtos.ServicoResponse;
import br.com.lavicameta.dto.DtoMapper;
import br.com.lavicameta.entity.Maquina;
import br.com.lavicameta.entity.Servico;
import br.com.lavicameta.entity.StatusMaquina;
import br.com.lavicameta.entity.StatusPedido;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.MaquinaRepository;
import br.com.lavicameta.repository.PedidoRepository;
import br.com.lavicameta.repository.ServicoRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Gestão de máquinas e preços pelo administrador (UC15, RN01). */
@Service
public class AdminCatalogoService {

    private final MaquinaRepository maquinas;
    private final ServicoRepository servicos;
    private final PedidoRepository pedidos;
    private final Clock clock;

    public AdminCatalogoService(MaquinaRepository maquinas, ServicoRepository servicos, PedidoRepository pedidos,
                                Clock clock) {
        this.maquinas = maquinas;
        this.servicos = servicos;
        this.pedidos = pedidos;
        this.clock = clock;
    }

    @Transactional
    public Integer criarMaquina(MaquinaRequest req) {
        String nome = req.nome().trim();
        if (maquinas.existsByNomeIgnoreCase(nome)) {
            throw ApiException.conflito("NOME_EM_USO", "Já existe uma máquina com este nome.");
        }
        Maquina maquina = new Maquina(nome, req.tipo(), req.capacidadeKg(), limpar(req.recursos()), clock.instant());
        return maquinas.save(maquina).getId();
    }

    @Transactional
    public void atualizarMaquina(Integer id, MaquinaRequest req) {
        Maquina maquina = maquinas.findByIdForUpdate(id)
                .orElseThrow(() -> ApiException.naoEncontrado("Máquina não encontrada."));
        String nome = req.nome().trim();
        if (maquinas.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw ApiException.conflito("NOME_EM_USO", "Já existe uma máquina com este nome.");
        }
        if (maquina.getTipo() != req.tipo() && possuiPedidoAtivo(id)) {
            throw ApiException.conflito("MAQUINA_OCUPADA", "Não é possível mudar o tipo de uma máquina com pedido ativo.");
        }
        maquina.atualizarCadastro(nome, req.tipo(), req.capacidadeKg(), limpar(req.recursos()), clock.instant());
    }

    /** Somente Manutenção ⇄ Disponível; "Em uso" é controlado pelo ciclo. */
    @Transactional
    public void alterarStatus(Integer id, StatusMaquina novoStatus) {
        if (novoStatus == StatusMaquina.EM_USO) {
            throw ApiException.invalido("STATUS_INVALIDO", "O status \"Em uso\" é definido automaticamente pelo ciclo.");
        }
        Maquina maquina = maquinas.findByIdForUpdate(id)
                .orElseThrow(() -> ApiException.naoEncontrado("Máquina não encontrada."));
        if (maquina.getStatus() == StatusMaquina.EM_USO) {
            throw ApiException.conflito("MAQUINA_EM_USO", "Aguarde o fim do ciclo para alterar o status.");
        }
        Instant agora = clock.instant();
        if (novoStatus == StatusMaquina.MANUTENCAO && pedidos.existsReservaVigente(id, agora)) {
            throw ApiException.conflito("MAQUINA_RESERVADA",
                    "A máquina está reservada para um pagamento em andamento. Tente em alguns minutos.");
        }
        maquina.alterarStatus(novoStatus, agora);
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listarServicos() {
        return servicos.findAllByOrderByIdAsc().stream().map(DtoMapper::servico).toList();
    }

    /** Novo preço vale para pedidos futuros; pedidos já criados mantêm o valor fixado (RF09). */
    @Transactional
    public ServicoResponse atualizarServico(Integer id, ServicoRequest req) {
        Servico servico = servicos.findById(id).orElseThrow(() -> ApiException.naoEncontrado("Serviço não encontrado."));
        servico.atualizar(req.descricao().trim(), req.preco(), req.duracaoMin());
        return DtoMapper.servico(servico);
    }

    private boolean possuiPedidoAtivo(Integer maquinaId) {
        return pedidos.existsByMaquinaIdAndStatusIn(maquinaId,
                EnumSet.of(StatusPedido.AGUARDANDO_PAGAMENTO, StatusPedido.PAGO, StatusPedido.EM_ANDAMENTO));
    }

    private static String limpar(String recursos) {
        return recursos == null || recursos.isBlank() ? null : recursos.trim();
    }
}

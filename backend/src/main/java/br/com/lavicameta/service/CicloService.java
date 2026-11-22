package br.com.lavicameta.service;

import br.com.lavicameta.dto.PedidoDtos.PedidoResponse;
import br.com.lavicameta.entity.Ciclo;
import br.com.lavicameta.entity.ComandoMaquina;
import br.com.lavicameta.entity.EtapaCiclo;
import br.com.lavicameta.entity.Maquina;
import br.com.lavicameta.entity.Pedido;
import br.com.lavicameta.entity.StatusCiclo;
import br.com.lavicameta.entity.StatusComando;
import br.com.lavicameta.entity.StatusMaquina;
import br.com.lavicameta.entity.StatusPedido;
import br.com.lavicameta.entity.TipoMaquina;
import br.com.lavicameta.entity.TipoServico;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.exception.IntegracaoException;
import br.com.lavicameta.integration.machine.MachineController;
import br.com.lavicameta.repository.CicloRepository;
import br.com.lavicameta.repository.ComandoMaquinaRepository;
import br.com.lavicameta.repository.MaquinaRepository;
import br.com.lavicameta.repository.PedidoRepository;
import br.com.lavicameta.repository.ServicoRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Início automático, acompanhamento e encerramento dos ciclos (RF13, UC08, RN04, RN06, RN08). */
@Service
public class CicloService {

    private static final Logger LOG = LoggerFactory.getLogger(CicloService.class);

    private final PedidoRepository pedidos;
    private final MaquinaRepository maquinas;
    private final ServicoRepository servicos;
    private final CicloRepository ciclos;
    private final ComandoMaquinaRepository comandos;
    private final MachineController controlador;
    private final EstornoService estornoService;
    private final PedidoConsultaService consulta;
    private final Clock clock;

    public CicloService(PedidoRepository pedidos, MaquinaRepository maquinas, ServicoRepository servicos,
                        CicloRepository ciclos, ComandoMaquinaRepository comandos, MachineController controlador,
                        EstornoService estornoService, PedidoConsultaService consulta, Clock clock) {
        this.pedidos = pedidos;
        this.maquinas = maquinas;
        this.servicos = servicos;
        this.ciclos = ciclos;
        this.comandos = comandos;
        this.controlador = controlador;
        this.estornoService = estornoService;
        this.consulta = consulta;
        this.clock = clock;
    }

    /**
     * Inicia a primeira etapa de um pedido pago. Idempotente: se o comando já foi confirmado ou o
     * pedido não está mais PAGO, nada é feito. Falha no equipamento gera estorno integral (RN06).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void iniciarPrimeiraEtapa(String pedidoId) {
        Pedido pedido = pedidos.findByIdForUpdate(pedidoId).orElse(null);
        if (pedido == null || pedido.getStatus() != StatusPedido.PAGO) {
            return;
        }
        EtapaCiclo etapa = pedido.getServico().getNome().etapaInicial();
        String chave = chave(pedido, etapa);
        if (comandos.existsByChaveIdempotencia(chave)) {
            return;
        }
        Maquina maquina = maquinas.findByIdForUpdate(pedido.getMaquina().getId()).orElseThrow();
        Instant agora = clock.instant();
        if (maquina.getStatus() != StatusMaquina.DISPONIVEL) {
            registrarFalha(pedido, maquina, etapa, "Máquina em " + maquina.getStatus() + " no momento do início", agora);
            estornoService.solicitar(pedido, "Máquina indisponível após o pagamento", agora);
            return;
        }
        int duracao = duracaoEtapa(pedido, etapa);
        try {
            controlador.iniciarCiclo(maquina, duracao, chave);
        } catch (IntegracaoException ex) {
            LOG.error("Falha ao iniciar {} para o pedido {}: {}", maquina.getNome(), pedidoId, ex.getMessage());
            registrarFalha(pedido, maquina, etapa, ex.getMessage(), agora);
            estornoService.solicitar(pedido, "Falha ao iniciar a máquina", agora);
            return;
        }
        registrarInicio(pedido, maquina, etapa, duracao, chave, agora);
    }

    /** Combo (RN08): após a lavagem, o cliente inicia a secagem; o sistema escolhe uma secadora livre. */
    @Transactional(noRollbackFor = ApiException.class)
    public PedidoResponse iniciarSecagem(String usuarioId, String pedidoId) {
        consulta.buscarDoUsuario(usuarioId, pedidoId);
        Pedido pedido = pedidos.findByIdForUpdate(pedidoId).orElseThrow();
        if (!PedidoConsultaService.podeIniciarSecagem(pedido, ciclos.findByPedidoOrderByInicioEmAsc(pedido))) {
            throw ApiException.conflito("SECAGEM_NAO_PERMITIDA", "A secagem só pode começar após a lavagem do combo.");
        }
        Instant agora = clock.instant();
        Maquina secadora = maquinas.findByTipoAndStatusForUpdate(TipoMaquina.SECADORA, StatusMaquina.DISPONIVEL)
                .stream()
                .filter(m -> !pedidos.existsReservaVigente(m.getId(), agora))
                .findFirst()
                .orElseThrow(() -> ApiException.conflito("SEM_SECADORA_DISPONIVEL",
                        "Nenhuma secadora livre agora. Tente novamente em alguns minutos."));
        String chave = chave(pedido, EtapaCiclo.SECAGEM);
        int duracao = duracaoEtapa(pedido, EtapaCiclo.SECAGEM);
        try {
            controlador.iniciarCiclo(secadora, duracao, chave);
        } catch (IntegracaoException ex) {
            LOG.error("Falha ao iniciar secagem do pedido {}: {}", pedidoId, ex.getMessage());
            registrarFalha(pedido, secadora, EtapaCiclo.SECAGEM, ex.getMessage(), agora);
            throw ApiException.indisponivel("FALHA_SECADORA",
                    "Não foi possível iniciar a secadora. Tente novamente; se persistir, procure o atendimento.");
        }
        registrarInicio(pedido, secadora, EtapaCiclo.SECAGEM, duracao, chave, agora);
        return consulta.converter(pedido);
    }

    /** Encerra ciclos cujo tempo terminou: máquina volta a "Disponível" (UC08 passo 4). */
    @Transactional
    public void finalizarSeVencido(String cicloId) {
        Ciclo ciclo = ciclos.findById(cicloId).orElse(null);
        Instant agora = clock.instant();
        if (ciclo == null || ciclo.getStatus() != StatusCiclo.EM_ANDAMENTO || ciclo.getFimPrevistoEm().isAfter(agora)) {
            return;
        }
        Pedido pedido = pedidos.findByIdForUpdate(ciclo.getPedido().getId()).orElseThrow();
        Maquina maquina = maquinas.findByIdForUpdate(ciclo.getMaquina().getId()).orElseThrow();
        ciclo.concluir(agora);
        maquina.finalizarCiclo(agora);
        boolean aguardaSecagem = ciclo.getEtapa() == EtapaCiclo.LAVAGEM
                && pedido.getServico().getNome().incluiSecagemPosterior();
        if (!aguardaSecagem && pedido.getStatus() == StatusPedido.EM_ANDAMENTO) {
            pedido.concluir(agora);
        }
    }

    @Transactional
    public void atualizarMinutosRestantes() {
        Instant agora = clock.instant();
        for (Ciclo ciclo : ciclos.findByStatus(StatusCiclo.EM_ANDAMENTO)) {
            ciclo.getMaquina().atualizarMinutosRestantes(ciclo.minutosRestantes(agora), agora);
        }
    }

    @Transactional(readOnly = true)
    public List<String> ciclosVencidos() {
        return ciclos.findByStatusAndFimPrevistoEmLessThanEqual(StatusCiclo.EM_ANDAMENTO, clock.instant())
                .stream().map(Ciclo::getId).toList();
    }

    @Transactional(readOnly = true)
    public List<String> pedidosPagosSemCiclo() {
        return pedidos.findIdsByStatus(StatusPedido.PAGO);
    }

    private void registrarInicio(Pedido pedido, Maquina maquina, EtapaCiclo etapa, int duracao, String chave,
                                 Instant agora) {
        Ciclo ciclo = ciclos.save(new Ciclo(pedido, maquina, etapa, duracao, agora));
        comandos.save(new ComandoMaquina(ciclo.getId(), maquina.getId(), ComandoMaquina.Tipo.INICIAR,
                StatusComando.CONFIRMADO, chave, null, agora));
        maquina.iniciarCiclo(duracao, agora);
        if (pedido.getStatus() == StatusPedido.PAGO) {
            pedido.iniciarAndamento(agora);
        }
    }

    private void registrarFalha(Pedido pedido, Maquina maquina, EtapaCiclo etapa, String erro, Instant agora) {
        comandos.save(new ComandoMaquina(null, maquina.getId(), ComandoMaquina.Tipo.INICIAR, StatusComando.FALHOU,
                chave(pedido, etapa) + ":falha:" + UUID.randomUUID(), erro, agora));
    }

    private int duracaoEtapa(Pedido pedido, EtapaCiclo etapa) {
        TipoServico tipo = pedido.getServico().getNome();
        if (tipo != TipoServico.LAVAR_SECAR) {
            return pedido.getServico().getDuracaoMin();
        }
        TipoServico referencia = etapa == EtapaCiclo.LAVAGEM ? TipoServico.LAVAR : TipoServico.SECAR;
        return servicos.findByNome(referencia).orElseThrow().getDuracaoMin();
    }

    private static String chave(Pedido pedido, EtapaCiclo etapa) {
        return "pedido:" + pedido.getId() + ":" + etapa;
    }
}

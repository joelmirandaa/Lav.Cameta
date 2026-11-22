package br.com.lavicameta.service;

import br.com.lavicameta.dto.CatalogoDtos.FiltroMaquina;
import br.com.lavicameta.dto.CatalogoDtos.MaquinaDetalheResponse;
import br.com.lavicameta.dto.CatalogoDtos.MaquinaResponse;
import br.com.lavicameta.dto.CatalogoDtos.ServicoResponse;
import br.com.lavicameta.dto.DtoMapper;
import br.com.lavicameta.entity.Ciclo;
import br.com.lavicameta.entity.Maquina;
import br.com.lavicameta.entity.StatusCiclo;
import br.com.lavicameta.entity.TipoMaquina;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.CicloRepository;
import br.com.lavicameta.repository.MaquinaRepository;
import br.com.lavicameta.repository.PedidoRepository;
import br.com.lavicameta.repository.ServicoRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta de máquinas, status em tempo real e serviços (RF05–RF09, UC04). */
@Service
public class CatalogoService {

    private final MaquinaRepository maquinas;
    private final ServicoRepository servicos;
    private final PedidoRepository pedidos;
    private final CicloRepository ciclos;
    private final Clock clock;

    public CatalogoService(MaquinaRepository maquinas, ServicoRepository servicos, PedidoRepository pedidos,
                           CicloRepository ciclos, Clock clock) {
        this.maquinas = maquinas;
        this.servicos = servicos;
        this.pedidos = pedidos;
        this.ciclos = ciclos;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<MaquinaResponse> listar(FiltroMaquina filtro) {
        List<Maquina> lista = switch (filtro == null ? FiltroMaquina.AMBOS : filtro) {
            case LAVAR -> maquinas.findByTipoOrderByNomeAsc(TipoMaquina.LAVADORA);
            case SECAR -> maquinas.findByTipoOrderByNomeAsc(TipoMaquina.SECADORA);
            case AMBOS -> maquinas.findAllByOrderByTipoAscNomeAsc();
        };
        return converter(lista);
    }

    @Transactional(readOnly = true)
    public MaquinaDetalheResponse detalhar(Integer id) {
        Maquina maquina = maquinas.findById(id).orElseThrow(() -> ApiException.naoEncontrado("Máquina não encontrada."));
        List<ServicoResponse> aplicaveis = servicos.findAllByOrderByIdAsc().stream()
                .filter(s -> s.getNome().tipoMaquinaInicial() == maquina.getTipo())
                .map(DtoMapper::servico)
                .toList();
        return new MaquinaDetalheResponse(converter(List.of(maquina)).getFirst(), aplicaveis);
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listarServicos() {
        return servicos.findAllByOrderByIdAsc().stream().map(DtoMapper::servico).toList();
    }

    private List<MaquinaResponse> converter(List<Maquina> lista) {
        Instant agora = clock.instant();
        Set<Integer> reservadas = new HashSet<>(pedidos.findMaquinasComReservaVigente(agora));
        Map<Integer, Integer> minutos = new HashMap<>();
        for (Ciclo ciclo : ciclos.findByStatus(StatusCiclo.EM_ANDAMENTO)) {
            minutos.put(ciclo.getMaquina().getId(), ciclo.minutosRestantes(agora));
        }
        return lista.stream()
                .map(m -> DtoMapper.maquina(m, reservadas.contains(m.getId()),
                        minutos.getOrDefault(m.getId(), m.getMinutosRestantes())))
                .toList();
    }
}

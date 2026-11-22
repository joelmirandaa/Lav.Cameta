package br.com.lavicameta.service;

import br.com.lavicameta.dto.DtoMapper;
import br.com.lavicameta.dto.PedidoDtos.CarteiraResponse;
import br.com.lavicameta.entity.Usuario;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.MetodoPagamentoSalvoRepository;
import br.com.lavicameta.repository.PagamentoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Carteira do cliente: pagamentos e métodos tokenizados (RF15, UC10). */
@Service
public class CarteiraService {

    private static final int LIMITE_PAGAMENTOS = 50;

    private final PagamentoRepository pagamentos;
    private final MetodoPagamentoSalvoRepository metodos;
    private final UsuarioService usuarioService;

    public CarteiraService(PagamentoRepository pagamentos, MetodoPagamentoSalvoRepository metodos,
                           UsuarioService usuarioService) {
        this.pagamentos = pagamentos;
        this.metodos = metodos;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public CarteiraResponse obter(String usuarioId) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        return new CarteiraResponse(
                pagamentos.findByUsuario(usuario, PageRequest.of(0, LIMITE_PAGAMENTOS)).map(DtoMapper::pagamento).getContent(),
                metodos.findByUsuarioOrderByCriadoEmDesc(usuario).stream().map(DtoMapper::metodoSalvo).toList());
    }

    @Transactional
    public void removerMetodo(String usuarioId, String metodoId) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        metodos.delete(metodos.findByIdAndUsuario(metodoId, usuario)
                .orElseThrow(() -> ApiException.naoEncontrado("Forma de pagamento não encontrada.")));
    }
}

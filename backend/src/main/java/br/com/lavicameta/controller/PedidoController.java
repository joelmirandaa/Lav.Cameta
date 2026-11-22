package br.com.lavicameta.controller;

import br.com.lavicameta.dto.PedidoDtos.CheckoutRequest;
import br.com.lavicameta.dto.PedidoDtos.CriarPedidoRequest;
import br.com.lavicameta.dto.PedidoDtos.PagamentoResponse;
import br.com.lavicameta.dto.PedidoDtos.PaginaResponse;
import br.com.lavicameta.dto.PedidoDtos.PedidoResponse;
import br.com.lavicameta.service.CicloService;
import br.com.lavicameta.service.PagamentoService;
import br.com.lavicameta.service.PedidoConsultaService;
import br.com.lavicameta.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoConsultaService consultaService;
    private final PagamentoService pagamentoService;
    private final CicloService cicloService;

    public PedidoController(PedidoService pedidoService, PedidoConsultaService consultaService,
                            PagamentoService pagamentoService, CicloService cicloService) {
        this.pedidoService = pedidoService;
        this.consultaService = consultaService;
        this.pagamentoService = pagamentoService;
        this.cicloService = cicloService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CriarPedidoRequest req) {
        return pedidoService.criar(jwt.getSubject(), req);
    }

    @GetMapping
    public PaginaResponse<PedidoResponse> historico(@AuthenticationPrincipal Jwt jwt,
                                                    @RequestParam(defaultValue = "0") int pagina,
                                                    @RequestParam(defaultValue = "20") int tamanho) {
        return consultaService.historico(jwt.getSubject(), pagina, tamanho);
    }

    @GetMapping("/{id}")
    public PedidoResponse obter(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        return consultaService.obter(jwt.getSubject(), id);
    }

    @PostMapping("/{id}/cancel")
    public PedidoResponse cancelar(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        return pedidoService.cancelar(jwt.getSubject(), id);
    }

    @PostMapping("/{id}/checkout")
    public PagamentoResponse checkout(@AuthenticationPrincipal Jwt jwt, @PathVariable String id,
                                      @Valid @RequestBody CheckoutRequest req) {
        return pagamentoService.checkout(jwt.getSubject(), id, req.metodo());
    }

    @PostMapping("/{id}/drying")
    public PedidoResponse iniciarSecagem(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        return cicloService.iniciarSecagem(jwt.getSubject(), id);
    }
}

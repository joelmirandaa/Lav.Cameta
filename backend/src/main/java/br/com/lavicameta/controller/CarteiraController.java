package br.com.lavicameta.controller;

import br.com.lavicameta.dto.PedidoDtos.CarteiraResponse;
import br.com.lavicameta.service.CarteiraService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallet")
public class CarteiraController {

    private final CarteiraService carteiraService;

    public CarteiraController(CarteiraService carteiraService) {
        this.carteiraService = carteiraService;
    }

    @GetMapping
    public CarteiraResponse obter(@AuthenticationPrincipal Jwt jwt) {
        return carteiraService.obter(jwt.getSubject());
    }

    @DeleteMapping("/methods/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        carteiraService.removerMetodo(jwt.getSubject(), id);
    }
}

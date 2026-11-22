package br.com.lavicameta.controller;

import br.com.lavicameta.dto.AuthDtos.AlterarSenhaRequest;
import br.com.lavicameta.dto.AuthDtos.AtualizarPerfilRequest;
import br.com.lavicameta.dto.AuthDtos.UsuarioResponse;
import br.com.lavicameta.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public UsuarioResponse obter(@AuthenticationPrincipal Jwt jwt) {
        return perfilService.obter(jwt.getSubject());
    }

    @PutMapping
    public UsuarioResponse atualizar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AtualizarPerfilRequest req) {
        return perfilService.atualizar(jwt.getSubject(), req);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AlterarSenhaRequest req) {
        perfilService.alterarSenha(jwt.getSubject(), req);
    }
}

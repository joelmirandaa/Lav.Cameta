package br.com.lavicameta.controller;

import br.com.lavicameta.dto.AuthDtos.GoogleLoginRequest;
import br.com.lavicameta.dto.AuthDtos.LoginRequest;
import br.com.lavicameta.dto.AuthDtos.MensagemResponse;
import br.com.lavicameta.dto.AuthDtos.RedefinirSenhaRequest;
import br.com.lavicameta.dto.AuthDtos.RegistroRequest;
import br.com.lavicameta.dto.AuthDtos.SessaoResponse;
import br.com.lavicameta.dto.AuthDtos.SolicitarRecuperacaoRequest;
import br.com.lavicameta.service.AuthService;
import br.com.lavicameta.service.RecuperacaoSenhaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;
    private final RecuperacaoSenhaService recuperacaoService;

    public AuthController(AuthService authService, RecuperacaoSenhaService recuperacaoService) {
        this.authService = authService;
        this.recuperacaoService = recuperacaoService;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public SessaoResponse registrar(@Valid @RequestBody RegistroRequest req) {
        return authService.registrar(req);
    }

    @PostMapping("/auth/login")
    public SessaoResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.loginCliente(req);
    }

    @PostMapping("/auth/google")
    public SessaoResponse loginGoogle(@Valid @RequestBody GoogleLoginRequest req) {
        return authService.loginGoogle(req);
    }

    @PostMapping("/auth/password-recovery")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MensagemResponse solicitarRecuperacao(@Valid @RequestBody SolicitarRecuperacaoRequest req) {
        recuperacaoService.solicitar(req.email());
        return new MensagemResponse("Se o e-mail estiver cadastrado, você receberá um código em instantes.");
    }

    @PostMapping("/auth/password-reset")
    public MensagemResponse redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest req) {
        recuperacaoService.redefinir(req.email(), req.codigo(), req.novaSenha());
        return new MensagemResponse("Senha alterada. Entre com a nova senha.");
    }

    @PostMapping("/admin/auth/login")
    public SessaoResponse loginAdmin(@Valid @RequestBody LoginRequest req) {
        return authService.loginAdmin(req);
    }
}

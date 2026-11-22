package br.com.lavicameta.controller;

import br.com.lavicameta.dto.CatalogoDtos.FiltroMaquina;
import br.com.lavicameta.dto.CatalogoDtos.MaquinaRequest;
import br.com.lavicameta.dto.CatalogoDtos.MaquinaResponse;
import br.com.lavicameta.dto.CatalogoDtos.ServicoRequest;
import br.com.lavicameta.dto.CatalogoDtos.ServicoResponse;
import br.com.lavicameta.dto.CatalogoDtos.StatusMaquinaRequest;
import br.com.lavicameta.dto.RelatorioDtos.Periodo;
import br.com.lavicameta.dto.RelatorioDtos.RelatorioResponse;
import br.com.lavicameta.service.AdminCatalogoService;
import br.com.lavicameta.service.CatalogoService;
import br.com.lavicameta.service.RelatorioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Área administrativa (RF17–RF21, UC13–UC15). Acesso restrito ao papel ADMIN (RN11). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final RelatorioService relatorioService;
    private final AdminCatalogoService adminCatalogo;
    private final CatalogoService catalogoService;

    public AdminController(RelatorioService relatorioService, AdminCatalogoService adminCatalogo,
                           CatalogoService catalogoService) {
        this.relatorioService = relatorioService;
        this.adminCatalogo = adminCatalogo;
        this.catalogoService = catalogoService;
    }

    @GetMapping("/reports")
    public RelatorioResponse relatorio(@RequestParam(defaultValue = "MES") Periodo periodo,
                                       @RequestParam(required = false) Integer ano,
                                       @RequestParam(required = false) Integer mes) {
        return relatorioService.gerar(periodo, ano, mes);
    }

    @GetMapping("/machines")
    public List<MaquinaResponse> maquinas() {
        return catalogoService.listar(FiltroMaquina.AMBOS);
    }

    @PostMapping("/machines")
    @ResponseStatus(HttpStatus.CREATED)
    public MaquinaResponse criarMaquina(@Valid @RequestBody MaquinaRequest req) {
        Integer id = adminCatalogo.criarMaquina(req);
        return catalogoService.detalhar(id).maquina();
    }

    @PutMapping("/machines/{id}")
    public MaquinaResponse atualizarMaquina(@PathVariable Integer id, @Valid @RequestBody MaquinaRequest req) {
        adminCatalogo.atualizarMaquina(id, req);
        return catalogoService.detalhar(id).maquina();
    }

    @PatchMapping("/machines/{id}/status")
    public MaquinaResponse alterarStatus(@PathVariable Integer id, @Valid @RequestBody StatusMaquinaRequest req) {
        adminCatalogo.alterarStatus(id, req.status());
        return catalogoService.detalhar(id).maquina();
    }

    @GetMapping("/services")
    public List<ServicoResponse> servicos() {
        return adminCatalogo.listarServicos();
    }

    @PutMapping("/services/{id}")
    public ServicoResponse atualizarServico(@PathVariable Integer id, @Valid @RequestBody ServicoRequest req) {
        return adminCatalogo.atualizarServico(id, req);
    }
}

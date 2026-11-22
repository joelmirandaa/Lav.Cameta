package br.com.lavicameta.controller;

import br.com.lavicameta.dto.CatalogoDtos.FiltroMaquina;
import br.com.lavicameta.dto.CatalogoDtos.MaquinaDetalheResponse;
import br.com.lavicameta.dto.CatalogoDtos.MaquinaResponse;
import br.com.lavicameta.dto.CatalogoDtos.ServicoResponse;
import br.com.lavicameta.service.CatalogoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/machines")
    public List<MaquinaResponse> listar(@RequestParam(defaultValue = "AMBOS") FiltroMaquina filtro) {
        return catalogoService.listar(filtro);
    }

    @GetMapping("/machines/{id}")
    public MaquinaDetalheResponse detalhar(@PathVariable Integer id) {
        return catalogoService.detalhar(id);
    }

    @GetMapping("/services")
    public List<ServicoResponse> servicos() {
        return catalogoService.listarServicos();
    }
}

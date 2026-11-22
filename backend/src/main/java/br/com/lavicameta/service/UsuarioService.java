package br.com.lavicameta.service;

import br.com.lavicameta.entity.Usuario;
import br.com.lavicameta.exception.ApiException;
import br.com.lavicameta.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;

    public UsuarioService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public Usuario buscar(String id) {
        return usuarios.findById(id)
                .orElseThrow(() -> ApiException.naoAutorizado("NAO_AUTENTICADO", "Conta não encontrada. Entre novamente."));
    }
}

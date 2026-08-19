package com.henry.auth.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.henry.auth.dto.UsuarioRequest;
import com.henry.auth.dto.UsuarioResponse;
import com.henry.auth.entities.Rol;
import com.henry.auth.entities.Usuario;
import org.springframework.stereotype.Component;


@Component
public class UsuarioMapper {

    public UsuarioResponse entityToResponse(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioResponse(
                usuario.getUsername(),
                usuario.getRoles().stream()
                        .map(Rol::getNombre)
                        .collect(Collectors.toSet())
        );
    }

    public Usuario requestToEntity(UsuarioRequest request, String password, Set<Rol> roles) {
        if (request == null) return null;
        Usuario usuario = new Usuario();
        usuario.setUsername(request.username().trim());
        usuario.setPassword(password.trim());
        usuario.setRoles(roles);
        return usuario;
    }
}


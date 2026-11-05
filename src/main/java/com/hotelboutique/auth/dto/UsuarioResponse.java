package com.hotelboutique.auth.dto;

import com.hotelboutique.auth.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioResponse {
    private String email;
    private String nombre;
    private String preferencias;

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getEmail(), usuario.getNombre(), usuario.getPreferencias());
    }
}
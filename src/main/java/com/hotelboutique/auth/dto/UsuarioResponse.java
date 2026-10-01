package com.hotelboutique.auth.dto;

import com.hotelboutique.auth.entity.Usuario;

public class UsuarioResponse {
    private String email;
    private String nombre;
    private String preferencias;

    // Constructores manuales estándar
    public UsuarioResponse() {}

    public UsuarioResponse(String email, String nombre, String preferencias) {
        this.email = email;
        this.nombre = nombre;
        this.preferencias = preferencias;
    }

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getEmail(), usuario.getNombre(), usuario.getPreferencias());
    }

    // Getters y Setters manuales estándar
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPreferencias() { return preferencias; }
    public void setPreferencias(String preferencias) { this.preferencias = preferencias; }
}

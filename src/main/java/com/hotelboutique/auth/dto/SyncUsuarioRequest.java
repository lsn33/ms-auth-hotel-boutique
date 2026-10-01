package com.hotelboutique.auth.dto;

public class SyncUsuarioRequest {
    private String nombre;
    private String preferencias;

    // Constructor manual estándar
    public SyncUsuarioRequest() {}

    public SyncUsuarioRequest(String nombre, String preferencias) {
        this.nombre = nombre;
        this.preferencias = preferencias;
    }

    // Getters y Setters manuales estándar
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPreferencias() { return preferencias; }
    public void setPreferencias(String preferencias) { this.preferencias = preferencias; }
}

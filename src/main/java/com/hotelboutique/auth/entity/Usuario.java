package com.hotelboutique.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entra_oid", nullable = false, unique = true)
    private String entraOid;

    @Column(nullable = false)
    private String email;

    private String nombre;

    private String preferencias;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    // Constructores manuales para evitar errores
    public Usuario() {}

    public Usuario(Long id, String entraOid, String email, String nombre, String preferencias, LocalDateTime creadoEn) {
        this.id = id;
        this.entraOid = entraOid;
        this.email = email;
        this.nombre = nombre;
        this.preferencias = preferencias;
        this.creadoEn = creadoEn;
    }

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // Getters y Setters manuales estándar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntraOid() { return entraOid; }
    public void setEntraOid(String entraOid) { this.entraOid = entraOid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPreferencias() { return preferencias; }
    public void setPreferencias(String preferencias) { this.preferencias = preferencias; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    // Implementación manual del patrón Builder que reclama UsuarioService
    public static UsuarioBuilder builder() {
        return new UsuarioBuilder();
    }

    public static class UsuarioBuilder {
        private String entraOid;
        private String email;
        private String nombre;
        private String preferencias;

        public UsuarioBuilder entraOid(String entraOid) { this.entraOid = entraOid; return this; }
        public UsuarioBuilder email(String email) { this.email = email; return this; }
        public UsuarioBuilder nombre(String nombre) { this.nombre = nombre; return this; }
        public UsuarioBuilder preferencias(String preferencias) { this.preferencias = preferencias; return this; }

        public Usuario build() {
            Usuario u = new Usuario();
            u.setEntraOid(this.entraOid);
            u.setEmail(this.email);
            u.setNombre(this.nombre);
            u.setPreferencias(this.preferencias);
            return u;
        }
    }
}

package com.hotelboutique.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador unico que entrega Entra ID para cada usuario (Object ID)
    @Column(name = "entra_oid", nullable = false, unique = true)
    private String entraOid;

    @Column(nullable = false)
    private String email;

    private String nombre;

    // Espacio para datos de negocio extra que Entra ID no maneja
    private String preferencias;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }
}
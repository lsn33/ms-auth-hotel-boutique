package com.hotelboutique.auth.dto;

import lombok.Data;

@Data
public class SyncUsuarioRequest {
    private String nombre;
    private String preferencias;
}
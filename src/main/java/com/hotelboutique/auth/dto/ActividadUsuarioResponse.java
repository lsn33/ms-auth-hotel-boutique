package com.hotelboutique.auth.dto;

import com.hotelboutique.auth.entity.ActividadUsuario;

import java.time.LocalDateTime;

public record ActividadUsuarioResponse(
        String email,
        int totalReservas,
        int reservasCanceladas,
        String ultimoEvento,
        LocalDateTime ultimaActividad
) {

    public static ActividadUsuarioResponse desde(ActividadUsuario actividad) {
        return new ActividadUsuarioResponse(actividad.getEmail(), actividad.getTotalReservas(),
                actividad.getReservasCanceladas(), actividad.getUltimoEvento(), actividad.getUltimaActividad());
    }

    public static ActividadUsuarioResponse vacia(String email) {
        return new ActividadUsuarioResponse(email, 0, 0, null, null);
    }
}

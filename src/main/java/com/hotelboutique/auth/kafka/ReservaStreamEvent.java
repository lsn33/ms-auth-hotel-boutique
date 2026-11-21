package com.hotelboutique.auth.kafka;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Evento de reserva que publica ms-reservas en hotel.reservas.eventos.
 * Copia del lado consumidor: ms-auth solo necesita saber quien hizo que, por eso ignora el resto de los campos.
 */
public record ReservaStreamEvent(
        String eventId,
        String tipo,
        Long reservaId,
        String usuarioEmail,
        Long habitacionId,
        LocalDate fechaCheckin,
        LocalDate fechaCheckout,
        String estado,
        Instant ocurridoEn
) {

    public boolean esValido() {
        return tipo != null && !tipo.isBlank()
                && reservaId != null
                && usuarioEmail != null && !usuarioEmail.isBlank();
    }
}

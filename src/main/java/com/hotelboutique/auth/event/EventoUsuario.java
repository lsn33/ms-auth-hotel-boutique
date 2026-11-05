package com.hotelboutique.auth.event;

import java.time.LocalDate;

public record EventoUsuario(
        String tipo,
        Long reservaId,
        String usuarioEmail,
        LocalDate fechaCheckin,
        LocalDate fechaCheckout
) {}

package com.hotelboutique.auth.kafka;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de usuario del topico compactado hotel.usuarios.eventos (clave = email).
 * - eventId: trazabilidad; tipo: que ocurrio; email: identifica al usuario (es tambien la clave del mensaje)
 * - nombre: dato de perfil que necesitan otros servicios; ocurridoEn: cuando se sincronizo
 * Como el topico se compacta, Kafka conserva solo el ultimo evento de cada email.
 */
public record UsuarioStreamEvent(
        String eventId,
        String tipo,
        String email,
        String nombre,
        Instant ocurridoEn
) {

    public static UsuarioStreamEvent sincronizado(String email, String nombre) {
        return new UsuarioStreamEvent(UUID.randomUUID().toString(), "usuario.sincronizado", email, nombre, Instant.now());
    }
}

package com.hotelboutique.auth.messaging;

import com.hotelboutique.auth.event.EventoUsuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/** Convierte el cuerpo crudo del mensaje; devuelve null si no es un JSON valido. */
@Slf4j
final class EventoUsuarioParser {

    private static final JsonMapper MAPPER = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    private EventoUsuarioParser() {
    }

    static EventoUsuario parsear(Message mensaje) {
        try {
            return MAPPER.readValue(mensaje.getBody(), EventoUsuario.class);
        } catch (Exception e) {
            log.error("Mensaje no convertible a EventoUsuario: {}", e.getMessage());
            return null;
        }
    }
}

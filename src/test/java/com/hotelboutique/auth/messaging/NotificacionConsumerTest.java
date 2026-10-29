package com.hotelboutique.auth.messaging;

import com.hotelboutique.auth.event.EventoUsuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class NotificacionConsumerTest {

    @Test
    void registraNotificacionSinLanzarExcepcion() {
        NotificacionConsumer consumer = new NotificacionConsumer();
        EventoUsuario evento = new EventoUsuario("usuario.registrado", null, "nuevo@hotel.cl", null, null);

        assertThatCode(() -> consumer.onNotificacion(evento)).doesNotThrowAnyException();
    }
}

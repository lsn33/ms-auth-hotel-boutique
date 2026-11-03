package com.hotelboutique.auth.messaging;

import com.hotelboutique.auth.event.EventoUsuario;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class NotificacionConsumerTest {

    @Test
    void registraNotificacionYConfirmaConAck() throws Exception {
        Channel channel = mock(Channel.class);
        EventoUsuario evento = new EventoUsuario("usuario.registrado", null, "nuevo@hotel.cl", null, null);

        new NotificacionConsumer().onNotificacion(evento, channel, 2L);

        verify(channel).basicAck(2L, false);
    }

    @Test
    void tipoVacioHaceNackSinRequeue() throws Exception {
        Channel channel = mock(Channel.class);
        EventoUsuario evento = new EventoUsuario(" ", null, "nuevo@hotel.cl", null, null);

        new NotificacionConsumer().onNotificacion(evento, channel, 5L);

        verify(channel).basicNack(5L, false, false);
    }
}

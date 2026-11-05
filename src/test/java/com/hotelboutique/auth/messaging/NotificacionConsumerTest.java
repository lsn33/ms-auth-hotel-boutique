package com.hotelboutique.auth.messaging;

import com.hotelboutique.auth.event.EventoUsuario;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

class NotificacionConsumerTest {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private Message mensaje(EventoUsuario evento) {
        return new Message(MAPPER.writeValueAsBytes(evento));
    }

    @Test
    void registraNotificacionYConfirmaConAck() throws Exception {
        Channel channel = mock(Channel.class);
        EventoUsuario evento = new EventoUsuario("usuario.registrado", null, "nuevo@hotel.cl", null, null);

        new NotificacionConsumer().onNotificacion(mensaje(evento), channel, 2L);

        verify(channel).basicAck(2L, false);
    }

    @Test
    void tipoVacioHaceNackSinRequeue() throws Exception {
        Channel channel = mock(Channel.class);
        EventoUsuario evento = new EventoUsuario(" ", null, "nuevo@hotel.cl", null, null);

        new NotificacionConsumer().onNotificacion(mensaje(evento), channel, 5L);

        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void jsonMalformadoHaceNackSinRequeue() throws Exception {
        Channel channel = mock(Channel.class);
        Message roto = new Message("{esto no es json".getBytes(StandardCharsets.UTF_8));

        new NotificacionConsumer().onNotificacion(roto, channel, 9L);

        verify(channel).basicNack(9L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}

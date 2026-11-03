package com.hotelboutique.auth.messaging;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

class DlqConsumerTest {

    private Message mensaje(String cuerpo) {
        return new Message(cuerpo.getBytes(StandardCharsets.UTF_8), new MessageProperties());
    }

    @Test
    void registraYConfirmaMensajeDeNotificacionesDlq() throws Exception {
        Channel channel = mock(Channel.class);

        new DlqConsumer().onNotificacionesDlq(mensaje("{\"tipo\":\"usuario.registrado\"}"), channel, 4L);

        verify(channel).basicAck(4L, false);
    }
}
package com.hotelboutique.auth.messaging;

import com.hotelboutique.auth.event.EventoUsuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificacionConsumer {

    @RabbitListener(queues = "${hotel.messaging.queues.notificaciones}")
    public void onNotificacion(EventoUsuario evento) {
        try {
            log.info("Notificacion [{}] para {}", evento.tipo(), evento.usuarioEmail());
        } catch (RuntimeException ex) {
            log.error("Error al procesar notificacion del usuario {}, se envia a DLQ: {}",
                    evento.usuarioEmail(), ex.getMessage(), ex);
            throw new AmqpRejectAndDontRequeueException("Error al procesar notificacion", ex);
        }
    }
}

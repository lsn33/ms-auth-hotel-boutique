package com.hotelboutique.auth.messaging;

import com.hotelboutique.auth.event.EventoUsuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${hotel.messaging.exchanges.eventos}")
    private String exchangeEventos;

    public void publicar(EventoUsuario evento) {
        try {
            rabbitTemplate.convertAndSend(exchangeEventos, evento.tipo(), evento);
            log.info("Evento publicado: {} usuario={}", evento.tipo(), evento.usuarioEmail());
        } catch (AmqpException ex) {
            log.error("No se pudo publicar el evento {} del usuario {}: {}",
                    evento.tipo(), evento.usuarioEmail(), ex.getMessage(), ex);
        }
    }
}

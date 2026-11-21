package com.hotelboutique.auth.kafka;

import com.hotelboutique.auth.service.ActividadUsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer group "auth-actividad-usuario": lee el MISMO topico que usa ms-reservas (grupo "reservas-ocupacion"),
 * pero cada grupo recibe todos los eventos de forma independiente (con RabbitMQ habria que duplicar la cola).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "hotel.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class ActividadUsuarioConsumer {

    private final ActividadUsuarioService actividadService;

    @KafkaListener(
            topics = "${hotel.kafka.topics.reservas}",
            groupId = "${hotel.kafka.groups.actividad}",
            properties = "spring.json.value.default.type=com.hotelboutique.auth.kafka.ReservaStreamEvent")
    public void onReservaEvento(ConsumerRecord<String, ReservaStreamEvent> registro) {
        ReservaStreamEvent evento = registro.value();

        if (evento == null || !evento.esValido()) {
            log.warn("Evento de reserva invalido descartado (particion={}, offset={}): {}",
                    registro.partition(), registro.offset(), evento);
            return;
        }

        log.info("Evento recibido {} reserva={} usuario={} (particion={}, offset={})",
                evento.tipo(), evento.reservaId(), evento.usuarioEmail(), registro.partition(), registro.offset());
        actividadService.registrar(evento);
    }
}

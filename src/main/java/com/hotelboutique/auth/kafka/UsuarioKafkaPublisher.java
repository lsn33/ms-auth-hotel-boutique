package com.hotelboutique.auth.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** Productor Kafka de eventos de usuario. Si Kafka falla se registra, pero el login/sincronizacion no se ve afectado. */
@Component
public class UsuarioKafkaPublisher {

    private static final Logger log = LoggerFactory.getLogger(UsuarioKafkaPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;
    private final boolean habilitado;

    public UsuarioKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                 @Value("${hotel.kafka.topics.usuarios}") String topic,
                                 @Value("${hotel.kafka.enabled:true}") boolean habilitado) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.habilitado = habilitado;
    }

    public void publicarSincronizacion(String email, String nombre) {
        if (!habilitado || email == null || email.isBlank()) {
            return;
        }
        UsuarioStreamEvent evento = UsuarioStreamEvent.sincronizado(email, nombre);
        try {
            // Clave = email: todos los eventos de un usuario van a la misma particion y el topico compactado conserva el ultimo
            kafkaTemplate.send(topic, email, evento).whenComplete((resultado, error) -> {
                if (error != null) {
                    log.error("No se pudo publicar en Kafka el usuario {}: {}", email, error.getMessage());
                } else if (resultado != null) {
                    log.info("Kafka publicado: {} usuario={} topico={} particion={} offset={}",
                            evento.tipo(), email, topic,
                            resultado.getRecordMetadata().partition(), resultado.getRecordMetadata().offset());
                }
            });
        } catch (RuntimeException ex) {
            log.error("Kafka no disponible, usuario {} no publicado: {}", email, ex.getMessage());
        }
    }
}

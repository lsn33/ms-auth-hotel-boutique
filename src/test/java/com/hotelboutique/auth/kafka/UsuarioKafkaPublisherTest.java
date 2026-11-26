package com.hotelboutique.auth.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioKafkaPublisherTest {

    private static final String TOPICO = "hotel.usuarios.eventos";

    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
    }

    @Test
    void publicaConElEmailComoClave() {
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        new UsuarioKafkaPublisher(kafkaTemplate, TOPICO, true).publicarSincronizacion("ana@mail.com", "Ana");

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq(TOPICO),
                org.mockito.ArgumentMatchers.eq("ana@mail.com"), captor.capture());
        UsuarioStreamEvent evento = (UsuarioStreamEvent) captor.getValue();
        assertEquals("usuario.sincronizado", evento.tipo());
        assertEquals("ana@mail.com", evento.email());
        assertEquals("Ana", evento.nombre());
    }

    @Test
    void siKafkaFallaNoPropagaLaExcepcion() {
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenThrow(new IllegalStateException("broker caido"));

        new UsuarioKafkaPublisher(kafkaTemplate, TOPICO, true).publicarSincronizacion("ana@mail.com", "Ana");
    }

    @Test
    void sinEmailODeshabilitadoNoPublica() {
        new UsuarioKafkaPublisher(kafkaTemplate, TOPICO, true).publicarSincronizacion(" ", "Ana");
        new UsuarioKafkaPublisher(kafkaTemplate, TOPICO, false).publicarSincronizacion("ana@mail.com", "Ana");

        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }
}

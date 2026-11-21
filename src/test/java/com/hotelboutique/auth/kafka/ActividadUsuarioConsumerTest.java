package com.hotelboutique.auth.kafka;

import com.hotelboutique.auth.service.ActividadUsuarioService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActividadUsuarioConsumerTest {

    @Mock private ActividadUsuarioService actividadService;
    @InjectMocks private ActividadUsuarioConsumer consumer;

    private static ConsumerRecord<String, ReservaStreamEvent> registro(ReservaStreamEvent evento) {
        return new ConsumerRecord<>("hotel.reservas.eventos", 2, 15L, "7", evento);
    }

    @Test
    void eventoValidoSeEntregaAlServicio() {
        ReservaStreamEvent evento = new ReservaStreamEvent("e-1", "reserva.creada", 10L, "ana@mail.com", 7L,
                null, null, "CONFIRMADA", Instant.now());

        consumer.onReservaEvento(registro(evento));

        verify(actividadService).registrar(evento);
    }

    @Test
    void eventoSinUsuarioSeDescarta() {
        ReservaStreamEvent invalido = new ReservaStreamEvent("e-2", "reserva.creada", 10L, null, 7L,
                null, null, null, Instant.now());

        consumer.onReservaEvento(registro(invalido));

        verify(actividadService, never()).registrar(any());
    }

    @Test
    void valorNuloSeDescarta() {
        consumer.onReservaEvento(registro(null));

        verify(actividadService, never()).registrar(any());
    }
}

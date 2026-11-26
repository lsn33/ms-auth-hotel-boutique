package com.hotelboutique.auth.service;

import com.hotelboutique.auth.dto.ActividadUsuarioResponse;
import com.hotelboutique.auth.entity.ActividadUsuario;
import com.hotelboutique.auth.kafka.ReservaStreamEvent;
import com.hotelboutique.auth.repository.ActividadUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActividadUsuarioServiceTest {

    @Mock private ActividadUsuarioRepository repository;
    @InjectMocks private ActividadUsuarioService service;

    private static ReservaStreamEvent evento(String tipo) {
        return new ReservaStreamEvent("e-1", tipo, 10L, "ana@mail.com", 7L, null, null, null, Instant.now());
    }

    private ActividadUsuario guardada() {
        ArgumentCaptor<ActividadUsuario> captor = ArgumentCaptor.forClass(ActividadUsuario.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void reservaCreadaSumaUnaReservaAUnUsuarioNuevo() {
        when(repository.findById("ana@mail.com")).thenReturn(Optional.empty());

        service.registrar(evento("reserva.creada"));

        ActividadUsuario actividad = guardada();
        assertEquals(1, actividad.getTotalReservas());
        assertEquals(0, actividad.getReservasCanceladas());
        assertEquals("reserva.creada", actividad.getUltimoEvento());
    }

    @Test
    void cancelacionSumaCanceladasSinTocarElTotal() {
        when(repository.findById("ana@mail.com")).thenReturn(Optional.of(
                ActividadUsuario.builder().email("ana@mail.com").totalReservas(3).build()));

        service.registrar(evento("reserva.cancelada"));

        ActividadUsuario actividad = guardada();
        assertEquals(3, actividad.getTotalReservas());
        assertEquals(1, actividad.getReservasCanceladas());
    }

    @Test
    void checkinSoloActualizaLaUltimaActividad() {
        when(repository.findById("ana@mail.com")).thenReturn(Optional.of(
                ActividadUsuario.builder().email("ana@mail.com").totalReservas(2).build()));

        service.registrar(evento("reserva.checkin"));

        ActividadUsuario actividad = guardada();
        assertEquals(2, actividad.getTotalReservas());
        assertEquals("reserva.checkin", actividad.getUltimoEvento());
    }

    @Test
    void obtenerDevuelveCerosSiElUsuarioNoTieneActividad() {
        when(repository.findById("nuevo@mail.com")).thenReturn(Optional.empty());

        ActividadUsuarioResponse respuesta = service.obtener("nuevo@mail.com");

        assertEquals(0, respuesta.totalReservas());
        assertEquals("nuevo@mail.com", respuesta.email());
    }
}

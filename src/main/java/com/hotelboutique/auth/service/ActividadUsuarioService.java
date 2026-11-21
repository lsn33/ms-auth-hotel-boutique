package com.hotelboutique.auth.service;

import com.hotelboutique.auth.dto.ActividadUsuarioResponse;
import com.hotelboutique.auth.entity.ActividadUsuario;
import com.hotelboutique.auth.kafka.ReservaStreamEvent;
import com.hotelboutique.auth.repository.ActividadUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** Logica de negocio de la actividad del usuario; no conoce Kafka (eso vive en ActividadUsuarioConsumer). */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActividadUsuarioService {

    private final ActividadUsuarioRepository repository;

    @Transactional
    public void registrar(ReservaStreamEvent evento) {
        ActividadUsuario actividad = repository.findById(evento.usuarioEmail())
                .orElseGet(() -> ActividadUsuario.builder().email(evento.usuarioEmail()).build());

        switch (evento.tipo()) {
            case "reserva.creada" -> actividad.setTotalReservas(actividad.getTotalReservas() + 1);
            case "reserva.cancelada" -> actividad.setReservasCanceladas(actividad.getReservasCanceladas() + 1);
            default -> {
                // check-in y check-out solo actualizan la ultima actividad
            }
        }

        actividad.setUltimoEvento(evento.tipo());
        actividad.setUltimaActividad(LocalDateTime.now());
        repository.save(actividad);
        log.info("Actividad de {}: reservas={}, canceladas={} (evento {})",
                actividad.getEmail(), actividad.getTotalReservas(), actividad.getReservasCanceladas(), evento.tipo());
    }

    public ActividadUsuarioResponse obtener(String email) {
        return repository.findById(email)
                .map(ActividadUsuarioResponse::desde)
                .orElseGet(() -> ActividadUsuarioResponse.vacia(email));
    }
}

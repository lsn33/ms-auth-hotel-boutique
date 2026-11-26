package com.hotelboutique.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Actividad de cada usuario, calculada a partir de los eventos de reserva que llegan por Kafka. */
@Entity
@Table(name = "actividad_usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadUsuario {

    @Id
    private String email;

    @Column(name = "total_reservas", nullable = false)
    private int totalReservas;

    @Column(name = "reservas_canceladas", nullable = false)
    private int reservasCanceladas;

    @Column(name = "ultimo_evento")
    private String ultimoEvento;

    @Column(name = "ultima_actividad")
    private LocalDateTime ultimaActividad;
}

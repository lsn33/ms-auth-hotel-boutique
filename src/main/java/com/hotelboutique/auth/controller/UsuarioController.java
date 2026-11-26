package com.hotelboutique.auth.controller;

import com.hotelboutique.auth.dto.SyncUsuarioRequest;
import com.hotelboutique.auth.dto.ActividadUsuarioResponse;
import com.hotelboutique.auth.dto.UsuarioResponse;
import com.hotelboutique.auth.kafka.UsuarioKafkaPublisher;
import com.hotelboutique.auth.service.ActividadUsuarioService;
import com.hotelboutique.auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioKafkaPublisher usuarioKafkaPublisher;
    private final ActividadUsuarioService actividadService;

    @PostMapping("/sync")
    public ResponseEntity<UsuarioResponse> sincronizar(@AuthenticationPrincipal Jwt token,
                                                         @RequestBody SyncUsuarioRequest request) {
        UsuarioResponse respuesta = usuarioService.sincronizar(token, request);
        // Streaming (Kafka): el estado mas reciente del usuario queda en el topico compactado
        usuarioKafkaPublisher.publicarSincronizacion(respuesta.getEmail(), respuesta.getNombre());
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> miPerfil(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(token));
    }

    /** Actividad del usuario autenticado, calculada con los eventos de reserva que llegan por Kafka. */
    @GetMapping("/mi-actividad")
    public ResponseEntity<ActividadUsuarioResponse> miActividad(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.ok(actividadService.obtener(token.getClaimAsString("email")));
    }
}

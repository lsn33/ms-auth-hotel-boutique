package com.hotelboutique.auth.controller;

import com.hotelboutique.auth.dto.SyncUsuarioRequest;
import com.hotelboutique.auth.dto.UsuarioResponse;
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

    @PostMapping("/sync")
    public ResponseEntity<UsuarioResponse> sincronizar(@AuthenticationPrincipal Jwt token,
                                                         @RequestBody SyncUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.sincronizar(token, request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> miPerfil(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(token));
    }
}
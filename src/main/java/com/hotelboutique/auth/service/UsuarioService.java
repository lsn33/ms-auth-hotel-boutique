package com.hotelboutique.auth.service;

import com.hotelboutique.auth.dto.SyncUsuarioRequest;
import com.hotelboutique.auth.dto.UsuarioResponse;
import com.hotelboutique.auth.entity.Usuario;
import com.hotelboutique.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Se llama despues de que el usuario se registro/logueo en Cognito.
    // Crea el perfil local si no existe, o lo actualiza si ya existia.
    public UsuarioResponse sincronizar(Jwt token, SyncUsuarioRequest request) {
        String sub = token.getSubject();
        String email = token.getClaimAsString("email");

        Usuario usuario = usuarioRepository.findByCognitoSub(sub)
                .orElseGet(() -> Usuario.builder().cognitoSub(sub).build());

        usuario.setEmail(email);
        usuario.setNombre(request.getNombre());
        usuario.setPreferencias(request.getPreferencias());

        usuarioRepository.save(usuario);
        return UsuarioResponse.desde(usuario);
    }

    public UsuarioResponse obtenerPerfil(Jwt token) {
        Usuario usuario = usuarioRepository.findByCognitoSub(token.getSubject())
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado, sincroniza primero con /usuarios/sync"));
        return UsuarioResponse.desde(usuario);
    }
}
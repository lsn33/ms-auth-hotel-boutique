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

    // Se llama despues de que el usuario se registro/logueo en Entra ID.
    // Crea el perfil local si no existe, o lo actualiza si ya existia.
    public UsuarioResponse sincronizar(Jwt token, SyncUsuarioRequest request) {
        // Entra ID: oid = Object ID (equivalente a Cognito sub)
        String oid = token.getClaimAsString("oid");
        String email = token.getClaimAsString("preferred_username");

        Usuario usuario = usuarioRepository.findByEntraOid(oid)
                .orElseGet(() -> Usuario.builder().entraOid(oid).build());

        usuario.setEmail(email);
        usuario.setNombre(request.getNombre());
        usuario.setPreferencias(request.getPreferencias());

        usuarioRepository.save(usuario);
        return UsuarioResponse.desde(usuario);
    }

    public UsuarioResponse obtenerPerfil(Jwt token) {
        String oid = token.getClaimAsString("oid");
        Usuario usuario = usuarioRepository.findByEntraOid(oid)
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado, sincroniza primero con /usuarios/sync"));
        return UsuarioResponse.desde(usuario);
    }
}
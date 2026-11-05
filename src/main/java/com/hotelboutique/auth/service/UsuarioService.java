package com.hotelboutique.auth.service;

import com.hotelboutique.auth.dto.SyncUsuarioRequest;
import com.hotelboutique.auth.dto.UsuarioResponse;
import com.hotelboutique.auth.entity.Usuario;
import com.hotelboutique.auth.event.EventoUsuario;
import com.hotelboutique.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UsuarioResponse sincronizar(Jwt token, SyncUsuarioRequest request) {
        String sub = token.getSubject();
        String email = token.getClaimAsString("email");

        Usuario usuario = usuarioRepository.findByCognitoSub(sub).orElse(null);
        boolean esNuevo = usuario == null;
        if (esNuevo) {
            usuario = Usuario.builder().cognitoSub(sub).build();
        }

        usuario.setEmail(email);
        usuario.setNombre(request.getNombre());
        usuario.setPreferencias(request.getPreferencias());

        usuarioRepository.save(usuario);

        if (esNuevo) {
            eventPublisher.publishEvent(new EventoUsuario("usuario.registrado", null, email, null, null));
        }

        return UsuarioResponse.desde(usuario);
    }

    public UsuarioResponse obtenerPerfil(Jwt token) {
        Usuario usuario = usuarioRepository.findByCognitoSub(token.getSubject())
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado, sincroniza primero con /usuarios/sync"));
        return UsuarioResponse.desde(usuario);
    }
}

package com.hotelboutique.auth.service;

import com.hotelboutique.auth.dto.SyncUsuarioRequest;
import com.hotelboutique.auth.entity.Usuario;
import com.hotelboutique.auth.event.EventoUsuario;
import com.hotelboutique.auth.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @InjectMocks private UsuarioService usuarioService;

    private Jwt tokenConEmail(String sub, String email) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(sub);
        when(jwt.getClaimAsString("email")).thenReturn(email);
        return jwt;
    }

    private Jwt tokenSinEmail(String sub) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(sub);
        return jwt;
    }

    private SyncUsuarioRequest request() {
        SyncUsuarioRequest req = new SyncUsuarioRequest();
        req.setNombre("Joaquin");
        req.setPreferencias("ninguna");
        return req;
    }

    @Test
    void sincronizarUsuarioNuevoPublicaEventoRegistrado() {
        when(usuarioRepository.findByCognitoSub("sub-1")).thenReturn(Optional.empty());

        usuarioService.sincronizar(tokenConEmail("sub-1", "nuevo@hotel.cl"), request());

        verify(usuarioRepository).save(any(Usuario.class));
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(captor.capture());
        EventoUsuario evento = (EventoUsuario) captor.getValue();
        assertThat(evento.tipo()).isEqualTo("usuario.registrado");
        assertThat(evento.usuarioEmail()).isEqualTo("nuevo@hotel.cl");
    }

    @Test
    void sincronizarUsuarioExistenteNoPublicaEvento() {
        Usuario existente = Usuario.builder().cognitoSub("sub-1").email("viejo@hotel.cl").build();
        when(usuarioRepository.findByCognitoSub("sub-1")).thenReturn(Optional.of(existente));

        usuarioService.sincronizar(tokenConEmail("sub-1", "viejo@hotel.cl"), request());

        verify(usuarioRepository).save(existente);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void obtenerPerfilLanzaErrorSiNoSeSincronizo() {
        when(usuarioRepository.findByCognitoSub("sub-9")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerPerfil(tokenSinEmail("sub-9")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sincroniza primero");
    }
}

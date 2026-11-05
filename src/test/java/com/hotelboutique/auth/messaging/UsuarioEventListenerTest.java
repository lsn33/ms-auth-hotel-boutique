package com.hotelboutique.auth.messaging;

import com.hotelboutique.auth.event.EventoUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioEventListenerTest {

    @Mock private UsuarioEventPublisher publisher;
    @InjectMocks private UsuarioEventListener listener;

    @Test
    void onUsuarioEventoDelegaEnElPublicador() {
        EventoUsuario evento = new EventoUsuario("usuario.registrado", null, "nuevo@hotel.cl", null, null);

        listener.onUsuarioEvento(evento);

        verify(publisher).publicar(evento);
    }
}

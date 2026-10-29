package com.hotelboutique.auth.messaging;

import com.hotelboutique.auth.event.EventoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioEventPublisherTest {

    @Mock private RabbitTemplate rabbitTemplate;
    @InjectMocks private UsuarioEventPublisher publisher;

    private final EventoUsuario evento =
            new EventoUsuario("usuario.registrado", null, "nuevo@hotel.cl", null, null);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "exchangeEventos", "hotel.eventos");
    }

    @Test
    void publicarEnviaAlExchangeEventosConRoutingKeyDelTipo() {
        publisher.publicar(evento);

        verify(rabbitTemplate).convertAndSend("hotel.eventos", "usuario.registrado", evento);
    }

    @Test
    void publicarNoLanzaExcepcionSiRabbitFalla() {
        doThrow(new AmqpException("sin conexion"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(EventoUsuario.class));

        assertThatCode(() -> publisher.publicar(evento)).doesNotThrowAnyException();
    }
}

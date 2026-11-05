package com.hotelboutique.auth.messaging;

import com.hotelboutique.auth.event.EventoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UsuarioEventListener {

    private final UsuarioEventPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUsuarioEvento(EventoUsuario evento) {
        publisher.publicar(evento);
    }
}

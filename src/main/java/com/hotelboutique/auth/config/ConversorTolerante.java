package com.hotelboutique.auth.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

public class ConversorTolerante extends JacksonJsonMessageConverter {

    @Override
    public Object fromMessage(Message message) {
        try {
            return super.fromMessage(message);
        } catch (Exception e) {
            return message.getBody();
        }
    }
}
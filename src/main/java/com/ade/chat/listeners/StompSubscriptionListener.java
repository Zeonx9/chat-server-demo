package com.ade.chat.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class StompSubscriptionListener implements ApplicationListener<SessionSubscribeEvent> {
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        var headers = event.getMessage().getHeaders();
        log.info("new subscription: sessionId={}, destination={}", headers.get("simpSessionId"), headers.get("simpDestination"));
    }
}

package eu.invouk.nexuschunk.socket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class MinecraftSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = Logger.getLogger(MinecraftSocketHandler.class.getName());

    // Udržiavanie všetkých aktívnych pripojení (Minecraft serverov)
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());



    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        logger.info("Nové pripojenie z: " + session.getRemoteAddress() + " (ID: " + session.getId() + "). Aktívne pripojenia: " + sessions.size());

        // Príklad: Odošleme uvítaciu správu späť klientovi (pluginu)
        try {
            session.sendMessage(new TextMessage("Server bol pripojený k Spring Nexus Serveru."));
        } catch (IOException e) {
            logger.severe("Chyba pri odosielaní úvodnej správy: " + e.getMessage());
        }
    }

    /**
     * 📥 Spracovanie prichádzajúcich textových správ.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Prijatá správa od " + session.getId() + ": " + payload);

        // TODO: Tu by prebiehala kľúčová biznis logika
        // Napr.: Uložiť dáta do DB, overiť stav servera, vyvolať nejakú akciu...

        // Príklad ECHO: Odoslanie správy späť všetkým ostatným pripojeným serverom (ak je to potrebné)
        broadcastMessage("Dostal som od " + session.getId() + ": " + payload);
    }

    /**
     * ❌ Uzatvorenie pripojenia.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        logger.warning("Pripojenie zatvorené z " + session.getRemoteAddress() +
                ". Dôvod: " + status.getCode() + " - " + status.getReason() +
                ". Aktívne pripojenia: " + sessions.size());
    }

    /**
     * 📤 Metóda pre odoslanie správy všetkým pripojeným Minecraft serverom.
     */
    public void broadcastMessage(String message) {
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                logger.severe("Chyba pri broadcastingu správy: " + e.getMessage());
            }
        });
    }

    /**
     * 📤 Metóda pre odoslanie správy konkrétnemu pripojeniu (podľa ID).
     */
    public void sendMessageToSession(String sessionId, String message) {
        sessions.stream()
                .filter(session -> session.getId().equals(sessionId) && session.isOpen())
                .findFirst()
                .ifPresent(session -> {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        logger.severe("Chyba pri odosielaní správy pre ID " + sessionId + ": " + e.getMessage());
                    }
                });
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        session.sendMessage(message);
        super.handlePongMessage(session, message);
    }
}
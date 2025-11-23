package eu.invouk.nexuschunk.socket;

import eu.invouk.api.packets.EPacket;
import eu.invouk.api.packets.Packet;
import eu.invouk.api.packets.PacketDecoderFactory;
import eu.invouk.api.packets.connection.AuthorizationPacket;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MinecraftSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = Logger.getLogger(MinecraftSocketHandler.class.getName());
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private boolean isAuthorized = false;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        logger.info("Nove pripojenie z: " + session.getRemoteAddress() + " (ID: " + session.getId() + "). Aktivne pripojenia: " + sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Prijata správa od " + session.getId() + ": " + payload);

        Packet packet = PacketDecoderFactory.decode(payload);
        if (packet == null) {
            log.error("Prijatý neplatný JSON. Ukončujem pripojenie.");
            session.close(CloseStatus.BAD_DATA.withReason("Neplatny format paketu."));
            return;
        }
        if (!isAuthorized) {
            if (packet.getEPacket() == EPacket.AUTHORIZE_PACKET && packet instanceof AuthorizationPacket authorizationPacket) {

                if(authorizationPacket.getAuthorizationToken().equals("abc0123")) {
                    this.isAuthorized = true;
                    logger.info("Session " + session.getId() + " uspesne autorizovana");
                    return; // Ukončíme spracovanie prvého paketu
                } else {
                    logger.warning("Session " + session.getId() + " sa pokusila autorizovat neplatnym tokenom.");
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Neplatny autorizacny token."));
                    return;
                }
            } else {
                session.close(CloseStatus.POLICY_VIOLATION.withReason("Prva sprava musi byt AUTHORIZE_PACKET."));
                return;
            }
        }

        switch (packet.getEPacket()) {
            case HEARTBEAT_PACKET -> {
                System.out.println("Heartbeat packet");
            }
            default -> {
                logger.info("Unknown packet: " + payload);
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        logger.warning("Pripojenie zatvorene z " + session.getRemoteAddress() +
                ". Dovod: " + status.getCode() + " - " + status.getReason() +
                ". Aktivne pripojenia: " + sessions.size());
    }

    public void broadcastPacket(Packet packet) {
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(packet.encode()));
                }
            } catch (IOException e) {
                logger.severe("Chyba pri broadcastingu spravy: " + e.getMessage());
            }
        });
    }

    public void sendMessageToSession(String sessionId, Packet packet) {
        sessions.stream()
                .filter(session -> session.getId().equals(sessionId) && session.isOpen())
                .findFirst()
                .ifPresent(session -> {
                    try {
                        session.sendMessage(new TextMessage(packet.encode()));
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
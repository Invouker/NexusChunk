package eu.invouk.nexuschunk.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // Zapne Spring WebSocket podporu
public class WebSocketConfig implements WebSocketConfigurer {

    private final MinecraftSocketHandler socketHandler;

    public WebSocketConfig(MinecraftSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketHandler, "/ws/connect")
                .setAllowedOriginPatterns("*"); // Povolenie pripojenia z akéhokoľvek zdroja (POZOR: v produkcii obmedzte!)
    }

}
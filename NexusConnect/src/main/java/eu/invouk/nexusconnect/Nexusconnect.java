package eu.invouk.nexusconnect;

import eu.invouk.api.packets.Packet;
import eu.invouk.api.packets.connection.AuthorizationPacket;
import eu.invouk.nexusconnect.connector.WebSocketManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Nexusconnect extends JavaPlugin {

    private WebSocketManager webSocketManager;
    private static Nexusconnect instance;


    private static final Logger log = LoggerFactory.getLogger(Nexusconnect.class);

    @Override
    public void onEnable() {
        instance = this;
        //this.saveDefaultConfig(); // Ak používate konfiguračný súbor

        // Načítanie URI z konfigurácie (odporúčané)
        //String wsUri = this.getConfig().getString("websocket-uri", "ws://localhost:8080/ws/connect");
        String wsUri = "ws://localhost:8080/ws/connect";
        this.webSocketManager = new WebSocketManager(this, wsUri);
        this.webSocketManager.connect();

        log.info("NexusConnect: Plugin a WebSocket Manager inicializované.");



    }

    @Override
    public void onDisable() {
        if (this.webSocketManager != null) {
            this.webSocketManager.disconnect();
        }
        log.info("NexusConnect: Plugin bol úspešne vypnutý.");
    }

    public static Nexusconnect getInstance() {
        return instance;
    }

    public WebSocketManager getWebSocketManager() {
        return webSocketManager;
    }
}

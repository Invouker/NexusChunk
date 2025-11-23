package eu.invouk.nexusconnect.connector;

import eu.invouk.nexusconnect.Nexusconnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.net.http.HttpClient.newHttpClient;

public class WebSocketManager {

    private static final Logger log = LoggerFactory.getLogger(WebSocketManager.class);
    private final Nexusconnect plugin;
    private final String wsUri;
    private WebSocket webSocket;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final long RECONNECT_DELAY = 10; // Sekúnd

    private int tryConnection;
    private final int MAX_TRY_CONNECTION = 3;

    public WebSocketManager(Nexusconnect plugin, String wsUri) {
        this.plugin = plugin;
        this.wsUri = wsUri;
    }

    public void connect() {
        log.info("Pokúšam sa pripojiť k WebSocket serveru: {}", wsUri);
        HttpClient client = newHttpClient();

        try {
            client.newWebSocketBuilder()
                    .buildAsync(URI.create(wsUri), new MinecraftClientListener(plugin, this))
                    .thenAccept(ws -> {
                        this.webSocket = ws;
                        log.info("WebSocket úspešne pripojený.");
                        sendMessage("Connection Successfully!");
                    })
                    .exceptionally(t -> {
                        t.printStackTrace();
                        log.warn("({}/{}) Nepodarilo sa pripojiť k WebSocket serveru. Skúšam znovu o {}s.", tryConnection, MAX_TRY_CONNECTION, RECONNECT_DELAY);
                        tryConnection++;

                        if(tryConnection > MAX_TRY_CONNECTION) {
                            log.info("Cannot connect to " + wsUri);
                        } else  scheduleReconnect();
                        return null;
                    });
        } catch (Exception e) {
            log.warn("Chyba pri vytváraní WebSocket klienta.", e);
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        // Zabezpečíme, že plánovač beží a naplánujeme opätovné pripojenie
        scheduler.schedule(this::connect, RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    public void onConnectionClosed(int statusCode, String reason) {
        plugin.getLogger().warning(String.format("WebSocket zatvorený: Status=%d, Dôvod=%s", statusCode, reason));
        this.webSocket = null;

        // Pri nekritických chybách alebo zatvorení pre opätovné pripojenie
        if (statusCode != WebSocket.NORMAL_CLOSURE) {
            log.info("Spúšťam automatické opätovné pripojenie...");
            scheduleReconnect();
        }
    }

    public void disconnect() {
        log.info("Zatváram WebSocket pripojenie a plánovač.");
        if (webSocket != null && !webSocket.isInputClosed()) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Plugin vypnutý");
        }
        scheduler.shutdownNow();
    }

    public void sendMessage(String message) {
        if (webSocket != null && !webSocket.isInputClosed()) {
            plugin.getLogger().info("Odosielam správu: " + message);
            webSocket.sendText(message, true);
        } else {
            plugin.getLogger().warning("WebSocket nie je pripojený. Správa neodoslaná.");
            // Môžete tu pridať logiku pre ukladanie správ, kým sa znova nepripojí
        }
    }
}
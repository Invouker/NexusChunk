package eu.invouk.nexusconnect.connector;

import eu.invouk.api.packets.HeartBeatPacket;
import eu.invouk.api.packets.Packet;
import eu.invouk.api.packets.connection.AuthorizationPacket;
import eu.invouk.nexusconnect.Nexusconnect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Queue;
import java.util.concurrent.*;

import static java.net.http.HttpClient.newHttpClient;

public class WebSocketManager {

    private static final Logger log = LoggerFactory.getLogger(WebSocketManager.class);
    private final Nexusconnect plugin;
    private final String wsUri;
    private WebSocket webSocket;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final long RECONNECT_DELAY = 10; // Sekúnd


    private Queue<Packet> packetQueue = new ConcurrentLinkedQueue<>();

    private int tryConnection;
    private final int MAX_TRY_CONNECTION = 3;

    public WebSocketManager(Nexusconnect plugin, String wsUri) {
        this.plugin = plugin;
        this.wsUri = wsUri;
    }

    public void connect() {
        log.info("Pokusam sa pripojit k WebSocket serveru: {}", wsUri);
        HttpClient client = newHttpClient();

        try {
            client.newWebSocketBuilder()
                    .buildAsync(URI.create(wsUri), new MinecraftClientListener(plugin, this))
                    .thenAccept(ws -> {
                        this.webSocket = ws;
                        log.info("WebSocket uspesne pripojeny.");
                        //sendMessage("Connection Successfully!");

                        AuthorizationPacket authorizationPacket = new AuthorizationPacket("abc0123");
                        sendPacket(authorizationPacket);


                        HeartBeatPacket heartBeatPacket = new HeartBeatPacket();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                sendPacket(heartBeatPacket);
                            }
                        }.runTaskTimerAsynchronously(plugin, 20L, 20*10L);
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
            log.warn("Chyba pri vytvarani WebSocket klienta.", e);
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        // Zabezpečíme, že plánovač beží a naplánujeme opätovné pripojenie
        scheduler.schedule(this::connect, RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    public void onConnectionClosed(int statusCode, String reason) {
        plugin.getLogger().warning(String.format("WebSocket zatvoreny: Status=%d, Dovod=%s", statusCode, reason));
        this.webSocket = null;

        // Pri nekritických chybách alebo zatvorení pre opätovné pripojenie
        if (statusCode != WebSocket.NORMAL_CLOSURE) {
            log.info("Spustam automaticke opetovna pripojenie...");
            scheduleReconnect();
        }
    }

    public void disconnect() {
        log.info("Zatvaram WebSocket pripojenie a planovac.");
        if (webSocket != null && !webSocket.isInputClosed()) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Plugin vypnuty");
        }
        scheduler.shutdownNow();
    }

    private void sendMessage(String message) {
        if (webSocket != null && !webSocket.isInputClosed()) {
            plugin.getLogger().info("Odosielam spravu: " + message);
            webSocket.sendText(message, true);
        } else {
            plugin.getLogger().warning("WebSocket nie je pripojeny. Sprava neodoslana.");
        }
    }

    /*public void sendPacket(Packet packet) {
        CompletableFuture.runAsync(() -> {
            try {
                packetQueue.add(packet);
            }
        })
    }*/

    public void sendPacket(Packet packet) {
        if (webSocket != null && !webSocket.isInputClosed()) {
            plugin.getLogger().info("Odosielam spravu: " + packet.toString());
            webSocket.sendText(packet.encode(), true);
        } else {
            plugin.getLogger().warning("WebSocket nie je pripojeny. Sprava neodoslana.");
        }
    }
}
package eu.invouk.nexusconnect.connector;


import java.net.http.WebSocket;
import eu.invouk.nexusconnect.Nexusconnect;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public record MinecraftClientListener(Nexusconnect plugin, WebSocketManager manager) implements WebSocket.Listener {

    private static final Logger log = LoggerFactory.getLogger(MinecraftClientListener.class);

    @Override
    public void onOpen(WebSocket webSocket) {
        log.info("Pripojenie otvorené. Požiadavka na prvú správu.");
        webSocket.request(1); // Vyžiadame si prvú správu
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        manager.onConnectionClosed(statusCode, reason);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        log.warn("Chyba na WebSocket pripojení.", error);
    }

    // --- Spracovanie správ ---

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        String message = data.toString();

        // Kľúčový bod: Spracovanie API volaní Minecraftu MUSÍ prebehnúť na hlavnej niťe
        Bukkit.getScheduler().runTask(plugin, () -> {
            handleIncomingMessage(message);
        });

        // Vyžiadame si ďalšiu správu
        return CompletableFuture.completedFuture("done").thenRun(() -> webSocket.request(1));
    }

    /**
     * Spracuje prichádzajúce dáta z Spring servera na hlavnej niťe Minecraftu.
     *
     * @param message Prijatý reťazec (ideálne JSON).
     */
    private void handleIncomingMessage(String message) {
        log.info("Prijatá správa na hlavnej niťe: " + message);

        // TODO: Parsujte správu (ideálne JSON) a spracujte akcie
        // Príklad spracovania jednoduchého príkazu:
        if (message.startsWith("BROADCAST:")) {
            String broadcastMessage = message.substring("BROADCAST:".length()).trim();
            Bukkit.broadcastMessage("§6[Nexus] §f" + broadcastMessage);
        }
        // else if (message.startsWith("TELEPORT:")) { ... }
    }
}
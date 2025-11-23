package eu.invouk.api.packets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.invouk.api.packets.connection.AuthorizationPacket;

public class PacketDecoderFactory {

    private static final Gson GSON = new Gson();

    /**
     * Dekóduje surový JSON reťazec do správneho objektu Paketu.
     * @param jsonPayload Surový JSON reťazec z WebSocketu.
     * @return Konkrétna inštancia Paketu.
     */
    public static Packet decode(String jsonPayload) {
        if (jsonPayload == null || jsonPayload.isEmpty()) {
            return null;
        }

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonPayload).getAsJsonObject();
            if(jsonObject == null)
                return null;

            String typeString = jsonObject.get("ePacket").getAsString();
            if(typeString == null)
                return null;
            EPacket type = EPacket.valueOf(typeString);


            return switch (type) {
                case HEARTBEAT_PACKET -> GSON.fromJson(jsonPayload, HeartBeatPacket.class);
                case AUTHORIZE_PACKET -> GSON.fromJson(jsonPayload, AuthorizationPacket.class);
            };
        } catch (Exception e) {
            //System.err.println("Chyba dekódovania paketu: " + e.getMessage());
            return null;
        }
    }
}

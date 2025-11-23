package eu.invouk.api.packets.connection;

import com.google.gson.Gson;
import eu.invouk.api.packets.EPacket;
import eu.invouk.api.packets.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizationPacket extends Packet {

    private String authorizationToken;

    public AuthorizationPacket(String authorizationToken) {
        super(EPacket.AUTHORIZE_PACKET);
        this.authorizationToken = authorizationToken;
    }

    @Override
    public String encode() {
        return new Gson().toJson(this);
    }

}

package eu.invouk.api.packets;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public abstract class Packet {

    @SerializedName("ePacket")
    EPacket ePacket;

    public Packet(EPacket ePacket) {
        this.ePacket = ePacket;
    }

    public abstract String encode();


}

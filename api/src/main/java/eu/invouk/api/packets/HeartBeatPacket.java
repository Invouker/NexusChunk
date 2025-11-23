package eu.invouk.api.packets;

import com.google.gson.Gson;

public class HeartBeatPacket extends Packet {

    public HeartBeatPacket() {
        super(EPacket.HEARTBEAT_PACKET);
    }

    @Override
    public String encode() {
        return new Gson().toJson(this);
    }
}

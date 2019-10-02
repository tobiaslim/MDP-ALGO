package networkmanager;

import networkmanager.dto.Packet;

public interface NetworkSubscriber {
    void onPacket(Packet packet);
}

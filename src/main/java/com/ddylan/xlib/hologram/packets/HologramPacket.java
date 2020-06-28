package com.ddylan.xlib.hologram.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class HologramPacket
{
    private List<PacketContainer> packets;
    private List<Integer> entityIds;
    
    public HologramPacket(final List<PacketContainer> packets, final List<Integer> entityIds) {
        this.packets = packets;
        this.entityIds = entityIds;
    }
    
    public void sendToPlayer(final Player player) {
        for (final PacketContainer packetContainer : this.packets) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<Integer> getEntityIds() {
        return this.entityIds;
    }
    
    public List<PacketContainer> getPackets() {
        return this.packets;
    }
}

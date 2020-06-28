package com.ddylan.xlib.hologram.packets.v1_8;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.ddylan.xlib.hologram.HologramLine;
import com.ddylan.xlib.hologram.packets.HologramPacket;
import com.ddylan.xlib.hologram.packets.HologramPacketProvider;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Minecraft18HologramPacketProvider implements HologramPacketProvider
{
    @Override
    public HologramPacket getPacketsFor(final Location location, final HologramLine line) {
        final List<PacketContainer> packets = Collections.singletonList(this.createArmorStandPacket(line.getSkullId(), line.getText(), location));
        return new HologramPacket(packets, Arrays.asList(line.getSkullId(), -1337));
    }
    
    protected PacketContainer createArmorStandPacket(final int witherSkullId, final String text, final Location location) {
        final PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        final StructureModifier<Integer> ints = displayPacket.getIntegers();
        ints.write(0, witherSkullId);
        ints.write(1, 30);
        ints.write(2, (int)(location.getX() * 32.0));
        ints.write(3, (int)((location.getY() - 2.0) * 32.0));
        ints.write(4, (int)(location.getZ() * 32.0));
        final WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, 32);
        watcher.setObject(2, ChatColor.translateAlternateColorCodes('&', text));
        watcher.setObject(3, 1);
        displayPacket.getDataWatcherModifier().write(0, watcher);
        return displayPacket;
    }
}

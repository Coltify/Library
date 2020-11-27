package com.ddylan.library.hologram.packets.v1_7;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.ddylan.library.hologram.HologramLine;
import com.ddylan.library.hologram.packets.HologramPacket;
import com.ddylan.library.hologram.packets.HologramPacketProvider;
import net.minecraft.server.v1_7_R4.MathHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Arrays;

public class Minecraft17HologramPacketProvider implements HologramPacketProvider
{
    @Override
    public HologramPacket getPacketsFor(final Location location, final HologramLine line) {
        final PacketContainer skullPacket = this.createWitherSkull(location, line.getSkullId());
        final PacketContainer horsePacket = this.createHorse(location, line.getHorseId(), line.getText());
        final PacketContainer attachPacket = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);
        attachPacket.getIntegers().write(1, line.getHorseId());
        attachPacket.getIntegers().write(2, line.getSkullId());
        return new HologramPacket(Arrays.asList(skullPacket, horsePacket, attachPacket), Arrays.asList(line.getSkullId(), line.getHorseId()));
    }
    
    protected PacketContainer createWitherSkull(final Location location, final int id) {
        final PacketContainer skull = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        final StructureModifier<Integer> ints = skull.getIntegers();
        ints.write(0, id);
        ints.write(1, (int)(location.getX() * 32.0));
        ints.write(2, MathHelper.floor((location.getY() - 0.13 + 55.0) * 32.0));
        ints.write(3, (int)(location.getZ() * 32.0));
        ints.write(9, 66);
        return skull;
    }
    
    protected PacketContainer createHorse(final Location location, final int id, final String text) {
        final PacketContainer horse = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        horse.getIntegers().write(0, id);
        horse.getIntegers().write(1, 100);
        horse.getIntegers().write(2, (int)(location.getX() * 32.0));
        horse.getIntegers().write(3, MathHelper.floor((location.getY() + 55.0) * 32.0));
        horse.getIntegers().write(4, (int)(location.getZ() * 32.0));
        final WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, 0);
        watcher.setObject(1, 300);
        watcher.setObject(10, ChatColor.translateAlternateColorCodes('&', text));
        watcher.setObject(11, 1);
        watcher.setObject(12, -1700000);
        horse.getDataWatcherModifier().write(0, watcher);
        return horse;
    }
}

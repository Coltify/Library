package com.ddylan.library.hologram;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.hologram.packets.HologramPacket;
import com.ddylan.library.hologram.packets.HologramPacketProvider;
import com.ddylan.library.hologram.packets.v1_7.Minecraft17HologramPacketProvider;
import com.ddylan.library.hologram.packets.v1_8.Minecraft18HologramPacketProvider;
import com.ddylan.library.tab.TabUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import mkremins.fanciful.shaded.gson.internal.Pair;
import net.minecraft.server.v1_7_R4.v1_7_R4.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BaseHologram implements Hologram
{
    private final Collection<UUID> viewers;
    protected Location location;
    protected List<HologramLine> lastLines;
    protected List<HologramLine> lines;
    protected final Set<UUID> currentWatchers;
    protected static final double distance = 0.23;
    
    protected BaseHologram(final HologramBuilder builder) {
        this.lastLines = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.currentWatchers = new HashSet<>();
        if (builder.getLocation() == null) {
            throw new IllegalArgumentException("Please provide a location for the hologram using HologramBuilder#at(Location)");
        }
        this.viewers = builder.getViewers();
        this.location = builder.getLocation();
        for (final String line : builder.getLines()) {
            this.lines.add(new HologramLine(line));
        }
    }
    
    @Override
    public void send() {
        Collection<UUID> viewers = this.viewers;
        if (viewers == null) {
            viewers = ImmutableList.copyOf(LibraryPlugin.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (final UUID uuid : viewers) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                this.show(player);
            }
        }
        HologramRegistry.getHolograms().add(this);
    }
    
    @Override
    public void destroy() {
        Collection<UUID> viewers = this.viewers;
        if (viewers == null) {
            viewers = ImmutableList.copyOf(LibraryPlugin.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (final UUID uuid : viewers) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                this.destroy0(player);
            }
        }
        if (this.viewers != null) {
            this.viewers.clear();
        }
        HologramRegistry.getHolograms().remove(this);
    }
    
    @Override
    public void addLines(final String... lines) {
        for (final String line : lines) {
            this.lines.add(new HologramLine(line));
        }
        this.update();
    }
    
    @Override
    public void setLine(final int index, final String line) {
        if (index > this.lines.size() - 1) {
            this.lines.add(new HologramLine(line));
        }
        else if (this.lines.get(index) != null) {
            this.lines.get(index).setText(line);
        }
        else {
            this.lines.set(index, new HologramLine(line));
        }
        this.update();
    }
    
    @Override
    public void setLines(final Collection<String> lines) {
        Collection<UUID> viewers = this.viewers;
        if (viewers == null) {
            viewers = ImmutableList.copyOf(LibraryPlugin.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (final UUID uuid : viewers) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                this.destroy0(player);
            }
        }
        this.lines.clear();
        for (final String line : lines) {
            this.lines.add(new HologramLine(line));
        }
        this.update();
    }
    
    @Override
    public List<String> getLines() {
        final List<String> lines = new ArrayList<String>();
        for (final HologramLine line : this.lines) {
            lines.add(line.getText());
        }
        return lines;
    }
    
    @Override
    public Location getLocation() {
        return this.location;
    }
    
    protected List<HologramLine> rawLines() {
        return this.lines;
    }
    
    protected void show(final Player player) {
        if (!player.getLocation().getWorld().equals(this.location.getWorld())) {
            return;
        }
        final Location first = this.location.clone().add(0.0, this.lines.size() * 0.23, 0.0);
        for (final HologramLine line : this.lines) {
            this.showLine(player, first.clone(), line);
            first.subtract(0.0, 0.23, 0.0);
        }
        this.currentWatchers.add(player.getUniqueId());
    }
    
    protected Pair<Integer, Integer> showLine(final Player player, final Location loc, final HologramLine line) {
        final HologramPacketProvider packetProvider = this.getPacketProviderForPlayer(player);
        final HologramPacket hologramPacket = packetProvider.getPacketsFor(loc, line);
        if (hologramPacket != null) {
            hologramPacket.sendToPlayer(player);
            return (Pair<Integer, Integer>)new Pair(hologramPacket.getEntityIds().get(0), hologramPacket.getEntityIds().get(1));
        }
        return null;
    }
    
    protected void destroy0(final Player player) {
        final List<Integer> ints = new ArrayList<>();
        for (final HologramLine line : this.lines) {
            if (line.getHorseId() == -1337) {
                ints.add(line.getSkullId());
            }
            else {
                ints.add(line.getSkullId());
                ints.add(line.getHorseId());
            }
        }
        final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.convertIntegers(ints));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        this.currentWatchers.remove(player.getUniqueId());
    }
    
    protected int[] convertIntegers(final List<Integer> integers) {
        final int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = integers.get(i);
        }
        return ret;
    }
    
    public void update() {
        Collection<UUID> viewers = this.getViewers();
        if (viewers == null) {
            viewers = ImmutableList.copyOf(LibraryPlugin.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (final UUID uuid : viewers) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                this.update(player);
            }
        }
        this.lastLines.addAll(this.lines);
    }
    
    public void update(final Player player) {
        if (!player.getLocation().getWorld().equals(this.location.getWorld())) {
            return;
        }
        if (this.lastLines.size() != this.lines.size()) {
            this.destroy0(player);
            this.show(player);
            return;
        }
        for (int index = 0; index < this.rawLines().size(); ++index) {
            final HologramLine line = this.rawLines().get(index);
            final String text = ChatColor.translateAlternateColorCodes('&', line.getText());
            final boolean is18 = TabUtils.is18(player);
            try {
                final PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
                container.getIntegers().write(0, is18 ? line.getSkullId() : line.getHorseId());
                final WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
                if (is18) {
                    wrappedDataWatcher.setObject(2, text);
                }
                else {
                    wrappedDataWatcher.setObject(10, text);
                }
                final List<WrappedWatchableObject> watchableObjects = Arrays.asList(Iterators.toArray(wrappedDataWatcher.iterator(), WrappedWatchableObject.class));
                container.getWatchableCollectionModifier().write(0, watchableObjects);
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
                }
                catch (Exception ex) {}
            }
            catch (IndexOutOfBoundsException e) {
                this.destroy0(player);
                this.show(player);
            }
        }
    }
    
    private HologramPacketProvider getPacketProviderForPlayer(final Player player) {
        return (((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() > 5) ? new Minecraft18HologramPacketProvider() : new Minecraft17HologramPacketProvider();
    }
    
    protected Collection<UUID> getViewers() {
        return this.viewers;
    }
}

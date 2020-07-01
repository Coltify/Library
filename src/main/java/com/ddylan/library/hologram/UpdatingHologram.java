package com.ddylan.library.hologram;

import com.ddylan.library.LibraryPlugin;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class UpdatingHologram extends BaseHologram
{
    private long interval;
    private Consumer<Hologram> updateFunction;
    private boolean showing;
    
    public UpdatingHologram(final UpdatingHologramBuilder builder) {
        super(builder);
        this.interval = 1L;
        this.showing = false;
        this.interval = builder.getInterval();
        this.updateFunction = builder.getUpdateFunction();
    }
    
    @Override
    public void send() {
        if (this.showing) {
            this.update();
            return;
        }
        super.send();
        this.showing = true;
        new BukkitRunnable() {
            public void run() {
                if (!UpdatingHologram.this.showing) {
                    this.cancel();
                }
                else {
                    UpdatingHologram.this.update();
                }
            }
        }.runTaskTimerAsynchronously((Plugin) LibraryPlugin.getInstance(), 0L, this.interval * 20L);
    }
    
    @Override
    public void setLine(final int index, final String line) {
        if (index > this.rawLines().size() - 1) {
            this.rawLines().add(new HologramLine(line));
        }
        else if (this.rawLines().get(index) != null) {
            this.rawLines().get(index).setText(line);
        }
        else {
            this.rawLines().set(index, new HologramLine(line));
        }
    }
    
    @Override
    public void setLines(final Collection<String> lines) {
        Collection<UUID> viewers = this.getViewers();
        if (viewers == null) {
            viewers = ImmutableList.copyOf(LibraryPlugin.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (final UUID uuid : viewers) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                this.destroy0(player);
            }
        }
        this.rawLines().clear();
        for (final String line : lines) {
            this.rawLines().add(new HologramLine(line));
        }
    }
    
    @Override
    public void destroy() {
        super.destroy();
        this.showing = false;
    }
    
    @Override
    public void update() {
        this.updateFunction.accept(this);
        if (!this.showing) {
            return;
        }
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
        this.lastLines = this.lines;
    }
}

package com.ddylan.xlib.hologram;

import com.ddylan.xlib.Library;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class HologramListener implements Listener
{
    @EventHandler
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final Iterator[] iterator = new Iterator[]{};
        final Hologram[] hologram = new Hologram[1];
        final BaseHologram[] hologram2 = new BaseHologram[1];
        Bukkit.getScheduler().runTaskLater(Library.getInstance(), () -> {
            iterator[0] = HologramRegistry.getHolograms().iterator();
            while (iterator[0].hasNext()) {
                hologram[0] = (Hologram) iterator[0].next();
                hologram2[0] = (BaseHologram) hologram[0];
                if ((hologram2[0].getViewers() == null || hologram2[0].getViewers().contains(event.getPlayer().getUniqueId())) && hologram2[0].getLocation().getWorld().equals(event.getPlayer().getWorld()) && hologram[0].getLocation().distanceSquared(event.getPlayer().getLocation()) <= 1600.0) {
                    hologram2[0].show(event.getPlayer());
                }
            }
        }, 20L);
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location to = event.getTo();
        final Location from = event.getFrom();
        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }
        for (final Hologram hologram : HologramRegistry.getHolograms()) {
            final BaseHologram hologram2 = (BaseHologram)hologram;
            if ((hologram2.getViewers() == null || hologram2.getViewers().contains(event.getPlayer().getUniqueId())) && hologram2.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
                if (!hologram2.currentWatchers.contains(player.getUniqueId()) && hologram.getLocation().distanceSquared(player.getLocation()) <= 1600.0) {
                    hologram2.show(player);
                }
                else {
                    if (!hologram2.currentWatchers.contains(player.getUniqueId()) || hologram.getLocation().distanceSquared(player.getLocation()) <= 1600.0) {
                        continue;
                    }
                    hologram2.destroy0(player);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        for (final Hologram hologram : HologramRegistry.getHolograms()) {
            final BaseHologram hologram2 = (BaseHologram)hologram;
            if ((hologram2.getViewers() == null || hologram2.getViewers().contains(event.getPlayer().getUniqueId())) && hologram2.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
                hologram2.show(event.getPlayer());
            }
        }
    }
    
    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        new BukkitRunnable() {
            public void run() {
                for (final Hologram hologram : HologramRegistry.getHolograms()) {
                    final BaseHologram hologram2 = (BaseHologram)hologram;
                    hologram2.destroy0(event.getPlayer());
                    if ((hologram2.getViewers() == null || hologram2.getViewers().contains(event.getPlayer().getUniqueId())) && hologram2.getLocation().getWorld().equals(event.getPlayer().getWorld())) {
                        hologram2.show(event.getPlayer());
                    }
                }
            }
        }.runTaskLater((Plugin) Library.getInstance(), 10L);
    }
}

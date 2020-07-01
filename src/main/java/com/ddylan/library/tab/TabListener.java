package com.ddylan.library.tab;

import com.ddylan.library.LibraryPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class TabListener implements Listener {

    private final TabHandler handler;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        new BukkitRunnable()
        {
            public void run() {
                handler.lib.getTabHandler().addPlayer(event.getPlayer());
            }
        }.runTaskLater(LibraryPlugin.getInstance(), 10L);
    }


    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        handler.lib.getTabHandler().removePlayer(event.getPlayer());
        TabLayout.remove(event.getPlayer());
    }

}

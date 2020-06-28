package com.ddylan.xlib.tab;

import com.ddylan.xlib.Library;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TabListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        new BukkitRunnable()
        {
            public void run() {
                Library.getInstance().getTabHandler().addPlayer(event.getPlayer());
            }
        }.runTaskLater(Library.getInstance(), 10L);
    }


    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Library.getInstance().getTabHandler().removePlayer(event.getPlayer());
        TabLayout.remove(event.getPlayer());
    }

}

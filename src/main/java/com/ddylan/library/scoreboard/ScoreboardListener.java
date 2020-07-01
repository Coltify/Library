package com.ddylan.library.scoreboard;

import com.ddylan.library.LibraryPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LibraryPlugin.getInstance().getScoreboardHandler().create(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        LibraryPlugin.getInstance().getScoreboardHandler().remove(event.getPlayer());
    }

}

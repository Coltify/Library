package com.ddylan.xlib.scoreboard;

import com.ddylan.xlib.Library;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Library.getInstance().getScoreboardHandler().create(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Library.getInstance().getScoreboardHandler().remove(event.getPlayer());
    }

}

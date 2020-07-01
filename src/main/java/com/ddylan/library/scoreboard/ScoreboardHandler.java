package com.ddylan.library.scoreboard;

import com.ddylan.library.LibraryPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardHandler {

    private Map<String, Scoreboard> boards = new ConcurrentHashMap<>();
    @Getter @Setter private ScoreboardConfiguration configuration = null;
    @Getter @Setter private int updateInterval = 2;

    public ScoreboardHandler() {
        if (LibraryPlugin.getInstance().getConfig().getBoolean("disableScoreboard", false)) {
            return;
        }

        new ScoreboardThread().start();
        LibraryPlugin.getInstance().getServer().getPluginManager().registerEvents(new ScoreboardListener(), LibraryPlugin.getInstance());
    }

    protected void create(Player player) {
        if (configuration != null) {
            boards.put(player.getName(), new Scoreboard(player));
        }
    }

    protected void updateScoreboard(Player player) {
        Scoreboard board = boards.get(player.getName());
        if (board != null) board.update();

    }

    protected void remove(Player player) {
        boards.remove(player.getName());
    }

}
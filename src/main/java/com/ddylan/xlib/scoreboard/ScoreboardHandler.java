package com.ddylan.xlib.scoreboard;

import com.ddylan.xlib.Library;
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
        if (Library.getInstance().getConfig().getBoolean("disableScoreboard", false)) {
            return;
        }

        new ScoreboardThread().start();
        Library.getInstance().getServer().getPluginManager().registerEvents(new ScoreboardListener(), Library.getInstance());
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
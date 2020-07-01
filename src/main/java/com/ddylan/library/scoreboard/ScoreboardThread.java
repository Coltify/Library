package com.ddylan.library.scoreboard;

import com.ddylan.library.LibraryPlugin;
import org.bukkit.entity.Player;

public class ScoreboardThread extends Thread {

    public ScoreboardThread() {
        super("Library - Scoreboard Thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            for (Player player : LibraryPlugin.getInstance().getServer().getOnlinePlayers()) {
                try {
                    LibraryPlugin.getInstance().getScoreboardHandler().updateScoreboard(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(LibraryPlugin.getInstance().getScoreboardHandler().getUpdateInterval() * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

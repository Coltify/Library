package com.ddylan.xlib.scoreboard;

import com.ddylan.xlib.Library;
import org.bukkit.entity.Player;

public class ScoreboardThread extends Thread {

    public ScoreboardThread() {
        super("xLib - Scoreboard Thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            for (Player player : Library.getInstance().getServer().getOnlinePlayers()) {
                try {
                    Library.getInstance().getScoreboardHandler().updateScoreboard(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(Library.getInstance().getScoreboardHandler().getUpdateInterval() * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

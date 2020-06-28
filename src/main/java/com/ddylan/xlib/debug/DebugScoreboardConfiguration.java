package com.ddylan.xlib.debug;

import com.ddylan.xlib.scoreboard.ScoreboardConfiguration;
import com.ddylan.xlib.scoreboard.TitleGetter;
import com.ddylan.xlib.util.Color;

public class DebugScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();
        configuration.setTitleGetter(new TitleGetter("&6&lMineHQ Network"));
        configuration.setScoreGetter((linkedList, player) -> {
            linkedList.addFirst(Color.GREEN + Color.SCOREBAORD_SEPARATOR);
            linkedList.add(Color.GRAY + "Rank: " + Color.RED + "Owner");
            linkedList.add(Color.WHITE + Color.SCOREBAORD_SEPARATOR);
        });

        return configuration;
    }

}

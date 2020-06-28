package com.ddylan.xlib.scoreboard;

import com.ddylan.xlib.util.Color;
import org.bukkit.entity.Player;

public class TitleGetter {

    private String defaultTitle;

    @Deprecated
    public TitleGetter(String defaultTitle) {
        this.defaultTitle = Color.translate(defaultTitle);
    }

    public TitleGetter() {

    }

    public String getTitle(Player player) {
        return this.defaultTitle;
    }

    public static TitleGetter forStaticString(final String staticString) {
        if (staticString != null) {
            return new TitleGetter() {
                public String getTitle(Player player) {
                    return staticString;
                }
            };
        }
        return null;
    }

}

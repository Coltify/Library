package com.ddylan.xlib.chat.defaults;

import com.ddylan.xlib.chat.ChatProvider;
import com.ddylan.xlib.util.Color;
import org.bukkit.entity.Player;

public class DefaultChatProvider extends ChatProvider {

    public DefaultChatProvider() {
        super("Default-Provider", '!', "defaultprovider", 1);
    }

    @Override
    public String format(Player player, String source) {
        return player.getDisplayName() + Color.RESET +  ": " + source;
    }

}

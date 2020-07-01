package com.ddylan.library.chat.defaults;

import com.ddylan.library.chat.ChatProvider;
import com.ddylan.library.util.Color;
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

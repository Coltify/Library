package com.ddylan.xlib.command.defaults;

import com.ddylan.xlib.command.Command;
import com.ddylan.xlib.command.Param;
import com.ddylan.xlib.util.Color;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommand {

    @Command(names = {"gm", "gamemode"}, permission = "op", description = "change gamemode")
    public static void gamemode(Player player, @Param(name = "mode") GameMode mode) {
        player.setGameMode(mode);
        player.sendMessage(Color.translate("&eYour gamemode has been changed to &b" + StringUtils.capitalize(mode.name().toLowerCase()) + "&e."));
    }

}

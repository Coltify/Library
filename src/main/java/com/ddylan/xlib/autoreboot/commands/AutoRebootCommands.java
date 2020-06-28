package com.ddylan.xlib.autoreboot.commands;

import com.ddylan.xlib.Library;
import com.ddylan.xlib.command.Command;
import com.ddylan.xlib.command.Param;
import com.ddylan.xlib.menu.menus.ConfirmMenu;
import com.ddylan.xlib.util.Color;
import com.ddylan.xlib.util.TimeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class AutoRebootCommands {

    @Command(names = "reboot", permission = "xlib.admin.reboot")
    public static void reboot(Player player, @Param(name = "time") String unparsedTime) {
        try {
            unparsedTime = unparsedTime.toLowerCase();
            int time = Math.toIntExact(TimeUtil.parseTime(unparsedTime));
            new ConfirmMenu(Color.translate("&6Are you sure?"), data -> {
                if (data) {
                    Library.getInstance().getAutoRebootHandler().rebootServer(time, TimeUnit.SECONDS);
                    player.sendMessage(Color.YELLOW + "Started auto reboot.");
                } else {
                    player.sendMessage(Color.translate("&7Server reboot aborted."));
                }
            }, true).openMenu(player);
        } catch (Exception e) {
            player.sendMessage(Color.RED + e.getMessage());
        }
    }

    @Command(names = "reboot cancel", permission = "xlib.admin.reboot")
    public static void rebootCancel(CommandSender sender) {
        if (!Library.getInstance().getAutoRebootHandler().isRebooting()) {
            sender.sendMessage(Color.RED + "No reboot is currently scheduled.");
        } else {
            Library.getInstance().getAutoRebootHandler().cancelReboot();

            /*
            if (yes || no || maybe() || so()) {
                Bukkit.getServer().shutdown();
            }
             */

            Library.getInstance().getServer().broadcastMessage(Color.RED + "⚠ " + Color.DARK_RED + Color.S + "-----------------------------" + Color.RED + " ⚠");
            Library.getInstance().getServer().broadcastMessage(Color.RED + "The server reboot has been cancelled.");
            Library.getInstance().getServer().broadcastMessage(Color.RED + "⚠ " + Color.DARK_RED + Color.S + "-----------------------------" + Color.RED + " ⚠");
        }
    }

}

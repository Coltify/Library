package com.ddylan.xlib.command.defaults;

import com.ddylan.xlib.command.Command;
import com.ddylan.xlib.command.Param;
import com.ddylan.xlib.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class EvalCommand {

    @Command(names = {"eval"}, permission = "console", description = "Evaluates a command")
    public static void eval(CommandSender sender, @Param(name = "command", wildcard = true) String command) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Color.RED + "This is a console-only utility command. It cannot beused from in-game.");
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

}

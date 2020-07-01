package com.ddylan.library.command.defaults;

import com.ddylan.library.LibraryPlugin;
import com.google.common.collect.Iterables;
import com.ddylan.library.command.Command;
import com.ddylan.library.command.Param;
import com.ddylan.library.util.Color;
import org.bukkit.entity.Player;

import java.util.List;

public class VisibilityDebugCommand {

    @Command(names = {"visibilitydebug", "debugvisibility", "visdebug", "cansee"}, permission = "")
    public static void visibilityDebug(Player sender, @Param(name = "viewer") Player viewer, @Param(name = "target") Player target) {
        List<String> lines = LibraryPlugin.getInstance().getVisibilityHandler().getDebugInfo(target, viewer);
        for (String debugLine : lines)
            sender.sendMessage(debugLine);
        boolean shouldBeAbleToSee = false;
        if (!Iterables.getLast(lines).contains("cannot"))
            shouldBeAbleToSee = true;
        boolean bukkit = viewer.canSee(target);
        if (shouldBeAbleToSee != bukkit) {
            sender.sendMessage(Color.DARK_RED + Color.BOLD + "Updating was not done correctly: " + viewer.getName() + " should be able to see " + target.getName() + " but cannot.");
        } else {
            sender.sendMessage(Color.GREEN + "Bukkit currently respects this result.");
        }
    }

}

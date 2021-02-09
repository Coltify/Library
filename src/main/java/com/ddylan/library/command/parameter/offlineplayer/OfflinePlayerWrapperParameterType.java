package com.ddylan.library.command.parameter.offlineplayer;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.command.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OfflinePlayerWrapperParameterType implements ParameterType<OfflinePlayerWrapper> {

    public OfflinePlayerWrapper transform(CommandSender sender, String source) {
        return new OfflinePlayerWrapper(source);
    }


    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!LibraryPlugin.getInstance().getVisibilityHandler().treatAsOnline(player, sender)) {
                continue;
            }
            completions.add(player.getName());
        }
        return completions;
    }
}

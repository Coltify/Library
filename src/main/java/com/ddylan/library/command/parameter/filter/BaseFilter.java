package com.ddylan.library.command.parameter.filter;

import com.ddylan.library.command.ParameterType;
import com.ddylan.library.util.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class BaseFilter implements ParameterType<String> {

    protected final Set<Pattern> bannedPatterns = new HashSet<>();

    @Override
    public String transform(CommandSender sender, String source) {
        for (Pattern bannedPattern : this.bannedPatterns) {
            if (bannedPattern.matcher(source).find()) {
                sender.sendMessage(Color.RED + "Command contains inappropriate content.");
                return null;
            }
        }
        return source;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> parameters, String source) {
        return new ArrayList<>();
    }
}

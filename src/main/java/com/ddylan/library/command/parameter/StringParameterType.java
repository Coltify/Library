package com.ddylan.library.command.parameter;

import com.ddylan.library.command.ParameterType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class StringParameterType implements ParameterType<String> {

    @Override
    public String transform(CommandSender sender, String source) {
        return source;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return Collections.singletonList(source);
    }

}

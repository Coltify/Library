package com.ddylan.library.command.bukkit;

import com.ddylan.library.command.CommandNode;
import com.ddylan.library.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

import java.util.Set;

public class ExtendedHelpTopic extends HelpTopic {

    private CommandNode node;

    public ExtendedHelpTopic(CommandNode node, Set<String> aliases) {
        this.node = node;
        this.name = "/" + node.getName();
        String description = node.getDescription();
        if (description.length() < 32) {
            this.shortText = description;
        } else {
            this.shortText = description.substring(0, 32);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Color.GOLD);
        sb.append("Description: ");
        sb.append(Color.WHITE);
        sb.append(node.getDescription());
        sb.append("\n");
        sb.append(Color.GOLD);
        sb.append("Usage: ");
        sb.append(Color.WHITE);
        sb.append(node.getUsageForHelpTopic());
        if (aliases != null && aliases.size() > 0) {
            sb.append("\n");
            sb.append(Color.GOLD);
            sb.append("Aliases: ");
            sb.append(Color.WHITE);
            sb.append(StringUtils.join(aliases, ", "));
        }
        this.fullText = sb.toString();
    }

    public boolean canSee(CommandSender commandSender) {
        return this.node.canUse(commandSender);
    }
    
}

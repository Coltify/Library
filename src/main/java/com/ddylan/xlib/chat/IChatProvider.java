package com.ddylan.xlib.chat;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface IChatProvider {

    String getName();
    String getLabel();
    Character getShortcut();
    Double getWeight();
    String format(Player player, String source);
    Set<UUID> getViewers();

}

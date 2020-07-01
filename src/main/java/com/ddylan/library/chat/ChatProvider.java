package com.ddylan.library.chat;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class ChatProvider implements IChatProvider {

    private final String name;
    private final char shortCut;
    private String label;
    private double weight;
    private Set<UUID> viewers;

    public ChatProvider(String name, char shortCut) {
        this.name = name;
        this.shortCut = shortCut;
        this.label = name.toLowerCase().replace(" ", "");
        this.weight = 0;
        this.viewers = new HashSet<>();
    }

    public ChatProvider(String name, char shortCut, String label) {
        this(name, shortCut);
        this.label = label;
    }

    public ChatProvider(String name, char shortCut, String label, double weight) {
        this(name, shortCut, label);
        this.weight = weight;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Character getShortcut() {
        return shortCut;
    }

    @Override
    public Double getWeight() {
        return weight;
    }

    @Override
    public abstract String format(Player player, String source);

    @Override
    public Set<UUID> getViewers() {
        return viewers;
    }
}

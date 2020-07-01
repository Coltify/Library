package com.ddylan.library.nametag;

import com.ddylan.library.LibraryPlugin;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;

@Getter
public abstract class NametagProvider {

    private String name;
    private int weight;

    @ConstructorProperties({"name", "weight"})
    public NametagProvider(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public static final NametagInfo createNametag(String prefix, String suffix) {
        return LibraryPlugin.getInstance().getNametagHandler().getOrCreate(prefix, suffix);
    }

    public abstract NametagInfo fetchNametag(Player toRefresh, Player refreshFor);

    protected static final class DefaultNametagProvider extends NametagProvider {

        public DefaultNametagProvider() {
            super("Default Provider", 0);
        }

        public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
            return createNametag("", "");
        }

    }
}

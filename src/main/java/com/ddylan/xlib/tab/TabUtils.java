package com.ddylan.xlib.tab;

import com.ddylan.xlib.Library;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TabUtils {

    private static Map<String, GameProfile> cache = new ConcurrentHashMap<>();

    public static boolean is18(Player player) {
        return ((((CraftPlayer)player).getHandle()).playerConnection.networkManager.getVersion() > 20);
    }

    public static GameProfile getOrCreateProfile(String name, UUID id) {
        GameProfile player = cache.get(name);
        if (player == null) {
            player = new GameProfile(id, name);
            player.getProperties().putAll(Objects.requireNonNull(Library.getInstance().getTabHandler().getDefaultPropertyMap()));
            cache.put(name, player);
        }
        return player;
    }

    public static GameProfile getOrCreateProfile(String name) {
        return getOrCreateProfile(name, new UUID(Library.RANDOM.nextLong(), Library.RANDOM.nextLong()));
    }

}

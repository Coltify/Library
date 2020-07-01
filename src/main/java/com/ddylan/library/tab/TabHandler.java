package com.ddylan.library.tab;

import com.ddylan.library.LibraryPlugin;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonParser;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;
import net.minecraft.util.com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class TabHandler {

    protected final LibraryPlugin lib;

    private final AtomicReference<Object> propertyMapSerializer = new AtomicReference<>();
    private final AtomicReference<Object> defaultPropertyMap = new AtomicReference<>();
    @Getter @Setter private LayoutProvider layoutProvider;
    private final Map<String, XTab> tabMap = new ConcurrentHashMap<>();

    public TabHandler(LibraryPlugin lib) {
        this.lib = lib;
        
        getDefaultPropertyMap();
        new TabThread(this).start();
        lib.getServer().getPluginManager().registerEvents(new TabListener(this), lib);
    }

    public PropertyMap.Serializer getPropertyMapSerializer() {
        Object value = propertyMapSerializer.get();

        if (value == null) {
            synchronized (propertyMapSerializer) {
                value = propertyMapSerializer.get();
                if (value == null) {
                    value = new PropertyMap.Serializer();
                }
            }
        }
        return (PropertyMap.Serializer) value;
    }

    public PropertyMap getDefaultPropertyMap() {
        Object value = defaultPropertyMap.get();
        if (value == null) {
            synchronized (defaultPropertyMap) {
                value = defaultPropertyMap.get();
                if (value == null) {
                    PropertyMap actualValue = fetchSkin();
                    value = (actualValue == null) ? defaultPropertyMap : actualValue;
                    defaultPropertyMap.set(value);
                }
            }
        }
        return (value == defaultPropertyMap) ? null : (PropertyMap) value;
    }

    private PropertyMap fetchSkin() {
        String propertyMap = lib.getXJedis().runBackboneRedisCommand(redis -> redis.get("propertyMap"));

        if (propertyMap != null && !propertyMap.isEmpty()) {
            Bukkit.getLogger().info("Using cached PropertyMap for skin...");
            JsonArray jsonObject = (new JsonParser()).parse(propertyMap).getAsJsonArray();
            return getPropertyMapSerializer().deserialize(jsonObject, null, null);
        }

        GameProfile profile = new GameProfile(UUID.fromString("6b22037d-c043-4271-94f2-adb00368bf16"), "Lightnen");
        YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        MinecraftSessionService sessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
        GameProfile profile1 = sessionService.fillProfileProperties(profile, true);

        final PropertyMap localPropertyMap = profile1.getProperties();

        lib.getXJedis().runBackboneRedisCommand(redis -> {
            Bukkit.getLogger().info("Caching PropertyMap for skin...");
            redis.setex("propertyMap", 3600, getPropertyMapSerializer().serialize(localPropertyMap, null, null).toString());
            return null;
        });



        return localPropertyMap;
    }

    public void updatePlayer(Player player) {
        if (tabMap.containsKey(player.getName())) {
            tabMap.get(player.getName()).update();
        }
    }

    public void addPlayer(Player player) {
        tabMap.put(player.getName(), new XTab(player));
    }

    public void removePlayer(Player player) {
        tabMap.remove(player.getName());
    }

}

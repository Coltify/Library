package com.ddylan.xlib.uuid.impl;

import com.ddylan.xlib.Library;
import com.ddylan.xlib.uuid.UUIDCacheable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RedisUUIDCache implements UUIDCacheable {

    private static Map<UUID, String> uuidToName = new ConcurrentHashMap<>();
    private static Map<String, UUID> nameToUuid = new ConcurrentHashMap<>();

    public RedisUUIDCache() {
        Library.getInstance().getXJedis().runBackboneRedisCommand(redis -> {
            Map<String, String> cache = redis.hgetAll("UUIDCacheable");

            for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                UUID uuid = UUID.fromString(cacheEntry.getKey());
                String name = cacheEntry.getValue();

                uuidToName.put(uuid, name);
                nameToUuid.put(name.toLowerCase(), uuid);
            }

            return null;
        });
    }

    public UUID uuid(String name) {
        return nameToUuid.get(name.toLowerCase());
    }

    public String name(UUID uuid) {
        return uuidToName.get(uuid);
    }

    public boolean ensure(UUID uuid) {
        if (String.valueOf(name(uuid)).equals("null")) {
            Library.getInstance().getLogger().warning(uuid + " didn't have a cached name.");
            return false;
        }
        return true;
    }

    public void update(final UUID uuid, final String name) {
        uuidToName.put(uuid, name);

        for (Map.Entry<String, UUID> entry : (new HashMap<>(nameToUuid)).entrySet()) {
            if (entry.getValue().equals(uuid)) {
                nameToUuid.remove(entry.getKey());
            }
        }

        nameToUuid.put(name.toLowerCase(), uuid);

        new BukkitRunnable() {
            public void run() {
                Library.getInstance().getXJedis().runBackboneRedisCommand(redis -> {
                    redis.hset("UUIDCacheable", uuid.toString(), name);
                    return null;
                });
            }
        }.runTaskAsynchronously((Library.getInstance()));
    }

}

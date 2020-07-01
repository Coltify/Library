package com.ddylan.library.uuid.impl;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.uuid.UUIDCacheable;
import com.ddylan.library.xpacket.XPacket;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedUUIDCache implements UUIDCacheable {

    private static Map<UUID, String> uuidToName = new ConcurrentHashMap<>();
    private static Map<String, UUID> nameToUuid = new ConcurrentHashMap<>();

    public DistributedUUIDCache() {
        LibraryPlugin.getInstance().getXJedis().runBackboneRedisCommand(redis -> {
            Map<String, String> cache = redis.hgetAll("UUIDCacheable");

            for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                UUID uuid = UUID.fromString(cacheEntry.getKey());
                String name = cacheEntry.getValue();

                DistributedUUIDCache.uuidToName.put(uuid, name);
                DistributedUUIDCache.nameToUuid.put(name.toLowerCase(), uuid);
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
            LibraryPlugin.getInstance().getLogger().warning(uuid + " didn't have a cached name.");
            return false;
        }
        return true;
    }

    public void update(UUID uuid, String name) {
        update0(uuid, name, true);
    }

    private void update0(final UUID uuid, final String name, boolean distributedToOthers) {
        uuidToName.put(uuid, name);


        for (Map.Entry<String, UUID> entry : (new HashMap<>(nameToUuid)).entrySet()) {
            if (entry.getValue().equals(uuid)) {
                nameToUuid.remove(entry.getKey());
            }
        }

        nameToUuid.put(name.toLowerCase(), uuid);

        if (distributedToOthers) {


            (new BukkitRunnable()
            {
                public void run() {
                    LibraryPlugin.getInstance().getXJedis().runBackboneRedisCommand(redis -> {
                        redis.hset("UUIDCacheable", uuid.toString(), name);
                        return null;
                    });
                }
            }).runTaskAsynchronously(LibraryPlugin.getInstance());

            DistributedUUIDCacheUpdatePacket packet = new DistributedUUIDCacheUpdatePacket(uuid, name);
            LibraryPlugin.getInstance().getXPacketHandler().sendToAll(packet);
        }
    }

    @Getter
    public static class DistributedUUIDCacheUpdatePacket implements XPacket {

        private String name;
        private UUID uuid;

        public DistributedUUIDCacheUpdatePacket(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public DistributedUUIDCacheUpdatePacket() {}

        public void onReceive() {
            if (LibraryPlugin.getInstance().getUuidCache().getImpl() instanceof DistributedUUIDCache)
                ((DistributedUUIDCache) LibraryPlugin.getInstance().getUuidCache().getImpl()).update0(this.uuid, this.name, false);
        } }

}

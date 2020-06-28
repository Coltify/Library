package com.ddylan.xlib.uuid;

import com.ddylan.xlib.Library;
import lombok.Getter;

import java.util.UUID;

public class UUIDCache {

    @Getter private UUIDCacheable impl = null;

    public UUIDCache() {
        try {
            impl = (UUIDCacheable)Class.forName(Library.getInstance().getConfig().getString("UUIDCacheable.Backend", "com.ddylan.xlib.uuid.impl.RedisUUIDCache")).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Library.getInstance().getServer().getPluginManager().registerEvents(new CacheListener(), Library.getInstance());
    }

    public UUID uuid(String name) {
        return impl.uuid(name);
    }

    public String name(UUID uuid) {
        return impl.name(uuid);
    }

    public boolean ensure(UUID uuid) {
        return impl.ensure(uuid);
    }
    
    public void update(UUID uuid, String name) {
        impl.update(uuid, name);
    }

}

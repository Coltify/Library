package com.ddylan.library.uuid;

import com.ddylan.library.LibraryPlugin;
import lombok.Getter;

import java.util.UUID;

public class UUIDCache {

    @Getter private UUIDCacheable impl = null;

    public UUIDCache() {
        try {
            impl = (UUIDCacheable)Class.forName(LibraryPlugin.getInstance().getConfig().getString("UUIDCacheable.Backend", "com.ddylan.xlib.uuid.impl.RedisUUIDCache")).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LibraryPlugin.getInstance().getServer().getPluginManager().registerEvents(new CacheListener(), LibraryPlugin.getInstance());
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

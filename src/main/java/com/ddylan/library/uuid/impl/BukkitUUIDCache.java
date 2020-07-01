package com.ddylan.library.uuid.impl;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.uuid.UUIDCacheable;

import java.util.UUID;

public class BukkitUUIDCache implements UUIDCacheable {

    @Override
    public UUID uuid(String name) {
        return LibraryPlugin.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
    }

    @Override
    public String name(UUID uuid) {
        return LibraryPlugin.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    @Override
    public boolean ensure(UUID uuid) {
        return true;
    }

    @Override
    public void update(UUID uuid, String name) {

    }

}

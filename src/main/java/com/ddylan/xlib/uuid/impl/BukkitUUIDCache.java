package com.ddylan.xlib.uuid.impl;

import com.ddylan.xlib.Library;
import com.ddylan.xlib.uuid.UUIDCacheable;

import java.util.UUID;

public class BukkitUUIDCache implements UUIDCacheable {

    @Override
    public UUID uuid(String name) {
        return Library.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
    }

    @Override
    public String name(UUID uuid) {
        return Library.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    @Override
    public boolean ensure(UUID uuid) {
        return true;
    }

    @Override
    public void update(UUID uuid, String name) {

    }

}

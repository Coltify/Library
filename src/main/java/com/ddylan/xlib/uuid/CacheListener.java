package com.ddylan.xlib.uuid;

import com.ddylan.xlib.Library;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Library.getInstance().getUuidCache().update(event.getUniqueId(), event.getName());
    }

}

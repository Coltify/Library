package com.ddylan.library.uuid;

import com.ddylan.library.LibraryPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        LibraryPlugin.getInstance().getUuidCache().update(event.getUniqueId(), event.getName());
    }

}

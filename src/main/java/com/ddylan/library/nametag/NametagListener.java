package com.ddylan.library.nametag;

import com.ddylan.library.LibraryPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class NametagListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (LibraryPlugin.getInstance().getNametagHandler().isInitiated()) {}
        event.getPlayer().setMetadata("LibraryNametag-LoggedIn", new FixedMetadataValue(LibraryPlugin.getInstance(), true));
        LibraryPlugin.getInstance().getNametagHandler().initiatePlayer(event.getPlayer());
        LibraryPlugin.getInstance().getNametagHandler().reloadPlayer(event.getPlayer());
        LibraryPlugin.getInstance().getNametagHandler().reloadOthersFor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("LibraryNametag-LoggedIn", LibraryPlugin.getInstance());
        LibraryPlugin.getInstance().getNametagHandler().getTeamMap().remove(event.getPlayer().getName());
    }

}

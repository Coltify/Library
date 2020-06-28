package com.ddylan.xlib.nametag;

import com.ddylan.xlib.Library;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class NametagListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Library.getInstance().getNametagHandler().isInitiated()) {}
        event.getPlayer().setMetadata("xLibNametag-LoggedIn", new FixedMetadataValue(Library.getInstance(), true));
        Library.getInstance().getNametagHandler().initiatePlayer(event.getPlayer());
        Library.getInstance().getNametagHandler().reloadPlayer(event.getPlayer());
        Library.getInstance().getNametagHandler().reloadOthersFor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("xLibNametag-LoggedIn", Library.getInstance());
        Library.getInstance().getNametagHandler().getTeamMap().remove(event.getPlayer().getName());
    }

}

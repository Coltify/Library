package com.ddylan.xlib.lunar;

import com.ddylan.xlib.Library;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LunarListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            Library.getInstance().getLunarAPI().giveAllStaffModules(event.getPlayer());
        }
    }

}

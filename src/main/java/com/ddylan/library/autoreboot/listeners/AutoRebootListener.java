package com.ddylan.library.autoreboot.listeners;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.event.HourEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class AutoRebootListener implements Listener {

    @EventHandler
    public void onHour(HourEvent event) {
        if (LibraryPlugin.getInstance().getAutoRebootHandler().getRebootTimes().contains(event.getHour())) {
            LibraryPlugin.getInstance().getAutoRebootHandler().rebootServer(5, TimeUnit.MINUTES);
        }
    }

}

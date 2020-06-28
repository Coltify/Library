package com.ddylan.xlib.autoreboot.listeners;

import com.ddylan.xlib.Library;
import com.ddylan.xlib.event.HourEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class AutoRebootListener implements Listener {

    @EventHandler
    public void onHour(HourEvent event) {
        if (Library.getInstance().getAutoRebootHandler().getRebootTimes().contains(event.getHour())) {
            Library.getInstance().getAutoRebootHandler().rebootServer(5, TimeUnit.MINUTES);
        }
    }

}

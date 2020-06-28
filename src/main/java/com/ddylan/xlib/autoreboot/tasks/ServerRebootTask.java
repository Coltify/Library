package com.ddylan.xlib.autoreboot.tasks;

import com.ddylan.xlib.Library;
import com.ddylan.xlib.util.Color;
import com.ddylan.xlib.util.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class ServerRebootTask extends BukkitRunnable {

    @Getter private int secondsRemaining;

    private boolean wasWhitelisted;

    public ServerRebootTask(int timeUnitAmount, TimeUnit timeUnit) {
        this.secondsRemaining = Math.toIntExact(timeUnit.toSeconds(timeUnitAmount));
        this.wasWhitelisted = Library.getInstance().getServer().hasWhitelist();
    }

    @Override
    public void run() {
        if (this.secondsRemaining == 300) {
            Library.getInstance().getServer().setWhitelist(true);
        } else if (this.secondsRemaining == 0) {
            Library.getInstance().getServer().setWhitelist(this.wasWhitelisted);
            Library.getInstance().getServer().shutdown();
        }

        switch (this.secondsRemaining) {
            case 5:
            case 10:
            case 15:
            case 30:
            case 60:
            case 120:
            case 180:
            case 240:
            case 300:
                Library.getInstance().getServer().broadcastMessage(Color.RED + "⚠ " + Color.DARK_RED + Color.S + "-----------------------------" + Color.RED + " ⚠");
                Library.getInstance().getServer().broadcastMessage(Color.RED + "Server rebooting in " + TimeUtil.formatIntoDetailedString(this.secondsRemaining) + ".");
                Library.getInstance().getServer().broadcastMessage(Color.RED + "⚠ " + Color.DARK_RED + Color.S + "-----------------------------" + Color.RED + " ⚠");
                break;
        }

        this.secondsRemaining--;
    }


    public synchronized void cancel() throws IllegalStateException {
        super.cancel();

        Bukkit.setWhitelist(this.wasWhitelisted);
    }
    
}

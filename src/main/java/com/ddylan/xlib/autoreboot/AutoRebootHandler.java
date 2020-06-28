package com.ddylan.xlib.autoreboot;

import com.ddylan.xlib.autoreboot.commands.AutoRebootCommands;
import com.ddylan.xlib.Library;
import com.ddylan.xlib.autoreboot.listeners.AutoRebootListener;
import com.ddylan.xlib.autoreboot.tasks.ServerRebootTask;
import com.ddylan.xlib.event.HalfHourEvent;
import com.ddylan.xlib.event.HourEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoRebootHandler {

    @Getter private List<Integer> rebootTimes;
    @Getter private boolean initiated = false;
    private ServerRebootTask serverRebootTask = null;

    public AutoRebootHandler() {
        rebootTimes = ImmutableList.copyOf(Library.getInstance().getConfig().getIntegerList("AutoRebootTimes"));
        Library.getInstance().getCommandHandler().registerClass(AutoRebootCommands.class);
        Library.getInstance().getServer().getPluginManager().registerEvents(new AutoRebootListener(), Library.getInstance());

        setupHourEvents();
    }

    private void setupHourEvents() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((new ThreadFactoryBuilder()).setNameFormat("xLib - Hour Event Thread").setDaemon(true).build());
        int minOfHour = Calendar.getInstance().get(12);
        int minToHour = 60 - minOfHour;
        int minToHalfHour = (minToHour >= 30) ? minToHour : (30 - minOfHour);

        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(Library.getInstance(), () -> {
            Bukkit.getServer().getPluginManager().callEvent(new HourEvent(minOfHour));
        }), minToHour, 60L, TimeUnit.MINUTES);

        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(Library.getInstance(), () -> {
            Bukkit.getServer().getPluginManager().callEvent(new HalfHourEvent(minOfHour, minOfHour));
        }), minToHalfHour, 30L, TimeUnit.MINUTES);
    }

    @Deprecated
    public void rebootServer(int seconds) {
        rebootServer(seconds, TimeUnit.SECONDS);
    }

    public void rebootServer(int unitAmount, TimeUnit timeUnit) {
        if (isRebooting()) {
            throw new IllegalStateException("Reboot already in progress");
        }
        serverRebootTask = new ServerRebootTask(unitAmount, timeUnit);
        serverRebootTask.runTaskTimer(Library.getInstance(), 20L, 20L);
    }

    public boolean isRebooting() {
        return serverRebootTask != null;
    }

    public int getRebootSecondsRemaining() {
        if (!isRebooting()) {
            return -1;
        }

        return serverRebootTask.getSecondsRemaining();
    }

    public void cancelReboot() {
        if (isRebooting()) {
            serverRebootTask.cancel();
            serverRebootTask = null;
        }
    }

}

package com.ddylan.xlib.tab;

import com.ddylan.xlib.Library;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TabThread extends Thread {

    private Plugin protocolLib = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");

    public TabThread() {
        setName("eLib - Tab Thread");
        setDaemon(true);
    }

    public void run() {
        while(Library.getInstance().isEnabled() && protocolLib != null && protocolLib.isEnabled()) {
            for (Player player : Library.getInstance().getServer().getOnlinePlayers()) {
                try {
                    Library.getInstance().getTabHandler().updatePlayer(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            Thread.sleep(250L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

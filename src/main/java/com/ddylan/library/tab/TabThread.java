package com.ddylan.library.tab;

import com.ddylan.library.LibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TabThread extends Thread {

    private TabHandler tabHandler;
    private Plugin protocolLib = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");

    public TabThread(TabHandler tabHandler) {
        setName("Library - Tab Thread");
        setDaemon(true);
    }

    public void run() {
        while(LibraryPlugin.getInstance().isEnabled() && protocolLib != null && protocolLib.isEnabled()) {
            for (Player player : LibraryPlugin.getInstance().getServer().getOnlinePlayers()) {
                try {
                    LibraryPlugin.getInstance().getTabHandler().updatePlayer(player);
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

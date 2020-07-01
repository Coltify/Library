package com.ddylan.library.util;

import com.lunarclient.bukkitapi.LunarClientAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class LunarUtil {

    public static LunarClientAPI getLunarHook(JavaPlugin plugin) {
        return (LunarClientAPI) plugin.getServer().getPluginManager().getPlugin("LunarClient-API");
    }

}

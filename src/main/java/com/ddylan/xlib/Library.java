package com.ddylan.xlib;

import com.ddylan.xlib.autoreboot.AutoRebootHandler;
import com.ddylan.xlib.boss.BossBarHandler;
import com.ddylan.xlib.chat.ChatHandler;
import com.ddylan.xlib.command.CommandHandler;
import com.ddylan.xlib.economy.EconomyHandler;
import com.ddylan.xlib.jedis.XJedis;
import com.ddylan.xlib.lunar.LunarHandler;
import com.ddylan.xlib.menu.MenuHandler;
import com.ddylan.xlib.nametag.NametagHandler;
import com.ddylan.xlib.scoreboard.ScoreboardHandler;
import com.ddylan.xlib.tab.TabHandler;
import com.ddylan.xlib.uuid.UUIDCache;
import com.ddylan.xlib.visibility.VisibilityHandler;
import com.ddylan.xlib.xpacket.XPacketHandler;
import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

@Getter
public class Library extends JavaPlugin {

    public static final Random RANDOM = new Random();

    private static Library instance;

    private XJedis xJedis;
    private UUIDCache uuidCache;
    private CommandHandler commandHandler;
    private NametagHandler nametagHandler;
    private ScoreboardHandler scoreboardHandler;
    private BossBarHandler bossBarHandler;
    private MenuHandler menuHandler;
    private AutoRebootHandler autoRebootHandler;
    private LunarHandler lunarHandler;
    private VisibilityHandler visibilityHandler;
    private XPacketHandler xPacketHandler;
    private TabHandler tabHandler;
    private ChatHandler chatHandler;
    private EconomyHandler economyHandler;

    private LunarClientAPI lunarAPI;

    /**
     * Singleton instance getter.
     * @return instance of xLib plugin
     */
    public static Library getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        lunarAPI = (LunarClientAPI) getServer().getPluginManager().getPlugin("LunarClient-API");

        //  possibly librato?
        //  mongo impl
        xJedis = new XJedis();
        uuidCache = new UUIDCache();
        commandHandler = new CommandHandler();
        nametagHandler = new NametagHandler();
        scoreboardHandler = new ScoreboardHandler();
        bossBarHandler = new BossBarHandler();
        menuHandler = new MenuHandler();
        autoRebootHandler = new AutoRebootHandler();
        lunarHandler = new LunarHandler();
        visibilityHandler = new VisibilityHandler();
        xPacketHandler = new XPacketHandler();
        tabHandler = new TabHandler();
        chatHandler = new ChatHandler(instance);
        economyHandler = new EconomyHandler();
    }

    @Override
    public void onDisable() {

    }

}

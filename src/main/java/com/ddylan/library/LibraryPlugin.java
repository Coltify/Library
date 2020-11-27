package com.ddylan.library;

import com.ddylan.library.autoreboot.AutoRebootHandler;
import com.ddylan.library.boss.BossBarHandler;
import com.ddylan.library.chat.ChatHandler;
import com.ddylan.library.command.CommandHandler;
import com.ddylan.library.economy.EconomyHandler;
import com.ddylan.library.jedis.XJedis;
import com.ddylan.library.lunar.LunarHandler;
import com.ddylan.library.menu.MenuHandler;
import com.ddylan.library.nametag.NametagHandler;
import com.ddylan.library.scoreboard.ScoreboardHandler;
import com.ddylan.library.tab.TabHandler;
import com.ddylan.library.uuid.UUIDCache;
import com.ddylan.library.visibility.VisibilityHandler;
import com.ddylan.library.xpacket.XPacketHandler;
import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

@Getter
public class LibraryPlugin extends JavaPlugin {

    public static final Random RANDOM = new Random();

    private static LibraryPlugin instance;

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
     * @return instance of Library plugin
     */
    public static LibraryPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

//        lunarAPI = (LunarClientAPI) getServer().getPluginManager().getPlugin("LunarClient-API");

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
        tabHandler = new TabHandler(instance);
        chatHandler = new ChatHandler(instance);
        economyHandler = new EconomyHandler(instance);
    }

    @Override
    public void onDisable() {

    }

}

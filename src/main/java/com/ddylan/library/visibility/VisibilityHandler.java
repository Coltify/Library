package com.ddylan.library.visibility;

import com.ddylan.library.LibraryPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VisibilityHandler {

    private final Map<String, IVisibilityHandler> handlers = new LinkedHashMap<>();
    private final Map<String, OverrideHandler> overrideHandlers = new LinkedHashMap<>();

    public VisibilityHandler() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerJoin(PlayerJoinEvent event) {
                update(event.getPlayer());
            }

            @EventHandler(priority = EventPriority.LOWEST)
            public void onTabComplete(PlayerChatTabCompleteEvent event) {
                String token = event.getLastToken();
                Collection<String> completions = event.getTabCompletions();
                completions.clear();
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (!treatAsOnline(target, event.getPlayer()))
                        continue;
                    if (StringUtils.startsWithIgnoreCase(target.getName(), token))
                        completions.add(target.getName());
                }
            }
        }, LibraryPlugin.getInstance());

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    update(player);
                }
            }
        }.runTaskTimer(LibraryPlugin.getInstance(), 0, 1);

    }

    public void registerHandler(String identifier, IVisibilityHandler handler) {
        handlers.put(identifier, handler);
    }

    public void registerOverride(String identifier, OverrideHandler handler) {
        overrideHandlers.put(identifier, handler);
    }

    public void update(Player player) {
        if (handlers.isEmpty() && overrideHandlers.isEmpty())
            return;
        updateAllTo(player);
        updateToAll(player);
    }

    @Deprecated
    public void updateAllTo(Player viewer) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
                continue;
            }
            viewer.showPlayer(target);
        }
    }

    @Deprecated
    public void updateToAll(Player target) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
                continue;
            }
            viewer.showPlayer(target);
        }
    }

    public boolean treatAsOnline(Player target, Player viewer) {
        return (viewer.canSee(target) || !target.hasMetadata("invisible") || viewer.hasPermission("alfax.staff"));
    }

    private boolean shouldSee(Player target, Player viewer) {
        for (OverrideHandler handler : overrideHandlers.values()) {
            if (handler.getAction(target, viewer) == OverrideAction.SHOW)
                return true;
        }
        for (IVisibilityHandler handler : handlers.values()) {
            if (handler.getAction(target, viewer) == VisibilityAction.HIDE)
                return false;
        }
        return true;
    }

    public List<String> getDebugInfo(Player target, Player viewer) {
        List<String> debug = new ArrayList<>();
        Boolean canSee = null;
        for (Map.Entry<String, OverrideHandler> entry : overrideHandlers.entrySet()) {
            OverrideHandler handler = entry.getValue();
            OverrideAction action = handler.getAction(target, viewer);
            ChatColor color = ChatColor.GRAY;
            if (action == OverrideAction.SHOW &&
                    canSee == null) {
                canSee = Boolean.TRUE;
                color = ChatColor.GREEN;
            }
            debug.add(color + "Overriding Handler: \"" + entry.getKey() + "\": " + action);
        }
        for (Map.Entry<String, IVisibilityHandler> entry : handlers.entrySet()) {
            IVisibilityHandler handler = entry.getValue();
            VisibilityAction action = handler.getAction(target, viewer);
            ChatColor color = ChatColor.GRAY;
            if (action == VisibilityAction.HIDE &&
                    canSee == null) {
                canSee = Boolean.FALSE;
                color = ChatColor.GREEN;
            }
            debug.add(color + "Normal Handler: \"" + entry.getKey() + "\": " + action);
        }
        if (canSee == null)
            canSee = Boolean.TRUE;
        debug.add(ChatColor.AQUA + "Result: " + viewer.getName() + " " + (canSee ? "can" : "cannot") + " see " + target.getName());
        return debug;
    }

}

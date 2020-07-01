package com.ddylan.library.chat;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@AllArgsConstructor
public class ChatListener implements Listener {

    private final ChatHandler chatHandler;

    @EventHandler(priority = EventPriority.LOWEST)
    public void cancel(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        char shortcut = message.charAt(0);
        IChatProvider provider;

        if (chatHandler.getByShortcut(shortcut) != null) {
            provider = chatHandler.getByShortcut(shortcut);
            message = message.substring(1);
        } else {
            provider = chatHandler.getActiveProvider();
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (provider.getViewers().contains(online.getUniqueId())) {
                online.sendMessage(provider.format(player, message));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        chatHandler.getByShortcut('!').getViewers().add(event.getUniqueId());
    }

}

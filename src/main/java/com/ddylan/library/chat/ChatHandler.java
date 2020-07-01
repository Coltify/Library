package com.ddylan.library.chat;

import com.ddylan.library.chat.defaults.DefaultChatProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ChatHandler {

    private Map<String, IChatProvider> chatProviders;

    public ChatHandler(JavaPlugin plugin) {
        chatProviders = new HashMap<>();

        registerProvider(new DefaultChatProvider());
        plugin.getServer().getPluginManager().registerEvents(new ChatListener(this), plugin);
    }

    public void registerProvider(ChatProvider chatProvider) {
        chatProviders.put(chatProvider.getName(), chatProvider);
    }

    public void unregisterProvider(ChatProvider chatProvider) {
        //  We want to keep the default chat provider (public chat) so we don't allow anyone to remove it
        if (!chatProvider.getName().equalsIgnoreCase("default provider")) {
            chatProviders.remove(chatProvider.getName());
        }
    }

    public IChatProvider getDefaultProvider() {
        return chatProviders.get("default provider");
    }

    public IChatProvider getActiveProvider() {
        IChatProvider toReturn = null;
        double lastWeight = -1.0d;
        for (IChatProvider chatProvider : chatProviders.values()) {
            if (chatProvider.getWeight() > lastWeight) {
                lastWeight = chatProvider.getWeight();
                toReturn = chatProvider;
            }
        }
        return toReturn;
    }

    public IChatProvider getByShortcut(char shortcut) {
        for (IChatProvider chatProvider : chatProviders.values()) {
            if (chatProvider.getShortcut().equals(shortcut)) {
                return chatProvider;
            }
        }
        return null;
    }

}

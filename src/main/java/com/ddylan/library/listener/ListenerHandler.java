package com.ddylan.library.listener;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.util.ClassUtils;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ListenerHandler {

    public ListenerHandler(LibraryPlugin library) {
        loadListenersFromPackage(library, "me.ohvalsgod.library.listener.listeners");
        loadListenersFromPackage(library, "me.ohvalsgod.library.gui");
    }

    /**
     * Registers all listeners from the given package with the given plugin.
     *
     * @param plugin      The plugin responsible for these listeners. This is here
     *                    because the .getClassesInPackage method requires it (for no real reason)
     * @param packageName The package to load listeners from. Example: "me.ohvalsgod.thads.listeners"
     */
    public static void loadListenersFromPackage(Plugin plugin, String packageName) {
        for (Class<?> clazz : ClassUtils.getClassesInPackage(plugin, packageName)) {
            if (isListener(clazz)) {
                try {
                    plugin.getServer().getPluginManager().registerEvents((org.bukkit.event.Listener) clazz.newInstance(), plugin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if the given class implements the {@link org.bukkit.event.Listener} interface.
     *
     * @param clazz     The class to check
     * @return          If the class implements the {@link org.bukkit.event.Listener} interface
     */
    public static boolean isListener(Class<?> clazz) {
        for (Class<?> interfaze : clazz.getInterfaces()) {
            if (interfaze == Listener.class) {
                return true;
            }
        }
        return false;
    }


}

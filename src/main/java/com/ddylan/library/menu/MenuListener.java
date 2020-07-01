package com.ddylan.library.menu;

import com.ddylan.library.LibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu openExtendedMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openExtendedMenu != null) {
            if (event.getSlot() != event.getRawSlot()) {
                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                }

                return;
            }

            if (openExtendedMenu.getButtons().containsKey(event.getSlot())) {
                Button button = openExtendedMenu.getButtons().get(event.getSlot());
                boolean cancel = button.shouldCancel(player, event.getClick());

                if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);

                    if (event.getCurrentItem() != null) {
                        player.getInventory().addItem(event.getCurrentItem());
                    }
                } else {
                    event.setCancelled(cancel);
                }

                button.clicked(player, event.getClick());
                button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());
                button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton(), event.getCurrentItem(),  event.getCursor());

                if (Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                    Menu newExtendedMenu = Menu.currentlyOpenedMenus.get(player.getName());

                    if (newExtendedMenu == openExtendedMenu) {
                        boolean buttonUpdate = button.shouldUpdate(player, event.getClick());

                        if ((newExtendedMenu.isUpdateAfterClick() && buttonUpdate) || buttonUpdate) {
                            openExtendedMenu.setClosedByMenu(true);
                            newExtendedMenu.openMenu(player);
                        }
                    }
                } else if (button.shouldUpdate(player, event.getClick())) {
                    openExtendedMenu.setClosedByMenu(true);
                    openExtendedMenu.openMenu(player);
                }

                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(LibraryPlugin.getInstance(), player::updateInventory, 1L);
                }
            } else {
                if (event.getCurrentItem() != null) {
                    event.setCancelled(true);
                }

                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu openExtendedMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openExtendedMenu != null) {
            openExtendedMenu.onClose(player);

            Menu.currentlyOpenedMenus.remove(player.getName());
        }
    }

}
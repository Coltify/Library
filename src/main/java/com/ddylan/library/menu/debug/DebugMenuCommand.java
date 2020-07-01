package com.ddylan.library.menu.debug;

import com.ddylan.library.command.Command;
import com.ddylan.library.menu.Button;
import com.ddylan.library.menu.Menu;
import com.ddylan.library.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DebugMenuCommand {

    @Command(names = "debugmenu", permission = "xlib.debugmenu")
    public static void debugMenu(Player player) {
        new DebugMenu().openMenu(player);
        for (int i = 0; i < 40; i++) {
            player.getInventory().setItem(i, new ItemBuilder(Material.GLASS).amount(i).build());
        }
    }


    public static class DebugMenu extends Menu {

        @Override
        public String getTitle(Player player) {
            return "Debug menu";
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> buttons = new HashMap<>();

            for (int i = 0; i < 53; i++) {
                int finalI = i;
                buttons.put(i, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(Material.GLASS).amount(finalI).build();
                    }
                });
            }

            return buttons;
        }
    }

}

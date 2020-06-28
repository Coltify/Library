package com.ddylan.xlib.menu;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public abstract class Button {

    public static void outline(Map<Integer, Button> buttons, Button filler, boolean top, boolean paginated) {

        if (!paginated) {
            buttons.put(0, filler);
            buttons.put(8, filler);
        }

        if (top) {
            for (int i = 1; i < 8; i++) {
                buttons.put(i, filler);
            }
        }
        buttons.put(9, filler);
        buttons.put(17, filler);
        buttons.put(18, filler);
        buttons.put(26, filler);
        buttons.put(27, filler);
        buttons.put(35, filler);
        buttons.put(36, filler);
        buttons.put(44, filler);

        for (int i = 45; i < 54; i++) {
            buttons.put(i, filler);
        }
    }

    public static Button placeholder(final ItemStack itemStack) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return itemStack;
            }
        };
    }

    public static Button placeholder(final Material material) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemStack(material);
            }
        };
    }

    public static Button placeholder(final Material material, final byte data, String... title) {
        return (new Button() {
            public ItemStack getButtonItem(Player player) {
                ItemStack it = new ItemStack(material, 1, data);
                ItemMeta meta = it.getItemMeta();

                meta.setDisplayName(StringUtils.join(title));
                it.setItemMeta(meta);

                return it;
            }
        });
    }

    public abstract ItemStack getButtonItem(Player player);

    public void clicked(Player player, ClickType clickType) {

    }

    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {}

    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton, ItemStack currentItem, ItemStack cursor) {

    }

    public boolean shouldCancel(Player player, ClickType clickType) {
        return (true);
    }

    public boolean shouldUpdate(Player player, ClickType clickType) {
        return (false);
    }

    public static void playFail(Player player) {
        player.playSound(player.getLocation(), Sound.DIG_GRASS, 20F, 0.1F);

    }

    public static void playSuccess(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20F, 15F);
    }

    public static void playNeutral(Player player) {
        player.playSound(player.getLocation(), Sound.CLICK, 20F, 1F);
    }

}
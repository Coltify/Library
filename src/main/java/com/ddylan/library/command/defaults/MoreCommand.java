package com.ddylan.library.command.defaults;

import com.ddylan.library.command.Command;
import com.ddylan.library.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MoreCommand {

    @Command(names = {"more", "stack"}, permission = "op", description = "Get more of the item in your hand.")
    public static void more(Player player) {
        if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand().getAmount() >= 64) {
            player.sendMessage(Color.translate("&cYou cannot get more of &4" + StringUtils.capitaliseAllWords(player.getItemInHand().getType().name().replace("_", " ").toLowerCase()) + "&c."));
            player.playSound(player.getLocation(), Sound.IRONGOLEM_THROW, 1, 1);
            return;
        }
        player.getItemInHand().setAmount(64);
        player.updateInventory();
        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
    }

}

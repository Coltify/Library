package com.ddylan.library.command.defaults;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.command.Command;
import com.ddylan.library.util.Color;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class BuildCommand {

    @Command(names = "build", permission = "op")
    public static void build(Player sender) {
        if (sender.hasMetadata("build")) {
            sender.removeMetadata("build", LibraryPlugin.getInstance());
        } else {
            sender.setMetadata("build", new FixedMetadataValue(LibraryPlugin.getInstance(), Boolean.TRUE));
        }
        sender.sendMessage(Color.translate(String.format("&eYou are %s &ein build mode.", (sender.hasMetadata("build") ? "&anow":"&cno longer"))));
    }

}

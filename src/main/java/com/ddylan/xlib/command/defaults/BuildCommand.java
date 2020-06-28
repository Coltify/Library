package com.ddylan.xlib.command.defaults;

import com.ddylan.xlib.Library;
import com.ddylan.xlib.command.Command;
import com.ddylan.xlib.util.Color;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class BuildCommand {

    @Command(names = "build", permission = "op")
    public static void build(Player sender) {
        if (sender.hasMetadata("build")) {
            sender.removeMetadata("build", Library.getInstance());
        } else {
            sender.setMetadata("build", new FixedMetadataValue(Library.getInstance(), Boolean.TRUE));
        }
        sender.sendMessage(Color.translate(String.format("&eYou are %s &ein build mode.", (sender.hasMetadata("build") ? "&anow":"&cno longer"))));
    }

}

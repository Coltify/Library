package com.ddylan.xlib.debug;

import com.ddylan.xlib.nametag.NametagInfo;
import com.ddylan.xlib.nametag.NametagProvider;
import org.bukkit.entity.Player;

public class DebugNametagProvider extends NametagProvider {

    public DebugNametagProvider() {
        super("xLib-Debug-Nametag", 1);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        return createNametag("§4", "");
    }
}

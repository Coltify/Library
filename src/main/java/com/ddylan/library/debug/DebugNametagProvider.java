package com.ddylan.library.debug;

import com.ddylan.library.nametag.NametagInfo;
import com.ddylan.library.nametag.NametagProvider;
import org.bukkit.entity.Player;

public class DebugNametagProvider extends NametagProvider {

    public DebugNametagProvider() {
        super("Library-Debug-Nametag", 1);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        return createNametag("§4", "");
    }
}

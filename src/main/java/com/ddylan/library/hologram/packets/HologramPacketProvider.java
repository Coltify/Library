package com.ddylan.library.hologram.packets;

import com.ddylan.library.hologram.HologramLine;
import org.bukkit.Location;

public interface HologramPacketProvider
{
    HologramPacket getPacketsFor(final Location p0, final HologramLine p1);
}

package com.ddylan.xlib.hologram.packets;

import com.ddylan.xlib.hologram.HologramLine;
import org.bukkit.Location;

public interface HologramPacketProvider
{
    HologramPacket getPacketsFor(final Location p0, final HologramLine p1);
}

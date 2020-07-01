package com.ddylan.library.hologram;

import org.bukkit.Location;

import java.util.Collection;
import java.util.List;

public interface Hologram
{
    void send();
    
    void destroy();
    
    void addLines(final String... p0);
    
    void setLine(final int p0, final String p1);
    
    void setLines(final Collection<String> p0);
    
    List<String> getLines();
    
    Location getLocation();
}

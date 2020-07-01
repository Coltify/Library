package com.ddylan.library.hologram;

import java.util.LinkedHashSet;
import java.util.Set;

public final class HologramRegistry
{
    private static final Set<Hologram> holograms;
    
    public static Set<Hologram> getHolograms() {
        return HologramRegistry.holograms;
    }
    
    static {
        holograms = new LinkedHashSet<Hologram>();
    }
}

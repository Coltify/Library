package com.ddylan.xlib.hologram;

import org.bukkit.Location;

import java.util.*;

public class HologramBuilder
{
    private Collection<UUID> viewers;
    private Location location;
    protected List<String> lines;
    
    protected HologramBuilder(final Collection<UUID> viewers) {
        this.lines = new ArrayList<String>();
        this.viewers = viewers;
    }
    
    public HologramBuilder addLines(final Iterable<String> lines) {
        for (final String line : lines) {
            this.lines.add(line);
        }
        return this;
    }
    
    public HologramBuilder addLines(final String... lines) {
        this.lines.addAll(Arrays.asList(lines));
        return this;
    }
    
    public HologramBuilder at(final Location location) {
        this.location = location;
        return this;
    }
    
    public UpdatingHologramBuilder updates() {
        return new UpdatingHologramBuilder(this);
    }
    
    public Hologram build() {
        return new BaseHologram(this);
    }
    
    protected Collection<UUID> getViewers() {
        return this.viewers;
    }
    
    protected Location getLocation() {
        return this.location;
    }
    
    protected List<String> getLines() {
        return this.lines;
    }
}

package com.ddylan.xlib.hologram;

import com.ddylan.xlib.util.EntityUtils;

public class HologramLine
{
    private final int skullId;
    private final int horseId;
    private String text;
    
    public HologramLine(final String text) {
        this.skullId = EntityUtils.getFakeEntityId();
        this.horseId = EntityUtils.getFakeEntityId();
        this.text = text;
    }
    
    public int getSkullId() {
        return this.skullId;
    }
    
    public int getHorseId() {
        return this.horseId;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
}

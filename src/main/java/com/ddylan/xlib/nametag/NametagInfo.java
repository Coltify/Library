package com.ddylan.xlib.nametag;

import com.ddylan.xlib.packet.ScoreboardTeamPacketMod;
import lombok.Getter;

import java.util.Collections;

@Getter
public class NametagInfo {

    private String name, prefix, suffix;
    private ScoreboardTeamPacketMod packet;

    public NametagInfo(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;

        this.packet = new ScoreboardTeamPacketMod(name, prefix, suffix, Collections.emptyList(), 0);
    }

}

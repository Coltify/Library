package com.ddylan.xlib.scoreboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ScoreboardConfiguration {

    private TitleGetter titleGetter;
    private ScoreGetter scoreGetter;

}

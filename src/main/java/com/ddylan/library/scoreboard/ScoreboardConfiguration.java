package com.ddylan.library.scoreboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ScoreboardConfiguration {

    private TitleGetter titleGetter;
    private ScoreGetter scoreGetter;

}

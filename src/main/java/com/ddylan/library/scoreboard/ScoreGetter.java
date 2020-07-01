package com.ddylan.library.scoreboard;

import org.bukkit.entity.Player;

import java.util.LinkedList;

public interface ScoreGetter {
    void getScores(LinkedList<String> linkedList, Player player);
}

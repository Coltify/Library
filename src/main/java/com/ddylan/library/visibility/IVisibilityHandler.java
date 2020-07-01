package com.ddylan.library.visibility;

import org.bukkit.entity.Player;

public interface IVisibilityHandler {
    VisibilityAction getAction(Player paramPlayer1, Player paramPlayer2);
}

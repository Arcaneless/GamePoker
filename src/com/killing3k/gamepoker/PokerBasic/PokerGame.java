package com.killing3k.gamepoker.PokerBasic;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public interface PokerGame {

    boolean isJointGame(Player p);
    boolean isFull();
    boolean joinGame(Player p);
    void leftGame(Player p);
    void start();
    void end();
    List<Player> getPlayers();
    Inventory openGui(Player p);
    String getName();

}

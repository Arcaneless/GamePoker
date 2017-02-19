package com.killing3k.gamepoker;

import com.killing3k.gamepoker.PokerBasic.PokerGame;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GameHandler {

    private Map<Player, PokerGame> list = new HashMap<>();

    public PokerGame getGame(Player p) {
        return list.get(p);
    }

    public void addGame(Player p, PokerGame pg) {
        list.put(p, pg);
    }

    public void removeGame(Player p) {
        list.entrySet().removeIf(e -> e.getKey().equals(p));
    }
}

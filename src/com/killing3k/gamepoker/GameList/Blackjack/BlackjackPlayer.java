package com.killing3k.gamepoker.GameList.Blackjack;

import com.killing3k.gamepoker.PokerBasic.PlayerDeck;

public class BlackjackPlayer extends PlayerDeck {
    public int points;
    public int reward;

    public BlackjackPlayer(boolean isDealer) {
        super(isDealer);
        points = 0;
        reward = 0;
    }
    public int getState() {
        if (points<21) return 0;
        else if (points>21) return 1;
        else return 2;
    }
}

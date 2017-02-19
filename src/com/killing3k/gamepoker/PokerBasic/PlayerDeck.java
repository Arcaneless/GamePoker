package com.killing3k.gamepoker.PokerBasic;

import java.util.ArrayList;
import java.util.List;

public class PlayerDeck {

    private List<Card> cards = new ArrayList<>();
    private int bet;

    public PlayerDeck(boolean isDealer) {
        if (!isDealer) bet = 100;
    }

    public int getBet() {
        return bet;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getDeck() {
        return cards;
    }

}

package com.killing3k.gamepoker.PokerBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {

    private List<Card> cards = new ArrayList<>();

    public Deck() {
        newDeck();
        shuffle();
    }

    private void newDeck() {
        for (byte i=0; i<4; i++) {
            for (byte j=1; j<14; j++) {
                cards.add(new Card(Suit.fromIndex(i), j));
            }
        }
    }

    public void shuffle() {
        long seed = System.nanoTime();
        Collections.shuffle(cards, new Random(seed));
    }

    public List<Card> getDeck() {
        return cards;
    }

    public boolean isCardTaken(Card card) {
        return cards.contains(card);
    }

    public Card takeCard(Card card) {
        cards.removeIf(c -> c.equals(card));
        return card;
    }

    public Card takeCard() {
        Random rand = new Random(System.nanoTime());
        Card c = new Card(Suit.fromIndex(rand.nextInt(3)), rand.nextInt(13)+1);
        while (isCardTaken(c)) c = new Card(Suit.fromIndex(rand.nextInt(3)), rand.nextInt(13)+1);
        return takeCard(c);
    }

}

package com.killing3k.gamepoker.PokerBasic;

public enum Suit {

    DIAMOND(0),
    CLUB(1),
    HEART(3),
    SPADE(4);

    private int index;

    Suit(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static Suit fromIndex(int index) {
        return Suit.values()[index];
    }

}

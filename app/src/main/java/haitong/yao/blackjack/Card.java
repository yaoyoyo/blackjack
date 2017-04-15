package haitong.yao.blackjack;

/**
 * Created by haitong on 17/4/14.
 */
public class Card {

    enum Suit {HEART, SPADE, DIAMOND, CLUB}

    enum Rank {ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING}

    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Card card = (Card) obj;
        if (card.suit != suit || card.rank != rank) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = suit.ordinal();
        result = 31 * result + rank.ordinal();
        return result;
    }
}

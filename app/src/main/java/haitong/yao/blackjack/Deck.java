package haitong.yao.blackjack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by haitong on 17/4/14.
 */
public class Deck {

    Collection<Card.Suit> suits = Arrays.asList(Card.Suit.values());
    Collection<Card.Rank> ranks = Arrays.asList(Card.Rank.values());

    private List<Card> deck = new ArrayList<>();

    public Deck() {
        init();
    }

    private void init() {
        for (Iterator<Card.Suit> i = suits.iterator(); i.hasNext(); /*do nothing*/) {
            Card.Suit suit = i.next();
            for (Iterator<Card.Rank> j = ranks.iterator(); j.hasNext(); /*do nothing*/) {
                deck.add(new Card(suit, j.next()));
            }
        }
    }

    public List<Card> getDeck() {
        return deck;
    }

}

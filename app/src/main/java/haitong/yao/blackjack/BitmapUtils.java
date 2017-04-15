package haitong.yao.blackjack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by haitong on 17/4/14.
 */
public class BitmapUtils {

    public static Bitmap cards;

    public static Bitmap getCardImage(Context context, Card card) {
        if (null == card)
            return null;
        if (null == cards)
            cards = BitmapFactory.decodeResource(context.getResources(), R.drawable.cards);

        int rows = Card.Suit.values().length;
        int cols = Card.Rank.values().length;
        int width = cards.getWidth() / cols;
        int height = cards.getHeight() / rows;
        int x = width * card.getRank().ordinal();
        int y = height * card.getSuit().ordinal();
        return Bitmap.createBitmap(cards, x, y, width, height);
    }

}

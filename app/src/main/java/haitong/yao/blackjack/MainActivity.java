package haitong.yao.blackjack;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private static final int MSG_PLAYER_DEAL = 0x00;
    private static final int MSG_PLAYER_HIT = 0x01;
    private static final int MSG_PLAYER_STAND = 0x02;
    private static final int MSG_PLAYER_FOLD = 0x03;
    private static final int MSG_BANKER_DEAL = 0x04;
    private static final int MSG_BANKER_DEAL_HIDE = 0x05;
    private static final int MSG_BANKER_FRONT_HIDE = 0x06;
    private static final int MSG_RESET = 0x07;

    private static final int TEN = 10;
    private static final int BLACK_JACK = 21;

    private static final int CARD_WIDTH = 225;
    private static final int CARD_HEIGHT = 315;
    private static final int CARD_MARGIN = 10;
    private static final int CARD_MARGIN_TOP = 20;
    private static final int CARD_MARGIN_BOTTOM = 500;

    private int screenWidth;
    private int screenHeight;

    private Random random;

    private ViewGroup parent;
    private View hit;
    private View stand;
    private View fold;

    private List<Card> cards;
    private List<CardImage> playerCards;
    private List<CardImage> bankerCards;

    private Handler msgHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_PLAYER_DEAL:
                    dealPlayer();
                    break;
                case MSG_BANKER_DEAL:
                    dealBanker();
                    break;
                case MSG_BANKER_DEAL_HIDE:
                    dealBankerHide();
                    break;
                case MSG_BANKER_FRONT_HIDE:
                    frontBankerHide();
                    break;
                case MSG_PLAYER_HIT:
                    hit();
                    break;
                case MSG_PLAYER_STAND:
                    stand();
                    break;
                case MSG_PLAYER_FOLD:
                    fold();
                    break;
                case MSG_RESET:
                    reset();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ScreenUtils.init(getApplicationContext());
        setContentView(R.layout.activity_main);

        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = ScreenUtils.getScreenHeight(this);

        random = new Random();
        cards = new Deck().getDeck();
        playerCards = new ArrayList<>();
        bankerCards = new ArrayList<>();

        parent = (ViewGroup) findViewById(R.id.activity_main);

        hit = findViewById(R.id.hit);
        hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hit();
            }
        });
        stand = findViewById(R.id.stand);
        stand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stand();
            }
        });
        fold = findViewById(R.id.fold);
        fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fold();
            }
        });

        deal();
    }

    private void deal() {
        disableButtons();
        msgHandle.sendMessageDelayed(Message.obtain(msgHandle, MSG_PLAYER_DEAL), 500);
        msgHandle.sendMessageDelayed(Message.obtain(msgHandle, MSG_BANKER_DEAL), 2000);
        msgHandle.sendMessageDelayed(Message.obtain(msgHandle, MSG_PLAYER_DEAL), 3500);
        msgHandle.sendMessageDelayed(Message.obtain(msgHandle, MSG_BANKER_DEAL_HIDE), 5000);
    }

    private void dealPlayer() {
        disableButtons();
        CardImage player = new CardImage(
                cards.remove(random.nextInt(cards.size())),
                createCardImage());
        player.setCallback(new CardImage.IAnimationCallback() {
            @Override
            public void onFrontEnd() {
                updatePlayerCardsLocation();
            }

            @Override
            public void onTranslationEnd() {
                if (playerCards.size() >= 2) {
                    enableButtons();
                }
                if (playerCards.size() > 2) {
                    check();
                }
            }
        });
        player.front();
        playerCards.add(player);
    }

    private void dealBanker() {
        disableButtons();
        CardImage banker = new CardImage(
                cards.remove(random.nextInt(cards.size())),
                createCardImage());
        banker.setCallback(new CardImage.IAnimationCallback() {
            @Override
            public void onFrontEnd() {
                updateBankerCardsLocation();
            }

            @Override
            public void onTranslationEnd() {
                if (bankerCards.size() > 2) {
                    disableButtons();
                    if (!check()) {
                        if (count(bankerCards) > count(playerCards)) {
                            lose();
                        } else {
                            msgHandle.sendMessageDelayed(Message.obtain(msgHandle, MSG_BANKER_DEAL), 2000);
                        }
                    }
                }
            }
        });
        banker.front();
        bankerCards.add(banker);
    }

    private void dealBankerHide() {
        disableButtons();
        CardImage banker = new CardImage(
                cards.remove(random.nextInt(cards.size())),
                createCardImage());
        banker.setCallback(new CardImage.IAnimationCallback() {
            @Override
            public void onFrontEnd() {
            }

            @Override
            public void onTranslationEnd() {
                check();
            }
        });
        bankerCards.add(banker);
        updateBankerCardsLocation();
    }

    private void frontBankerHide() {
        disableButtons();
        CardImage banker = bankerCards.get(1);
        banker.front();
    }

    private ImageView createCardImage() {
        ImageView card = new ImageView(this);
        card.setImageResource(R.drawable.card_back);
        card.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(CARD_WIDTH, CARD_HEIGHT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.rightMargin = 40;
        parent.addView(card, layoutParams);
        return card;
    }

    private int[] getPlayerCardLocation(int num) {
        int centerX = screenWidth / 2;
        int size = playerCards.size();
        int x = centerX
                - ((size - 1) * CARD_MARGIN + size * CARD_WIDTH) / 2
                + num * (CARD_MARGIN + CARD_WIDTH);
        int y = screenHeight - CARD_MARGIN_BOTTOM;
        return new int[]{x, y};
    }

    private void updatePlayerCardsLocation() {
        int size = playerCards.size();
        for (int i = 0; i < size; i++) {
            int[] location = getPlayerCardLocation(i);
            CardImage cardImage = playerCards.get(i);
            cardImage.translate(location[0], location[1]);
        }
    }

    private int[] getBankerCardLocation(int num) {
        int centerX = screenWidth / 2;
        int size = bankerCards.size();
        int x = centerX
                - ((size - 1) * CARD_MARGIN + size * CARD_WIDTH) / 2
                + num * (CARD_MARGIN + CARD_WIDTH);
        int y = CARD_MARGIN_TOP;
        return new int[]{x, y};
    }

    private void updateBankerCardsLocation() {
        int size = bankerCards.size();
        for (int i = 0; i < size; i++) {
            int[] location = getBankerCardLocation(i);
            CardImage cardImage = bankerCards.get(i);
            cardImage.translate(location[0], location[1]);
        }
    }

    private boolean check() {
        boolean isBankerBlackJack = isBlackJack(bankerCards);
        boolean isPlayerBlackJack = isBlackJack(playerCards);
        if (isBusted(playerCards) || (isBankerBlackJack && !isPlayerBlackJack)) {
            lose();
            return true;
        } else if (isBusted(bankerCards) || (!isBankerBlackJack && isPlayerBlackJack)) {
            win();
            return true;
        } else if (isBankerBlackJack && isPlayerBlackJack) {
            draw();
            return true;
        }
        return false;
    }

    private boolean isBlackJack(List<CardImage> handCards) {
        return count(handCards) == BLACK_JACK;
    }

    private boolean isBusted(List<CardImage> handCards) {
        return count(handCards) > BLACK_JACK;
    }

    private int count(List<CardImage> handCards) {
        int total = 0;
        if (null == handCards) {
            return total;
        }
        for (CardImage cardImage : handCards) {
            if (cardImage == null || cardImage.getCard() == null) {
                continue;
            }
            Card card = cardImage.getCard();
            if (card.getRank() == Card.Rank.ACE) {
                if (total + 11 > BLACK_JACK) {
                    total += 1;
                } else {
                    total += 11;
                }
            } else if (card.getRank().ordinal() >= 9) {
                total += TEN;
            } else {
                total += (card.getRank().ordinal() + 1);
            }
        }
        return total;
    }

    private void reset() {
        cards = new Deck().getDeck();
        for (CardImage cardImage : playerCards) {
            if (cardImage != null && cardImage.getImage() != null) {
                parent.removeView(cardImage.getImage());
            }
        }
        for (CardImage cardImage : bankerCards) {
            if (cardImage != null && cardImage.getImage() != null) {
                parent.removeView(cardImage.getImage());
            }
        }
        playerCards.clear();
        bankerCards.clear();
        for (int i = 0; i < 8; i++) {
            msgHandle.removeMessages(i);
        }
        deal();
    }

    private void enableButtons() {
        hit.setClickable(true);
        stand.setClickable(true);
        fold.setClickable(true);
    }

    private void disableButtons() {
        hit.setClickable(false);
        stand.setClickable(false);
        fold.setClickable(false);
    }

    private void hit() {
        disableButtons();
        msgHandle.sendMessage(Message.obtain(msgHandle, MSG_PLAYER_DEAL));
    }

    private void stand() {
        disableButtons();
        if (count(bankerCards) > count(playerCards)) {
            lose();
            return;
        }
        msgHandle.sendMessage(Message.obtain(msgHandle, MSG_BANKER_DEAL));
    }

    private void fold() {
        disableButtons();
        frontBankerHide();
        lose();
    }

    private void win() {
        disableButtons();
        Toast.makeText(MainActivity.this, "you win!", Toast.LENGTH_SHORT).show();
        msgHandle.sendMessage(Message.obtain(msgHandle, MSG_BANKER_FRONT_HIDE));
        msgHandle.sendMessageDelayed(Message.obtain(msgHandle, MSG_RESET), 4000);
    }

    private void lose() {
        disableButtons();
        Toast.makeText(MainActivity.this, "you lose!", Toast.LENGTH_SHORT).show();
        msgHandle.sendMessage(Message.obtain(msgHandle, MSG_BANKER_FRONT_HIDE));
        msgHandle.sendMessageDelayed(Message.obtain(msgHandle, MSG_RESET), 4000);
    }

    private void draw() {
        disableButtons();
        Toast.makeText(MainActivity.this, "game draws!", Toast.LENGTH_SHORT).show();
        msgHandle.sendMessage(Message.obtain(msgHandle, MSG_BANKER_FRONT_HIDE));
        msgHandle.sendMessageDelayed(Message.obtain(msgHandle, MSG_RESET), 4000);
    }

}

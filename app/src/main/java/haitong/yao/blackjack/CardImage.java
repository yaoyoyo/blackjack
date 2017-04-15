package haitong.yao.blackjack;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Created by haitong on 17/4/15.
 */
public class CardImage {

    private static final long DURATION = 500L;

    private static final float DEPTH_Z = 0f;

    private int translationX;
    private int translationY;

    private Card card;
    private ImageView image;

    private AnimatorSet animatorSet;
    private IAnimationCallback callback;

    public CardImage(Card card, ImageView image) {
        this.card = card;
        this.image = image;
        translationX = 0;
        translationY = 0;
    }

    public void setCallback(IAnimationCallback callback) {
        this.callback = callback;
    }

    public void translate(final int x, final int y) {
        if (image == null) {
            return;
        }

        if (image.getLeft() == 0 || image.getTop() == 0) {
            final ViewTreeObserver observer = image.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    translate(x, y);
                    observer.removeOnGlobalLayoutListener(this);
                }
            });
            return;
        }

        if (null != animatorSet) {
            animatorSet.end();
        }
        image.clearAnimation();

        ObjectAnimator animX = ObjectAnimator.ofFloat(image, "translationX", x - image.getLeft());
        ObjectAnimator animY = ObjectAnimator.ofFloat(image, "translationY", y - image.getTop());
        translationX = x - image.getLeft();
        translationY = y - image.getTop();
        Log.e("haitong", "ci=" + this + "\ntx=" + translationX + ", ty=" + translationY);
        Log.e("haitong", "ci=" + this + "left=" + image.getLeft() + ", top = " + image.getTop());

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animX, animY);
        animatorSet.setDuration(DURATION);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (null != callback)
                    callback.onTranslationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void front() {
        if (image == null) {
            return;
        }

        if (null != animatorSet) {
            animatorSet.end();
        }
        image.clearAnimation();

        final int x = image.getLayoutParams().width / 2 + translationX;
        final int y = image.getLayoutParams().height / 2 + translationY;
        Log.e("haitong", "ci=" + this + "\nx=" + x + ", y=" + y);
        Animation firstHalf = rotate(0, 90, x, y);
        firstHalf.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setImageBitmap(BitmapUtils.getCardImage(image.getContext(), card));
                Animation secondHalf = rotate(270, 360, x, y);
                secondHalf.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (null != callback)
                            callback.onFrontEnd();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private Animation rotate(float startDegree, float endDegree, float centerX, float centerY) {
        Rotate3dAnimation anim = new Rotate3dAnimation(
                startDegree, endDegree, centerX, centerY, DEPTH_Z, false);
        anim.setDuration(DURATION);
        anim.setFillAfter(true);
        image.startAnimation(anim);
        return anim;
    }

    public Card getCard() {
        return card;
    }

    public ImageView getImage() {
        return image;
    }

    public interface IAnimationCallback {
        void onFrontEnd();

        void onTranslationEnd();
    }

}

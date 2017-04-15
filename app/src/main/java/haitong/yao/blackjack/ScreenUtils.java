package haitong.yao.blackjack;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by haitong on 16/12/7.
 */
public class ScreenUtils {

    private static int sScreenWidth;
    private static int sScreenHeight;

    public static void init(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        sScreenWidth = dm.widthPixels;
        sScreenHeight = dm.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        if (sScreenWidth <= 0) {
            init(context);
        }
        return sScreenWidth;
    }

    public static int getScreenHeight(Context context) {
        if (sScreenHeight <= 0) {
            init(context);
        }
        return sScreenHeight;
    }

}

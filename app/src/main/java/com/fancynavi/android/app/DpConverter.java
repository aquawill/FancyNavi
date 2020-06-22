package com.fancynavi.android.app;

import android.content.Context;
import android.util.DisplayMetrics;

class DpConverter {

    static float convertDpToPixel(float dp, Context context) {
        return dp * getDensity(context);
    }

    static float convertPixelToDp(float px, Context context) {
        return px / getDensity(context);
    }

    private static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
}

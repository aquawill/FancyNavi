package com.fancynavi.android.app;

import android.content.Context;
import android.util.DisplayMetrics;

class DpConverter {

    static float convertDpToPixel(float dp, Context context) {
        float px = dp * getDensity(context);
        return px;
    }

    static float convertPixelToDp(float px, Context context) {
        float dp = px / getDensity(context);
        return dp;
    }

    private static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
}

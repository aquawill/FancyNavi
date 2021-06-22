package com.fancynavi.android.app;

import android.graphics.PointF;
import android.util.Log;

import com.here.android.mpa.mapping.Map;

import static com.fancynavi.android.app.DataHolder.TAG;

class ShiftMapCenter {
    ShiftMapCenter(Map map, float widthOffset, float heightOffset) {
        map.setTransformCenter(new PointF((map.getWidth() * widthOffset), (map.getHeight() * heightOffset)));
        Log.d(TAG, "map.getWidth(): " + map.getWidth());
        Log.d(TAG, "map.getHeight(): " + map.getHeight());
        Log.d(TAG, "map.setTransformCenter(new PointF((" + map.getWidth() * widthOffset + "), (" + map.getHeight() * heightOffset + ")));");
    }
}

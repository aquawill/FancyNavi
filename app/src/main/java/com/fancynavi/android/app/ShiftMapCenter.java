package com.fancynavi.android.app;

import android.graphics.PointF;
import android.util.Log;

import com.here.android.mpa.mapping.Map;

class ShiftMapCenter {
    ShiftMapCenter(Map map, float widthOffset, float heightOffset) {
        map.setTransformCenter(new PointF((map.getWidth() * widthOffset), (map.getHeight() * heightOffset)));
        Log.d("SDK", "map.getWidth(): " + map.getWidth());
        Log.d("SDK", "map.getHeight(): " + map.getHeight());
        Log.d("SDK", "map.setTransformCenter(new PointF((" + map.getWidth() * widthOffset + "), (" + map.getHeight() * heightOffset + ")));");
    }
}

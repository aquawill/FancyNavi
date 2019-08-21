package com.fancynavi.android.app;

import android.graphics.PointF;

import com.here.android.mpa.mapping.Map;

class ShiftMapCenter {
    ShiftMapCenter(Map map, float widthOffset, float heightOffset) {
        map.setTransformCenter(new PointF((map.getWidth() * widthOffset), (map.getHeight() * heightOffset)));
    }
}

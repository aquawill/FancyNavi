package com.fancynavi.android.app;

import android.graphics.PointF;
import android.util.Log;

import com.here.android.mpa.mapping.Map;

import static com.fancynavi.android.app.DataHolder.TAG;

class ShiftMapCenter {

    void setTransformCenter(Map map, float widthOffset, float heightOffset) {
        map.setTransformCenter(new PointF((map.getWidth() * widthOffset), (map.getHeight() * heightOffset)));
        Log.d(TAG, "map.getWidth(): " + map.getWidth());
        Log.d(TAG, "map.getHeight(): " + map.getHeight());
        Log.d(TAG, "map.setTransformCenter(new PointF((" + map.getWidth() * widthOffset + "), (" + map.getHeight() * heightOffset + ")));");
    }

    Double[] getTransformCenterOffset(Map map) {
        PointF pointF = map.getTransformCenter();
        double xOffset = pointF.x / map.getWidth();
        double yOffset = pointF.y / map.getHeight();
        Double[] offset = new Double[2];
        offset[0] = xOffset;
        offset[1] = yOffset;
        return offset;
    }
}

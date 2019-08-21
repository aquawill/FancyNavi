package com.fancynavi.android.app;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;

class ResetMapCenter {
    ResetMapCenter(Map map, PositioningManager positioningManager) {
        map.setCenter(positioningManager.getPosition().getCoordinate(), Map.Animation.NONE);
    }

    ResetMapCenter(Map map, GeoBoundingBox geoBoundingBox) {
        map.zoomTo(geoBoundingBox, Map.Animation.NONE, Map.MOVE_PRESERVE_ORIENTATION);
    }

}

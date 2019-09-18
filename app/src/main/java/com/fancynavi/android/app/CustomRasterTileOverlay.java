package com.fancynavi.android.app;

import com.here.android.mpa.mapping.MapOverlayType;
import com.here.android.mpa.mapping.UrlMapRasterTileSourceBase;

public class CustomRasterTileOverlay extends UrlMapRasterTileSourceBase {
    private final static String TILE_URL = "https://raw.githubusercontent.com/aquawill/tpe_no_parking_lines/master/tiles/%s/%s/%s.png";

    CustomRasterTileOverlay() {
        setCachingEnabled(true);
        hideAtZoomRange(0, 15);
        setOverlayType(MapOverlayType.FOREGROUND_OVERLAY);
        setTransparency(Transparency.ON);
        setTileSize(256);
    }

    @Override
    public String getUrl(int x, int y, int z) {
        String url = null;
        try {
            url = String.format(TILE_URL, z, x, y);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return url;
    }
}

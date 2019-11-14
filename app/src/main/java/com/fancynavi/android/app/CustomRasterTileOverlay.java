package com.fancynavi.android.app;

import com.here.android.mpa.mapping.MapOverlayType;
import com.here.android.mpa.mapping.UrlMapRasterTileSourceBase;

public class CustomRasterTileOverlay extends UrlMapRasterTileSourceBase {

    private String tileUrl;

    CustomRasterTileOverlay() {
        setCachingEnabled(true);
        hideAtZoomRange(0, 8);
        setOverlayType(MapOverlayType.FOREGROUND_OVERLAY);
        setTransparency(Transparency.ON);
        setTileSize(256);
    }

    String getTileUrl() {
        return tileUrl;
    }

    void setTileUrl(String tileUrl) {
        this.tileUrl = tileUrl;
    }

    @Override
    public String getUrl(int x, int y, int z) {
        String url = null;
        try {
            url = String.format(tileUrl, z, x, y);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return url;
    }
}

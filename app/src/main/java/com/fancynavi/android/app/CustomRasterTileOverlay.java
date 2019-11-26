package com.fancynavi.android.app;

import com.here.android.mpa.mapping.MapOverlayType;
import com.here.android.mpa.mapping.UrlMapRasterTileSourceBase;

public class CustomRasterTileOverlay extends UrlMapRasterTileSourceBase {

    private String[] subDomains;
    private String tileUrl;

    CustomRasterTileOverlay() {
        setCachingEnabled(true);
        hideAtZoomRange(0, 8);
        setOverlayType(MapOverlayType.FOREGROUND_OVERLAY);
        setTransparency(Transparency.ON);
        setTileSize(256);
    }

    public String[] getSubDomains() {
        return subDomains;
    }

    public void setSubDomains(String[] subDomains) {
        this.subDomains = subDomains;
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
        if (subDomains != null) {
            String s = subDomains[(x + y) % subDomains.length];
            try {
                url = String.format(tileUrl, s, z, x, y);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                url = String.format(tileUrl, z, x, y);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return url;
    }
}

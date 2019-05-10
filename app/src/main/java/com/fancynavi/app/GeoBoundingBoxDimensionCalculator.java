package com.fancynavi.app;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;

public class GeoBoundingBoxDimensionCalculator {

    private GeoBoundingBox geoBoundingBox;
    private double bBoxHeightMeters;
    private double bBoxWidthMeters;

    GeoBoundingBoxDimensionCalculator(GeoBoundingBox geoBoundingBox) {
        this.geoBoundingBox = geoBoundingBox;
        getGeoBoundingBoxDimension(geoBoundingBox);
    }

    double getBBoxHeight() {
        return bBoxHeightMeters;
    }

    double getBBoxWidth() {
        return bBoxWidthMeters;
    }

    private void getGeoBoundingBoxDimension(GeoBoundingBox geoBoundingBox) {
        GeoCoordinate bBoxTopLeft = geoBoundingBox.getTopLeft();
        GeoCoordinate bBoxBottomRight = geoBoundingBox.getBottomRight();
        GeoCoordinate bBoxTopRight = new GeoCoordinate(bBoxTopLeft.getLatitude(), bBoxBottomRight.getLongitude());
        GeoCoordinate bBoxBottomLeft = new GeoCoordinate(bBoxBottomRight.getLatitude(), bBoxTopLeft.getLongitude());
        bBoxHeightMeters = bBoxTopLeft.distanceTo(bBoxBottomLeft);
        bBoxWidthMeters = bBoxBottomLeft.distanceTo(bBoxBottomRight);
    }


}

package com.fancynavi.android.app;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;

class GeoBoundingBoxDimensionCalculator {

    private double heightInMeters;
    private double widthInMeters;

    GeoBoundingBoxDimensionCalculator(GeoBoundingBox geoBoundingBox) {
        GeoCoordinate bBoxTopLeft = geoBoundingBox.getTopLeft();
        GeoCoordinate bBoxBottomRight = geoBoundingBox.getBottomRight();
        GeoCoordinate bBoxTopRight = new GeoCoordinate(bBoxTopLeft.getLatitude(), bBoxBottomRight.getLongitude());
        GeoCoordinate bBoxBottomLeft = new GeoCoordinate(bBoxBottomRight.getLatitude(), bBoxTopLeft.getLongitude());
        heightInMeters = bBoxTopLeft.distanceTo(bBoxBottomLeft);
        widthInMeters = bBoxBottomLeft.distanceTo(bBoxBottomRight);
    }

    double getHeightInMeters() {
        return heightInMeters;
    }

    double getWidthInMeters() {
        return widthInMeters;
    }


}

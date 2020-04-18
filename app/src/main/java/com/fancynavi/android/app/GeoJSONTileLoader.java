package com.fancynavi.android.app;

import android.app.Activity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.LineString;
import com.cocoahero.android.geojson.MultiLineString;
import com.cocoahero.android.geojson.MultiPoint;
import com.cocoahero.android.geojson.MultiPolygon;
import com.cocoahero.android.geojson.Point;
import com.cocoahero.android.geojson.Polygon;
import com.cocoahero.android.geojson.Position;
import com.cocoahero.android.geojson.Ring;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPolygon;
import com.here.android.mpa.common.GeoPolyline;
import com.here.android.mpa.mapping.MapContainer;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapPolygon;
import com.here.android.mpa.mapping.MapPolyline;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.fancynavi.android.app.DataHolder.TAG;

class GeoJSONTileLoader {
    private MapContainer resultMapContainer;
    private String baseUrl;
    private RequestQueue requestQueue;
    private List<PointResult> pointResultList;
    private List<LingStringResult> lineStringResultList;
    private List<PolygonResult> polygonResultList;
    private OnTileRequestCompletedListener onTileRequestCompletedListener;

    GeoJSONTileLoader(Activity activity, String baseUrl) {
        this.baseUrl = baseUrl;
        this.requestQueue = Volley.newRequestQueue(activity);
        this.resultMapContainer = new MapContainer();
    }

    void setOnTileRequestCompletedListener(OnTileRequestCompletedListener onTileRequestCompletedListener) {
        this.onTileRequestCompletedListener = onTileRequestCompletedListener;
    }

    MapContainer getResultMapContainer() {
        return resultMapContainer;
    }

    private GeoCoordinate processPoint(double[] pointPosition) {
        return new GeoCoordinate(pointPosition[1], pointPosition[0], pointPosition[2]);
    }

    private GeoPolyline processLingString(LineString lineString) {
        GeoPolyline multiLineStringGeoPolyline = new GeoPolyline();
        List<Position> multiLineStringShapePointList = lineString.getPositions();
        for (Position shapePointPosition : multiLineStringShapePointList) {
            double[] pointPosition = shapePointPosition.toArray();
            multiLineStringGeoPolyline.add(new GeoCoordinate(pointPosition[1], pointPosition[0], pointPosition[2]));
        }
        return multiLineStringGeoPolyline;
    }

    private GeoPolygon processPolygon(Polygon polygon) {
        GeoPolygon geoPolygon = new GeoPolygon();
        for (Ring ring : polygon.getRings()) {
            List<Position> shapePointList = ring.getPositions();
            for (Position shapePointPosition : shapePointList) {
                double[] pointPosition = shapePointPosition.toArray();
                geoPolygon.add(new GeoCoordinate(pointPosition[1], pointPosition[0], pointPosition[2]));
            }
        }
        return geoPolygon;
    }

    List<PointResult> getPointResultList() {
        return pointResultList;
    }

    List<LingStringResult> getLineStringResultList() {
        return lineStringResultList;
    }

    List<PolygonResult> getPolygonResultList() {
        return polygonResultList;
    }

    private void getTiles(int xBegins, int xEnds, int yBegins, int yEnds, int z) {
        for (int xIndex = xBegins; xIndex <= xEnds; xIndex++) {
            for (int yIndex = yBegins; yIndex <= yEnds; yIndex++) {
                String url = String.format(baseUrl, z, xIndex, yIndex);
                StringRequest geoJsonTileStringRequest = new StringRequest(url, s -> {
//                    Log.d(TAG, url);
                    try {
                        GeoJSONObject geoJSONObject = GeoJSON.parse(new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                        List<Feature> featureList = new FeatureCollection(geoJSONObject.toJSON()).getFeatures();
                        for (Feature feature : featureList) {
                            String featureId = feature.getIdentifier();
                            JSONObject featureProperties = feature.getProperties();
                            switch (feature.getGeometry().getType()) {
                                case "Point":
                                    GeoCoordinate geoCoordinate = processPoint(new Point(feature.getGeometry().toJSON()).getPosition().toArray());
                                    MapMarker mapMarker = new MapMarker(geoCoordinate);
                                    resultMapContainer.addMapObject(mapMarker);
                                    PointResult pointResult = new PointResult(featureId, url, geoCoordinate, featureProperties);
                                    pointResultList.add(pointResult);
                                    break;
                                case "Polygon":
                                    GeoPolygon geoPolygon = processPolygon(new Polygon(feature.getGeometry().toJSON()));
                                    resultMapContainer.addMapObject(new MapPolygon(geoPolygon));
                                    PolygonResult polygonResult = new PolygonResult(featureId, url, geoPolygon, featureProperties);
                                    polygonResultList.add(polygonResult);
                                    break;
                                case "LineString":
                                    GeoPolyline geoPolyline = processLingString(new LineString(feature.getGeometry().toJSON()));
                                    resultMapContainer.addMapObject(new MapPolyline(geoPolyline));
                                    LingStringResult lineStringResult = new LingStringResult(featureId, url, geoPolyline, featureProperties);
                                    lineStringResultList.add(lineStringResult);
                                    break;
                                case "MultiPoint":
                                    List<Position> positionList = new MultiPoint(feature.getGeometry().toJSON()).getPositions();
                                    for (Position position : positionList) {
                                        GeoCoordinate geoCoordinateMultiPoint = processPoint(position.toArray());
                                        resultMapContainer.addMapObject(new MapMarker(geoCoordinateMultiPoint));
                                        PointResult pointResultMultiPoint = new PointResult(featureId, url, geoCoordinateMultiPoint, featureProperties);
                                        pointResultList.add(pointResultMultiPoint);
                                    }
                                    break;
                                case "MultiLineString":
                                    List<LineString> lineStringList = new MultiLineString(feature.getGeometry().toJSON()).getLineStrings();
                                    for (LineString lineString : lineStringList) {
                                        GeoPolyline geoPolylineMultiLineString = processLingString(lineString);
                                        resultMapContainer.addMapObject(new MapPolyline(geoPolylineMultiLineString));
                                        LingStringResult lineStringResultMultiLineString = new LingStringResult(featureId, url, geoPolylineMultiLineString, featureProperties);
                                        lineStringResultList.add(lineStringResultMultiLineString);
                                    }
                                    break;
                                case "MultiPolygon":
                                    List<Polygon> polygonList = new MultiPolygon(feature.toJSON()).getPolygons();
                                    for (Polygon polygon : polygonList) {
                                        GeoPolygon geoPolygonMultiPolygon = processPolygon(polygon);
                                        resultMapContainer.addMapObject(new MapPolygon(geoPolygonMultiPolygon));
                                        PolygonResult polygonResultMultiPolygon = new PolygonResult(featureId, url, geoPolygonMultiPolygon, featureProperties);
                                        polygonResultList.add(polygonResultMultiPolygon);
                                    }
                                    break;
                            }
                        }
                        onTileRequestCompletedListener.onCompleted();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, volleyError -> {
                    Log.d(TAG, "volleyError: " + volleyError.getLocalizedMessage());
                });
                geoJsonTileStringRequest.setShouldRetryServerErrors(true);
                requestQueue.add(geoJsonTileStringRequest);
            }
        }
        requestQueue.start();
    }

    void getTilesWithMapBoundingBox(GeoBoundingBox geoBoundingBox, double mapZoom) {
        resultMapContainer.removeAllMapObjects();
        pointResultList = new ArrayList<>();
        lineStringResultList = new ArrayList<>();
        polygonResultList = new ArrayList<>();
        double mapSouthLatitude = geoBoundingBox.getBottomRight().getLatitude();
        double mapNorthLatitude = geoBoundingBox.getTopLeft().getLatitude();
        double mapEastLongitude = geoBoundingBox.getBottomRight().getLongitude();
        double mapWestLongitude = geoBoundingBox.getTopLeft().getLongitude();
        int[] northWestTileNumber = getTileNumber(mapNorthLatitude, mapWestLongitude, (int) mapZoom);
        int[] southEastTileNumber = getTileNumber(mapSouthLatitude, mapEastLongitude, (int) mapZoom);
        int tileXBegins = northWestTileNumber[1];
        int tileYBegins = northWestTileNumber[2];
        int tileXEnds = southEastTileNumber[1];
        int tileYEnds = southEastTileNumber[2];
        int tileZ = southEastTileNumber[0];
        getTiles(tileXBegins, tileXEnds, tileYBegins, tileYEnds, tileZ);
    }

    private int[] getTileNumber(final double lat, final double lon, final int z) {
        double latRad = lat * Math.PI / 180;
        double n = Math.pow(2, z);
        int xTile = (int) Math.floor(n * ((lon + 180) / 360));
        int yTile = (int) Math.floor(n * (1 - (Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI)) / 2);
        return new int[]{z, xTile, yTile};
    }

    interface OnTileRequestCompletedListener {
        void onCompleted();
    }

    static class PointResult {
        private String id;
        private String url;
        private GeoCoordinate geoCoordinate;
        private JSONObject properties;

        PointResult(String id, String url, GeoCoordinate geoCoordinate, JSONObject properties) {
            this.id = id;
            this.url = url;
            this.geoCoordinate = geoCoordinate;
            this.properties = properties;
        }

        String getId() {
            return id;
        }

        String getUrl() {
            return url;
        }

        GeoCoordinate getGeoCoordinate() {
            return geoCoordinate;
        }

        JSONObject getProperties() {
            return properties;
        }
    }

    static class LingStringResult {
        private String id;
        private String url;
        private GeoPolyline geoPolyline;
        private JSONObject properties;

        LingStringResult(String id, String url, GeoPolyline geoPolyline, JSONObject properties) {
            this.id = id;
            this.url = url;
            this.geoPolyline = geoPolyline;
            this.properties = properties;
        }

        String getId() {
            return id;
        }

        String getUrl() {
            return url;
        }

        GeoPolyline getGeoPolyline() {
            return geoPolyline;
        }

        JSONObject getProperties() {
            return properties;
        }
    }

    static class PolygonResult {
        private String id;
        private String url;
        private GeoPolygon geoPolygon;
        private JSONObject properties;

        PolygonResult(String id, String url, GeoPolygon geoPolygon, JSONObject properties) {
            this.id = id;
            this.url = url;
            this.geoPolygon = geoPolygon;
            this.properties = properties;
        }

        String getId() {
            return id;
        }

        String getUrl() {
            return url;
        }

        GeoPolygon getGeoPolygon() {
            return geoPolygon;
        }

        JSONObject getProperties() {
            return properties;
        }
    }
}

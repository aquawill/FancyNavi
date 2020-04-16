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

import java.util.ArrayList;
import java.util.List;

import static com.fancynavi.android.app.DataHolder.TAG;

class GeoJsonTileLoader {
    private MapContainer mapContainer;
    private String baseUrl;
    private List<String> downloadedTiles = new ArrayList<>();
    private RequestQueue requestQueue;

    GeoJsonTileLoader(Activity activity, String baseUrl) {
        this.baseUrl = baseUrl;
        this.requestQueue = Volley.newRequestQueue(activity);
        this.mapContainer = new MapContainer();
    }

    MapContainer getMapContainer() {
        return mapContainer;
    }

    private void processPoint(double[] pointPosition) {
        GeoCoordinate geoCoordinate = new GeoCoordinate(pointPosition[1], pointPosition[0], pointPosition[2]);
        MapMarker mapMarker = new MapMarker(geoCoordinate);
        mapContainer.addMapObject(mapMarker);
    }

    private void processLingString(LineString lineString) {
        GeoPolyline multiLineStringGeoPolyline = new GeoPolyline();
        List<Position> multiLineStringShapePointList = lineString.getPositions();
        for (Position shapePointPosition : multiLineStringShapePointList) {
            double[] pointPosition = shapePointPosition.toArray();
            multiLineStringGeoPolyline.add(new GeoCoordinate(pointPosition[1], pointPosition[0], pointPosition[2]));
        }
        MapPolyline multiLineStringMapPolyline = new MapPolyline(multiLineStringGeoPolyline);
        mapContainer.addMapObject(multiLineStringMapPolyline);
    }

    private void processPolygon(Polygon polygon) {
        for (Ring ring : polygon.getRings()) {
            GeoPolygon geoPolygon = new GeoPolygon();
            List<Position> shapePointList = ring.getPositions();
            for (Position shapePointPosition : shapePointList) {
                double[] pointPosition = shapePointPosition.toArray();
                geoPolygon.add(new GeoCoordinate(pointPosition[1], pointPosition[0], pointPosition[2]));
            }
            MapPolygon mapPolygon = new MapPolygon(geoPolygon);
            mapContainer.addMapObject(mapPolygon);
        }
    }

    private void queueTiles(int xBegins, int xEnds, int yBegins, int yEnds, int z) {
        for (int xIndex = xBegins; xIndex <= xEnds; xIndex++) {
            for (int yIndex = yBegins; yIndex <= yEnds; yIndex++) {
                String url = String.format(baseUrl, z, xIndex, yIndex);
                if (!downloadedTiles.contains(url)) {
                    downloadedTiles.add(url);
                    StringRequest geoJsonTileStringRequest = new StringRequest(url, s -> {
                        Log.d(TAG, url);
                        try {
                            GeoJSONObject geoJSONObject = GeoJSON.parse(s);
                            List<Feature> featureList = new FeatureCollection(geoJSONObject.toJSON()).getFeatures();
                            for (Feature feature : featureList) {
                                Log.d(TAG, feature.getGeometry().getType());
                                switch (feature.getGeometry().getType()) {
                                    case "Point":
                                        processPoint(new Point(feature.getGeometry().toJSON()).getPosition().toArray());
                                        break;
                                    case "Polygon":
                                        processPolygon(new Polygon(feature.getGeometry().toJSON()));
                                        break;
                                    case "LineString":
                                        processLingString(new LineString(feature.getGeometry().toJSON()));
                                        break;
                                    case "MultiPoint":
                                        List<Position> positionList = new MultiPoint(feature.getGeometry().toJSON()).getPositions();
                                        for (Position position : positionList) {
                                            processPoint(position.toArray());
                                        }
                                        break;
                                    case "MultiLineString":
                                        List<LineString> lineStringList = new MultiLineString(feature.getGeometry().toJSON()).getLineStrings();
                                        for (LineString lineString : lineStringList) {
                                            processLingString(lineString);
                                        }
                                        break;
                                    case "MultiPolygon":
                                        List<Polygon> polygonList = new MultiPolygon(feature.toJSON()).getPolygons();
                                        for (Polygon polygon : polygonList) {
                                            processPolygon(polygon);
                                        }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, volleyError -> {
                    });
                    requestQueue.add(geoJsonTileStringRequest);
                    requestQueue.start();
                }
            }
        }
    }

    void getTiles(GeoBoundingBox geoBoundingBox, double mapZoom) {
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
        queueTiles(tileXBegins, tileXEnds, tileYBegins, tileYEnds, tileZ);
    }

    private int[] getTileNumber(final double lat, final double lon, final int z) {
        double latRad = lat * Math.PI / 180;
        double n = Math.pow(2, z);
        int xTile = (int) Math.floor(n * ((lon + 180) / 360));
        int yTile = (int) Math.floor(n * (1 - (Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI)) / 2);
        return new int[]{z, xTile, yTile};
    }
}

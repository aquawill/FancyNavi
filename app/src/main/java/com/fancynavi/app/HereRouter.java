package com.fancynavi.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteWaypoint;

import java.util.ArrayList;

import static com.fancynavi.app.MapFragmentView.currentGeoPosition;
import static com.fancynavi.app.MapFragmentView.m_map;

class HereRouter {
    private Context context;
    private RoutePlan routePlan;
    private RouteOptions routeOptions;
    private ArrayList<GeoCoordinate> waypoints = new ArrayList<>();
    private ArrayList<MapMarker> inputWaypointIcons = new ArrayList<>();
    private ArrayList<MapMarker> outputWaypointIcons = new ArrayList<>();

    public HereRouter(RouteOptions routeOptions) {
        this.routeOptions = routeOptions;
    }

    public RoutePlan getRoutePlan() {
        return routePlan;
    }

    void setWaypoints(ArrayList<GeoCoordinate> waypoints) {
        this.waypoints = waypoints;
    }

    void setContext(Context context) {
        this.context = context;
    }

    void setRouteOptions(RouteOptions routeOptions) {
        this.routeOptions = routeOptions;
    }

    ArrayList<MapMarker> getOutputWaypointIcons() {
        return outputWaypointIcons;
    }

    void createRoute() {
        for (int i = 0; i < inputWaypointIcons.size(); i++) {
            MapMarker mapMarker = inputWaypointIcons.get(i);
            Log.d("Test", "i " + mapMarker.getCoordinate());
            waypoints.add(mapMarker.getCoordinate());
            m_map.removeMapObject(mapMarker);
        }

        inputWaypointIcons.clear();
        routePlan = new RoutePlan();

        if (waypoints.size() == 1) {
            waypoints.add(0, currentGeoPosition.getCoordinate());
        } else if (waypoints.isEmpty()) {
            Toast.makeText(context, "waypoints is empty.", Toast.LENGTH_SHORT).show();
        }
        VectorDrawableConverter vectorDrawableConverter = new VectorDrawableConverter();

        for (int i = 0; i < waypoints.size(); i++) {
            GeoCoordinate coord = waypoints.get(i);
//            MapLabeledMarker mapLabeledMarker = new MapLabeledMarker(coord);
//            mapLabeledMarker.setLabelText("eng", "Waypoint Index " + i);
//            m_map.addMapObject(mapLabeledMarker);
            RouteWaypoint waypoint = new RouteWaypoint(coord);
            MapMarker mapMarker = new MapMarker();
            Image icon = new Image();
            if (i != 0 && i != waypoints.size() - 1) {
                waypoint.setWaypointType(RouteWaypoint.Type.VIA_WAYPOINT);
                mapMarker.setCoordinate(waypoint.getOriginalPosition());
            } else {
                if (i == 0) {
                    icon.setBitmap(vectorDrawableConverter.getBitmapFromVectorDrawable(context, R.drawable.ic_orig));
                    mapMarker.setCoordinate(waypoint.getOriginalPosition()).setIcon(icon);
                } else if (i == waypoints.size() - 1) {
                    icon.setBitmap(vectorDrawableConverter.getBitmapFromVectorDrawable(context, R.drawable.ic_dest));
                    mapMarker.setCoordinate(waypoint.getOriginalPosition()).setIcon(icon);
                }
            }
            outputWaypointIcons.add(mapMarker);
            routePlan.addWaypoint(waypoint);
            routePlan.setRouteOptions(routeOptions);
        }
    }
}

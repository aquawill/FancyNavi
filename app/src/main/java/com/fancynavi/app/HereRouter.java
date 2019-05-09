package com.fancynavi.app;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
    private AppCompatActivity m_activity;
    private Context context;
    private RoutePlan routePlan;
    private RouteOptions routeOptions;
    private ArrayList<GeoCoordinate> waypoints = new ArrayList<>();
    private ArrayList<MapMarker> inputWaypointIcons = new ArrayList<>();
    private ArrayList<MapMarker> outputWaypointIcons = new ArrayList<>();

    public HereRouter(AppCompatActivity m_activity, RouteOptions routeOptions) {
        this.m_activity = m_activity;
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
            Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "waypoints is empty.", Snackbar.LENGTH_LONG).show();
        }
        //VectorDrawableConverter vectorDrawableConverter = new VectorDrawableConverter();

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
                    icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(context, R.drawable.ic_orig));
                    mapMarker.setCoordinate(waypoint.getOriginalPosition()).setIcon(icon);
                } else if (i == waypoints.size() - 1) {
                    icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(context, R.drawable.ic_dest));
                    mapMarker.setCoordinate(waypoint.getOriginalPosition()).setIcon(icon);
                }
            }
            outputWaypointIcons.add(mapMarker);
            routePlan.addWaypoint(waypoint);
            routePlan.setRouteOptions(routeOptions);
        }
    }
}

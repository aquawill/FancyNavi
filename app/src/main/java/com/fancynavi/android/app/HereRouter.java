package com.fancynavi.android.app;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fancynavi.app.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteWaypoint;

import java.util.ArrayList;

import static com.fancynavi.android.app.MapFragmentView.currentGeoPosition;
import static com.fancynavi.android.app.MapFragmentView.m_map;

class HereRouter {
    private AppCompatActivity m_activity;
    private Context context;
    private RoutePlan routePlan;
    private RouteOptions routeOptions;
    private ArrayList<GeoCoordinate> waypoints = new ArrayList<>();
    private ArrayList<MapMarker> inputWaypointIcons = new ArrayList<>();
    private ArrayList<MapMarker> outputWaypointIcons = new ArrayList<>();

    HereRouter(AppCompatActivity m_activity, RouteOptions routeOptions) {
        this.m_activity = m_activity;
        this.routeOptions = routeOptions;
    }

    RoutePlan getRoutePlan() {
        return routePlan;
    }

    void setRoutePlan(RoutePlan routePlan) {
        this.routePlan = routePlan;
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

    void createRouteForNavi() {
        for (int i = 0; i < inputWaypointIcons.size(); i++) {
            MapMarker mapMarker = inputWaypointIcons.get(i);
            Log.d("Test", "i " + mapMarker.getCoordinate());
            GeoCoordinate mapMarkerGeocoordinate = mapMarker.getCoordinate();
            mapMarkerGeocoordinate.setAltitude(0);
            waypoints.add(mapMarkerGeocoordinate);
            m_map.removeMapObject(mapMarker);
        }

        inputWaypointIcons.clear();
        routePlan = new RoutePlan();

        if (waypoints.size() == 1) {
            GeoCoordinate currentGeoPositionCoordinate = currentGeoPosition.getCoordinate();
            currentGeoPositionCoordinate.setAltitude(0);
            waypoints.add(0, currentGeoPositionCoordinate);
        } else if (waypoints.isEmpty()) {
            Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "waypoints is empty.", Snackbar.LENGTH_LONG).show();
        }
        //VectorDrawableConverter vectorDrawableConverter = new VectorDrawableConverter();

        for (int i = 0; i < waypoints.size(); i++) {
            GeoCoordinate coord = waypoints.get(i);
            RouteWaypoint waypoint = new RouteWaypoint(coord);
            MapMarker waypointMapMarker = new MapMarker();
            waypointMapMarker.setDraggable(true);
            waypointMapMarker.setTitle(String.valueOf(i));

            Image icon = new Image();
            if (i != 0 && i != waypoints.size() - 1) {
                waypoint.setWaypointType(RouteWaypoint.Type.VIA_WAYPOINT);
                waypointMapMarker.setCoordinate(waypoint.getOriginalPosition());
            } else {
                if (i == 0) {
                    icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(context, R.drawable.ic_orig));
                    waypointMapMarker.setCoordinate(waypoint.getOriginalPosition());
                    waypointMapMarker.setIcon(icon);
                } else if (i == waypoints.size() - 1) {
                    icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(context, R.drawable.ic_dest));
                    waypointMapMarker.setCoordinate(waypoint.getOriginalPosition());
                    waypointMapMarker.setIcon(icon);
                }
            }
            outputWaypointIcons.add(waypointMapMarker);
            routePlan.addWaypoint(waypoint);
            routePlan.setRouteOptions(routeOptions);
        }
    }
}

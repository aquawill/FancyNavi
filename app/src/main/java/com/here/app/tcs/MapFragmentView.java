/*
 * Copyright (c) 2011-2018 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.here.app.tcs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.PositioningManager.OnPositionChangedListener;
import com.here.android.mpa.common.RoadElement;
import com.here.android.mpa.guidance.LaneInformation;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;

//HERE SDK UI KIT components
import com.here.msdkui.guidance.GuidanceManeuverData;
import com.here.msdkui.guidance.GuidanceManeuverListener;
import com.here.msdkui.guidance.GuidanceManeuverPresenter;
import com.here.msdkui.guidance.GuidanceManeuverView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static java.util.Locale.TRADITIONAL_CHINESE;

/**
 * This class encapsulates the properties and functionality of the Map view.It also triggers a
 * turn-by-turn navigation from HERE Burnaby office to Langley BC.There is a sample voice skin
 * bundled within the SDK package to be used out-of-box, please refer to the Developer's guide for
 * the usage.
 */
class MapFragmentView {
    private MapFragment m_mapFragment;
    private Activity m_activity;
    private Button m_naviControlButton;
    private Map m_map;
    private NavigationManager m_navigationManager;
    private PositioningManager m_positioningManager;
    private GeoBoundingBox m_geoBoundingBox;
    private Route m_route;
    private boolean m_foregroundServiceStarted;
    //HERE SDK UI KIT components
    private GuidanceManeuverView guidanceManeuverView;
    private GuidanceManeuverPresenter guidanceManeuverPresenter;


    //HERE UI Kit, Guidance Maneuver View
    private GuidanceManeuverListener guidanceManeuverListener = new GuidanceManeuverListener() {
        @Override
        public void onDataChanged(@Nullable GuidanceManeuverData guidanceManeuverData) {
            guidanceManeuverView.setManeuverData(guidanceManeuverData);
        }

        @Override
        public void onDestinationReached() {
            guidanceManeuverView.highLightManeuver(Color.BLUE);
        }
    };


    MapFragmentView(Activity activity) {
        m_activity = activity;
        initMapFragment();
        initNaviControlButton();
    }

    ;

    private long simulationSpeedMs = 20; //defines the speed of navigation simulation

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = getMapFragment();

        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            if (m_mapFragment != null) {
                /* Initialize the MapFragment, results will be given via the called back. */
                m_mapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                        if (error == Error.NONE) {
                            m_map = m_mapFragment.getMap();
                            m_map.setMapDisplayLanguage(TRADITIONAL_CHINESE);
                            m_navigationManager = NavigationManager.getInstance();
                            m_positioningManager = PositioningManager.getInstance();

                        } else {
                            Toast.makeText(m_activity,
                                    "ERROR: Cannot initialize Map with error " + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

    }

    /*
    private void downloadVoice(final long skin_id) {
        Log.d("Test", "Downloading voice skin ID: " + skin_id);
        final VoiceCatalog voiceCatalog = VoiceCatalog.getInstance();
        voiceCatalog.downloadVoice(skin_id, new VoiceCatalog.OnDownloadDoneListener() {
            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
                if (error != VoiceCatalog.Error.NONE) {
                    Log.d("Test", "Failed downloading voice skin");
                } else {
                    Log.d("Test", "Voice skin downloaded and activated");
                    //NavigationManager.getInstance().setVoiceSkin(VoiceCatalog.getInstance().getLocalVoiceSkin(skin_id)); //Deprecated in SDK 3.7
                    VoiceGuidanceOptions voiceGuidanceOptions = m_navigationManager.getVoiceGuidanceOptions();
                    voiceGuidanceOptions.setVoiceSkin(voiceCatalog.getLocalVoiceSkin(skin_id));

                }
            }
        });
    }

    private void downloadCatalogAndSkin() {
        // First get the voice catalog from the backend that contains all available languages (so called voiceskins) for download
        VoiceCatalog.getInstance().downloadCatalog(new VoiceCatalog.OnDownloadDoneListener() {
            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
                if (error != VoiceCatalog.Error.NONE) {
                    Log.d("Test", "Failed to download catalog");
                } else {
                    Log.d("Test", "Catalog downloaded");
                    List<VoicePackage> packages = VoiceCatalog.getInstance().getCatalogList();
                    Log.d("Test", "# of available packages: " + packages.size());
                    for (VoicePackage lang : packages)
                        Log.d("Test", "\tLanguage name: " + lang.getLocalizedLanguage() + "\tGender: " + lang.getGender() + "\tis TTS: " + lang.isTts() + "\tID: " + lang.getId());
                    List<VoiceSkin> localInstalledSkins = VoiceCatalog.getInstance().getLocalVoiceSkins();
                    Log.d("Test", "# of local skins: " + localInstalledSkins.size());
                    for (VoiceSkin voice : localInstalledSkins) {
                        Log.d("Test", "ID: " + voice.getId() + " Language: " + voice.getLanguage());
                    }
                    downloadVoice(1101);
                }
            }
        });
    }
    */
    private NavigationManager.NewInstructionEventListener m_newInstructionEventListener = new NavigationManager.NewInstructionEventListener() {
        @Override
        public void onNewInstructionEvent() {

            Log.d("Test", "=======================================================================================");
            Log.d("Test", "Next Maneuver and Road information");
            Log.d("Test", "---------------------------------------------------------------------------------------");
            Log.d("Test getNextManeuver()", m_navigationManager.getNextManeuver().toString());
            Log.d("Test getCoordinate()", m_navigationManager.getNextManeuver().getCoordinate().toString());
            Log.d("Test Distance()", "Distance: " + m_navigationManager.getNextManeuverDistance() + "m");
            Log.d("Test getNextRoadName()", m_navigationManager.getNextManeuver().getNextRoadName());
            Log.d("Test RoadNumber()", m_navigationManager.getNextManeuver().getNextRoadNumber());
            Log.d("Test getTurn()", m_navigationManager.getNextManeuver().getTurn().toString());
            Log.d("Test getIcon()", m_navigationManager.getNextManeuver().getIcon().toString());
            Toast.makeText(m_activity, m_navigationManager.getNextManeuver().getTurn().toString() + " at " + m_navigationManager.getNextManeuver().getNextRoadName(), Toast.LENGTH_LONG).show();

            /*
            if(maneuver != null) {
                Log.d("Test", "---------------------------------------------------------------------------------------");
                Log.d("Test", "After Next Maneuver and Road information");
                Log.d("Test", "---------------------------------------------------------------------------------------");
                Log.d("Test getAfterNextManeuver()",maneuver.toString());
                Log.d("Test getAfterNextManeuverDistance()", "Distance: " + m_navigationManager.getAfterNextManeuverDistance() + "m");
                Log.d("Test getNextRoadName()", maneuver.getNextRoadName());
                Log.d("Test getNextRoadNumber()", maneuver.getNextRoadNumber());
                Log.d("Test getTurn()", maneuver.getTurn().toString());
            }
            */
            Log.d("Test", "=======================================================================================");
        }
    };
    private OnPositionChangedListener positionListener = new OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
            /*
            Log.d("Test Speed", "" + geoPosition.getSpeed() + "\tHeading: " + geoPosition.getHeading());
            RoadElement roadElement = m_positioningManager.getRoadElement();
            Log.d("Test RoadName", "" + roadElement.getRoadName() + "\tLaneNumber: " + roadElement.getNumberOfLanes());
            Log.d("Test SpeedLimit", "" + roadElement.getSpeedLimit() + "\tLinkId: " + roadElement.getPermanentLinkId());
            */
        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {

        }
    };
    private NavigationManager.PositionListener m_positionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(GeoPosition geoPosition) {
            //Log.d("Test Speed", "" + geoPosition.getSpeed());

        }
    };

    // Google has deprecated android.app.Fragment class. It is used in current SDK implementation.
    // Will be fixed in future SDK version.
    @SuppressWarnings("deprecation")
    private MapFragment getMapFragment() {
        return (MapFragment) m_activity.getFragmentManager().findFragmentById(R.id.mapFragmentView);
    }

    /*
     * Android 8.0 (API level 26) limits how frequently background apps can retrieve the user's
     * current location. Apps can receive location updates only a few times each hour.
     * See href="https://developer.android.com/about/versions/oreo/background-location-limits.html
     * In order to retrieve location updates more frequently start a foreground service.
     * See https://developer.android.com/guide/components/services.html#Foreground
     */
    private void startForegroundService() {
        if (!m_foregroundServiceStarted) {
            m_foregroundServiceStarted = true;
            Intent startIntent = new Intent(m_activity, ForegroundService.class);
            startIntent.setAction(ForegroundService.START_ACTION);
            m_activity.getApplicationContext().startService(startIntent);
        }
    }

    private void stopForegroundService() {
        if (m_foregroundServiceStarted) {
            m_foregroundServiceStarted = false;
            Intent stopIntent = new Intent(m_activity, ForegroundService.class);
            stopIntent.setAction(ForegroundService.STOP_ACTION);
            m_activity.getApplicationContext().startService(stopIntent);
        }
    }

    private void initGuidanceManeuverView(Route route) {
        guidanceManeuverView = m_activity.findViewById(R.id.guidanceManeuverView);
        guidanceManeuverPresenter = new GuidanceManeuverPresenter(m_activity.getApplicationContext(), m_navigationManager, route);
        guidanceManeuverPresenter.addListener(guidanceManeuverListener);
    }


    private void createRoute() {
        /* Initialize a CoreRouter */
        CoreRouter coreRouter = new CoreRouter();

        /* Initialize a RoutePlan */
        RoutePlan routePlan = new RoutePlan();

        /*
         * Initialize a RouteOption.HERE SDK allow users to define their own parameters for the
         * route calculation,including transport modes,route types and route restrictions etc.Please
         * refer to API doc for full list of APIs
         */
        RouteOptions routeOptions = new RouteOptions();
        /* Other transport modes are also available e.g Pedestrian */
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        /* Disable highway in this route. */
        routeOptions.setHighwaysAllowed(true);
        /* Calculate the shortest route available. */
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        /* Calculate 1 route. */
        routeOptions.setRouteCount(1);
        /* Finally set the route option */
        routePlan.setRouteOptions(routeOptions);

        //GeoCoordinate gc0 = new GeoCoordinate(25.12967,121.73993);
        //GeoCoordinate gc1 = new GeoCoordinate(25.06343,121.5513);

        ArrayList<GeoCoordinate> waypointList = new ArrayList<>();
        waypointList.add(new GeoCoordinate(25.12967, 121.73993));
        waypointList.add(new GeoCoordinate(25.06343, 121.5513));
        for (int i = 0; i < waypointList.size(); i++) {
            GeoCoordinate coord = waypointList.get(i);
            RouteWaypoint waypoint = new RouteWaypoint(new GeoCoordinate(coord.getLatitude(), coord.getLongitude()));
            if (i != 0 && i != waypointList.size() - 1) {
                waypoint.setWaypointType(RouteWaypoint.Type.VIA_WAYPOINT);
            } else {
                m_map.addMapObject(new MapMarker().setCoordinate(waypoint.getOriginalPosition()));
            }
            routePlan.addWaypoint(waypoint);
        }

        m_map.getPositionIndicator().setVisible(true);


        /* Trigger the route calculation,results will be called back via the listener */
        coreRouter.calculateRoute(routePlan, new Router.Listener<List<RouteResult>, RoutingError>() {
            @Override
            public void onProgress(int i) {
                /* The calculation progress can be retrieved in this callback. */
            }

            @Override
            public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                 RoutingError routingError) {
                /* Calculation is done.Let's handle the result */
                if (routingError == RoutingError.NONE) {
                    if (routeResults.get(0).getRoute() != null) {
                        m_route = routeResults.get(0).getRoute();
                        initGuidanceManeuverView(m_route); //啟動顯示導引文字
                        /* Create a MapRoute so that it can be placed on the map */
                        MapRoute mapRoute = new MapRoute(routeResults.get(0).getRoute());
                        /* Show the maneuver number on top of the route */
                        mapRoute.setManeuverNumberVisible(true);
                        /* Add the MapRoute to the map */
                        m_map.addMapObject(mapRoute);
                        /*
                         * We may also want to make sure the map view is orientated properly
                         * so the entire route can be easily seen.
                         */
                        m_geoBoundingBox = routeResults.get(0).getRoute().getBoundingBox();
                        m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE,
                                Map.MOVE_PRESERVE_ORIENTATION);
                        startNavigation();
                    } else {
                        Toast.makeText(m_activity,
                                "Error:route results returned is not valid",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(m_activity,
                            "Error:route calculation returned error code: " + routingError,
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void initNaviControlButton() {
        m_naviControlButton = m_activity.findViewById(R.id.naviCtrlButton);
        m_naviControlButton.setText(R.string.start_navi);
        m_naviControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_route == null) {
                    createRoute();
                } else {
                    m_navigationManager.stop();
                    m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE, 0f);
                    m_naviControlButton.setText(R.string.start_navi);
                    m_route = null;
                    guidanceManeuverPresenter.pause();
                    m_activity.findViewById(R.id.guidanceManeuverView).setVisibility(View.GONE);
                }
            }
        });
    }

    private void startNavigation() {
        m_naviControlButton.setText(R.string.stop_navi);
        /* Configure Navigation manager to launch navigation on current map */
        m_navigationManager.setMap(m_map);

        /*
         * Start the turn-by-turn navigation.Please note if the transport mode of the passed-in
         * route is pedestrian, the NavigationManager automatically triggers the guidance which is
         * suitable for walking. Simulation and tracking modes can also be launched at this moment
         * by calling either simulate() or startTracking()
         */

        /* Choose navigation modes between real time navigation and simulation */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_activity);
        alertDialogBuilder.setTitle("Navigation");
        alertDialogBuilder.setMessage("Choose Mode");
        alertDialogBuilder.setNegativeButton("Navigation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                m_navigationManager.startNavigation(m_route);
                startForegroundService();
            }

        });
        alertDialogBuilder.setPositiveButton("Simulation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                guidanceManeuverPresenter.resume();
                m_navigationManager.simulate(m_route, simulationSpeedMs);
                //m_activity.findViewById(R.id.guidanceManeuverView).setVisibility(View.VISIBLE);
                m_map.setTilt(60);
                m_map.setTransformCenter(new PointF(
                        (float) (m_map.getWidth() * 0.5),
                        (float) (m_map.getHeight() * 0.9)
                ));
                startForegroundService();
            }

        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        /*
         * Set the map update mode to ROADVIEW.This will enable the automatic map movement based on
         * the current location.If user gestures are expected during the navigation, it's
         * recommended to set the map update mode to NONE first. Other supported update mode can be
         * found in HERE Android SDK API doc
         */

        m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);

        /*
         * NavigationManager contains a number of listeners which we can use to monitor the
         * navigation status and getting relevant instructions.In this example, we will add 2
         * listeners for demo purpose,please refer to HERE Android SDK API documentation for details
         */
        addNavigationListeners();
    }

    private void addNavigationListeners() {
        /*
         * Register a NavigationManagerEventListener to monitor the status change on
         * NavigationManager
         */
        m_navigationManager.addNavigationManagerEventListener(new WeakReference<>(m_navigationManagerEventListener));

        m_navigationManager.addLaneInformationListener(new WeakReference<>(m_LaneInformationListener));
        m_navigationManager.addNewInstructionEventListener(new WeakReference<>(m_newInstructionEventListener));
        m_navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        m_navigationManager.addRealisticViewAspectRatio(NavigationManager.AspectRatio.AR_4x3);
        m_navigationManager.addRealisticViewListener(new WeakReference<>(m_realisticViewListener));
        m_navigationManager.addPositionListener(new WeakReference<>(m_positionListener));
        m_positioningManager.addListener(new WeakReference<>(positionListener));
    }

    private NavigationManager.LaneInformationListener m_LaneInformationListener = new NavigationManager.LaneInformationListener() {
        @Override
        public void onLaneInformation(List<LaneInformation> list, RoadElement roadElement) {
            super.onLaneInformation(list, roadElement);
            Log.d("Test", "=======================================================================================");
            Log.d("Test", "Lane information");
            Log.d("Test", "---------------------------------------------------------------------------------------");
            for (LaneInformation laneInformation : list) {
                Log.d("Test", "Lane Directions " + laneInformation.getDirections());
                Log.d("Test", "Recommended " + laneInformation.getRecommendationState());
            }
        }
    };

    private NavigationManager.RealisticViewListener m_realisticViewListener = new NavigationManager.RealisticViewListener() {
        @Override
        public void onRealisticViewNextManeuver(NavigationManager.AspectRatio aspectRatio, Image junction, Image signpost) {
            Log.d("Test", "=======================================================================================");
            Log.d("Test", "Aspect Ratio: " + aspectRatio.toString());
            Log.d("Test", "---------------------------------------------------------------------------------------");
            Log.d("Test", "Junction Image");
            Log.d("Test", "---------------------------------------------------------------------------------------");
            Log.d("Test", "Type: " + junction.getType().toString());
            Log.d("Test", "Width: " + junction.getWidth());
            Log.d("Test", "Height: " + junction.getHeight());
            Log.d("Test", "---------------------------------------------------------------------------------------");
            Log.d("Test", "Signpost Image");
            Log.d("Test", "---------------------------------------------------------------------------------------");
            Log.d("Test", "Type: " + signpost.getType().toString());
            Log.d("Test", "Width: " + signpost.getWidth());
            Log.d("Test", "Height: " + signpost.getHeight());
            Log.d("Test", "=======================================================================================");
        }

        @Override
        public void onRealisticViewShow(NavigationManager.AspectRatio aspectRatio, Image junction, Image signpost) {
            Log.d("Test", "=======================================================================================");
            Log.d("Test", "Aspect Ratio: " + aspectRatio.toString());
            Log.d("Test", "---------------------------------------------------------------------------------------");
            Log.d("Test", "Junction Image");
            Log.d("Test", "---------------------------------------------------------------------------------------");
            Log.d("Test", "Type: " + junction.getType().toString());
            Log.d("Test", "Width: " + junction.getWidth());
            Log.d("Test", "Height: " + junction.getHeight());
            Log.d("Test", "---------------------------------------------------------------------------------------");
            Log.d("Test", "Signpost Image");
            Log.d("Test", "---------------------------------------------------------------------------------------");
            Log.d("Test", "Type: " + signpost.getType().toString());
            Log.d("Test", "Width: " + signpost.getWidth());
            Log.d("Test", "Height: " + signpost.getHeight());
            Log.d("Test", "=======================================================================================");
        }
    };

    private NavigationManager.NavigationManagerEventListener m_navigationManagerEventListener = new NavigationManager.NavigationManagerEventListener() {
        @Override
        public void onRunningStateChanged() {
            Toast.makeText(m_activity, "Running state changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNavigationModeChanged() {
            Toast.makeText(m_activity, "Navigation mode changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnded(NavigationManager.NavigationMode navigationMode) {
            Toast.makeText(m_activity, navigationMode + " was ended", Toast.LENGTH_SHORT).show();
            stopForegroundService();
        }

        @Override
        public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
            Toast.makeText(m_activity, "Map update mode is changed to " + mapUpdateMode,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRouteUpdated(Route route) {
            Toast.makeText(m_activity, "Route updated", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCountryInfo(String s, String s1) {
            Toast.makeText(m_activity, "Country info updated from " + s + " to " + s1,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void onDestroy() {
        /* Stop the navigation when app is destroyed */
        if (m_navigationManager != null) {
            stopForegroundService();
            m_navigationManager.stop();
        }
    }
}

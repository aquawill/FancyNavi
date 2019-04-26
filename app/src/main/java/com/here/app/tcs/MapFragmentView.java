package com.here.app.tcs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.here.android.mpa.common.CopyrightLogoPosition;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.MapSettings;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.PositioningManager.OnPositionChangedListener;
import com.here.android.mpa.common.RoadElement;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.guidance.LaneInformation;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.SafetySpotNotification;
import com.here.android.mpa.guidance.SafetySpotNotificationInfo;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoiceGuidanceOptions;
import com.here.android.mpa.guidance.VoicePackage;
import com.here.android.mpa.guidance.VoiceSkin;
import com.here.android.mpa.mapping.LocalMesh;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapLabeledMarker;
import com.here.android.mpa.mapping.MapLocalModel;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.customization.CustomizableScheme;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here.msdkui.guidance.GuidanceManeuverData;
import com.here.msdkui.guidance.GuidanceManeuverListener;
import com.here.msdkui.guidance.GuidanceManeuverPresenter;
import com.here.msdkui.guidance.GuidanceManeuverView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;

import static java.util.Locale.TRADITIONAL_CHINESE;

//HERE SDK UI KIT components

/**
 * This class encapsulates the properties and functionality of the Map view.It also triggers a
 * turn-by-turn navigation from HERE Burnaby office to Langley BC.There is a sample voice skin
 * bundled within the SDK package to be used out-of-box, please refer to the Developer's guide for
 * the usage.
 */
class MapFragmentView {
    Map m_map;
    NavigationManager m_navigationManager;
    boolean isRoadView = false;
    boolean isDragged = false;
    //private MapFragment supportMapFragment; //Deprecated
    private SupportMapFragment supportMapFragment;
    //private Activity m_activity; //Deprecated
    private AppCompatActivity m_activity;
    private Button m_naviControlButton;
    private PositioningManager m_positioningManager;
    private GeoBoundingBox mapRouteBBox;
    private Route m_route;
    private MapLocalModel mapLocalModel;
    private boolean m_foregroundServiceStarted;
    //HERE SDK UI KIT components
    private GuidanceManeuverView guidanceManeuverView;
    private GuidanceManeuverPresenter guidanceManeuverPresenter;
    private boolean isRouteOverView = false;
    private MapRoute mapRoute;
    private ArrayList<GeoCoordinate> waypointList = new ArrayList<>();
    private ArrayList<MapObject> waypointIconList = new ArrayList<>();
    //HERE UI Kit, Guidance Maneuver View
    private GuidanceManeuverListener guidanceManeuverListener = new GuidanceManeuverListener() {
        @Override
        public void onDataChanged(@Nullable GuidanceManeuverData guidanceManeuverData) {
            guidanceManeuverView.setManeuverData(guidanceManeuverData);
            if (guidanceManeuverData != null) {
                Log.d("Test", "guidanceManeuverData.getInfo1(): " + guidanceManeuverData.getInfo1());
                Log.d("Test", "guidanceManeuverData.getInfo2(): " + guidanceManeuverData.getInfo2());
            }
        }

        @Override
        public void onDestinationReached() {
            guidanceManeuverView.highLightManeuver(Color.BLUE);
        }
    };
    private long simulationSpeedMs = 20; //defines the speed of navigation simulation
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            shiftMapCenter(m_map);
        }
    };
    private ImageView junctionViewImageView;
    private ImageView signpostImageView;
    private OnTouchListener mapOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (m_navigationManager.getMapUpdateMode() != NavigationManager.MapUpdateMode.NONE) {
                isRoadView = false;
                m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
                resetMapCenter(m_map);
                m_map.setTilt(0);
                m_map.zoomTo(m_route.getBoundingBox(), Map.Animation.NONE, 0f);
            }
            return false;
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
            m_map.setMapScheme(Map.Scheme.CARNAV_DAY);
            resetMapCenter(m_map);
            m_map.removeMapObject(mapLocalModel);
            m_map.setTilt(0);
            m_map.zoomTo(mapRouteBBox, Map.Animation.NONE, 0f);
            View guidanceView = m_activity.findViewById(R.id.guidanceManeuverView);
            guidanceView.setVisibility(View.GONE);
            if (mapRoute != null) {
                m_map.removeMapObject(mapRoute);
            }
            stopForegroundService();
        }

        @Override
        public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
            //Toast.makeText(m_activity, "Map update mode is changed to " + mapUpdateMode, Toast.LENGTH_SHORT).show();
            Log.d("Test", "mapUpdateMode is: " + mapUpdateMode);
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
    private NavigationManager.NewInstructionEventListener m_newInstructionEventListener = new NavigationManager.NewInstructionEventListener() {
        @Override
        public void onNewInstructionEvent() {
            Log.d("Test", "getNextManeuver().getRoadName(): " + m_navigationManager.getNextManeuver().getRoadName());
            Log.d("Test", "getNextManeuver().getNextRoadName(): " + m_navigationManager.getNextManeuver().getNextRoadName());
        }
    };
    private NavigationManager.SafetySpotListener safetySpotListener = new NavigationManager.SafetySpotListener() {
        @Override
        public void onSafetySpot(SafetySpotNotification safetySpotNotification) {
            super.onSafetySpot(safetySpotNotification);
            List<SafetySpotNotificationInfo> safetySpotInfos = safetySpotNotification.getSafetySpotNotificationInfos();
            for (int i = 0; i < safetySpotInfos.size(); i++) {
                SafetySpotNotificationInfo safetySpotInfo = safetySpotInfos.get(i);
                Log.d("Test", "" + safetySpotInfo.getSafetySpot().getType().toString());
            }

        }
    };
    private GeoPosition previousKnownPosition;
    private OnPositionChangedListener positionListener = new OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
            if (!isRouteOverView) {
                if (!isDragged) {
                    if (!geoPosition.equals(previousKnownPosition)) {
                        if (!geoPosition.equals(previousKnownPosition)) {
//                    m_map.setCenter(geoPosition.getCoordinate(), Map.Animation.NONE);
                        }
                        m_map.setCenter(geoPosition.getCoordinate(), Map.Animation.NONE);
                    }
                }

            }
        }


        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
            Log.d("Test", "locationMethod: " + locationMethod + " locationStatus: " + locationStatus);

        }
    };
    private NavigationManager.PositionListener m_positionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(GeoPosition geoPosition) {
            //Log.d("Test Speed", "" + geoPosition.getSpeed());
            m_map.setCenter(geoPosition.getCoordinate(), Map.Animation.LINEAR);
            mapLocalModel.setAnchor(geoPosition.getCoordinate());
            mapLocalModel.setYaw((float) geoPosition.getHeading());
            previousKnownPosition = geoPosition;
            if (!isRouteOverView) {
                if (!geoPosition.equals(previousKnownPosition)) {
                    m_map.setCenter(geoPosition.getCoordinate(), Map.Animation.NONE);
                }

            }

        }
    };
    private NavigationManager.LaneInformationListener m_LaneInformationListener = new NavigationManager.LaneInformationListener() {
        @Override
        public void onLaneInformation(List<LaneInformation> list, RoadElement roadElement) {
            super.onLaneInformation(list, roadElement);
            /*
            Log.d("Test", "=======================================================================================");
            Log.d("Test", "Lane information");
            Log.d("Test", "---------------------------------------------------------------------------------------");
            for (LaneInformation laneInformation : list) {
                Log.d("Test", "Lane Directions " + laneInformation.getDirections());
                Log.d("Test", "Recommended " + laneInformation.getRecommendationState());
            }
            */
        }
    };
    private NavigationManager.RealisticViewListener m_realisticViewListener = new NavigationManager.RealisticViewListener() {
        @Override
        public void onRealisticViewNextManeuver(NavigationManager.AspectRatio aspectRatio, Image junction, Image signpost) {
        }

        @Override
        public void onRealisticViewShow(NavigationManager.AspectRatio aspectRatio, Image junction, Image signpost) {
            junctionViewImageView.setVisibility(View.VISIBLE);
            signpostImageView.setVisibility(View.VISIBLE);
            Bitmap junctionBitmap = junction.getBitmap((int) junction.getWidth(), (int) junction.getHeight());
            Bitmap signpostBitMap = signpost.getBitmap((int) signpost.getWidth(), (int) signpost.getHeight());
            junctionViewImageView.setImageBitmap(junctionBitmap);
            signpostImageView.setImageBitmap(signpostBitMap);
        }

        @Override
        public void onRealisticViewHide() {
            junctionViewImageView.setVisibility(View.GONE);
            signpostImageView.setVisibility(View.GONE);
        }
    };

    MapFragmentView(AppCompatActivity activity) {
        m_activity = activity;
        initSupportMapFragment();
        initNaviControlButton();
    }

    private void initSupportMapFragment() {
        /* Locate the mapFragment UI element */
        supportMapFragment = getMapFragment();
        supportMapFragment.setCopyrightLogoPosition(CopyrightLogoPosition.BOTTOM_RIGHT);
        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath() + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }


        boolean success = MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            if (supportMapFragment != null) {
                /* Initialize the MapFragment, results will be given via the called back. */
                supportMapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(Error error) {
                        if (error == Error.NONE) {
                            supportMapFragment.getMapGesture()
                                    .addOnGestureListener(new MapGesture.OnGestureListener() {

                                        @Override
                                        public void onPanStart() {
                                            isDragged = true;
                                        }

                                        @Override
                                        public void onPanEnd() {

                                        }

                                        @Override
                                        public void onMultiFingerManipulationStart() {

                                        }

                                        @Override
                                        public void onMultiFingerManipulationEnd() {

                                        }

                                        @Override
                                        public boolean onMapObjectsSelected(List<ViewObject> list) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onTapEvent(PointF pointF) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onDoubleTapEvent(PointF pointF) {
                                            return false;
                                        }

                                        @Override
                                        public void onPinchLocked() {

                                        }

                                        @Override
                                        public boolean onPinchZoomEvent(float v, PointF pointF) {
                                            return false;
                                        }

                                        @Override
                                        public void onRotateLocked() {

                                        }

                                        @Override
                                        public boolean onRotateEvent(float v) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onTiltEvent(float v) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onLongPressEvent(PointF pointF) {
                                            Log.d("Test", "PointF X: " + pointF.x);
                                            Log.d("Test", "PointF Y: " + pointF.y);
                                            GeoBoundingBox geoBoundingBox = m_map.getBoundingBox();
                                            int supportMapFragmentHeight = supportMapFragment.getClipRect().getHeight();
                                            int supportMapFragmentWidth = supportMapFragment.getClipRect().getWidth();
                                            double mapTopLeftLat = geoBoundingBox.getTopLeft().getLatitude();
                                            double mapTopLeftLng = geoBoundingBox.getTopLeft().getLongitude();
                                            double mapBottomRightLat = geoBoundingBox.getBottomRight().getLatitude();
                                            double mapBottomRightLng = geoBoundingBox.getBottomRight().getLongitude();
                                            double yRatio = Math.abs((mapBottomRightLat - mapTopLeftLat) / supportMapFragmentHeight);
                                            double xRatio = Math.abs((mapBottomRightLng - mapTopLeftLng) / supportMapFragmentWidth);
                                            double pointLat = mapTopLeftLat - pointF.y * yRatio;
                                            double pointLng = mapTopLeftLng + pointF.x * xRatio;
                                            GeoCoordinate touchPointGeoCoordinate = new GeoCoordinate(pointLat, pointLng);
                                            MapMarker mapMarker = new MapMarker(touchPointGeoCoordinate);
                                            waypointIconList.add(mapMarker);
                                            mapMarker.setAnchorPoint(getMapMarkerAnchorPoint(mapMarker));
                                            waypointList.add(new GeoCoordinate(pointLat, pointLng));
                                            MapLabeledMarker mapLabeledMarker = new MapLabeledMarker(touchPointGeoCoordinate);
                                            mapLabeledMarker.setLabelText("eng", "Waypoint Index " + (waypointList.size() - 1));
                                            mapLabeledMarker.setFontScalingFactor(2.0f);
                                            mapLabeledMarker.setZIndex(10);
                                            m_map.addMapObject(mapMarker);
                                            return false;
                                        }

                                        @Override
                                        public void onLongPressRelease() {

                                        }

                                        @Override
                                        public boolean onTwoFingerTapEvent(PointF pointF) {
                                            return false;
                                        }
                                    }, 0, false);
                            m_map = supportMapFragment.getMap();
                            m_map.setMapDisplayLanguage(TRADITIONAL_CHINESE);
                            //m_map.setTrafficInfoVisible(true);
                            m_map.setSafetySpotsVisible(true);
                            m_map.setExtrudedBuildingsVisible(false);
                            m_positioningManager = PositioningManager.getInstance();
                            m_positioningManager.addListener(new WeakReference<>(positionListener));
                            EnumSet<PositioningManager.LogType> logTypes = EnumSet.of(
                                    PositioningManager.LogType.RAW,
                                    PositioningManager.LogType.DATA_SOURCE
                            );

                            m_positioningManager.setLogType(logTypes);
                            m_map.getPositionIndicator().setVisible(true);
                            if (m_positioningManager != null) {
                                m_positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
                            }
                            VoiceActivation voiceActivation = new VoiceActivation();
                            voiceActivation.downloadCatalogAndSkin();
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

    // Google has deprecated android.app.Fragment class. It is used in current SDK implementation.
    // Will be fixed in future SDK version.
    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapFragmentView);
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

    private void initNaviControlButton() {
        m_naviControlButton = m_activity.findViewById(R.id.naviCtrlButton);
        m_naviControlButton.setText("Start Navi");
        m_naviControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_route == null) {
                    m_navigationManager = NavigationManager.getInstance();
                    try {
                        createRoute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    m_navigationManager.stop();
                    m_map.zoomTo(mapRouteBBox, Map.Animation.NONE, 0f);
                    m_naviControlButton.setText("Start Navi");
                    m_route = null;
                    guidanceManeuverPresenter.pause();
                    m_activity.findViewById(R.id.guidanceManeuverView).setVisibility(View.GONE);
                    m_map.getPositionIndicator().setVisible(true);
                }
            }
        });
    }

    private void hudMapScheme(Map map) {
        /*Map Customization - Start*/
        CustomizableScheme m_colorScheme;
        String m_colorSchemeName = "colorScheme";
        if (map != null && map.getCustomizableScheme(m_colorSchemeName) == null) {
            map.createCustomizableScheme(m_colorSchemeName, Map.Scheme.CARNAV_NIGHT_GREY);
            map.setMapScheme(Map.Scheme.CARNAV_NIGHT_GREY);
            /*
            m_colorScheme = map.getCustomizableScheme(m_colorSchemeName);
            ZoomRange range = new ZoomRange(0.0, 20.0);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);

            map.setMapScheme(m_colorScheme);
            map.setLandmarksVisible(false);
            map.setExtrudedBuildingsVisible(false);
            map.setCartoMarkersVisible(false);
            */
            EnumSet<Map.LayerCategory> invisibleLayerCategory = EnumSet.of(
                    Map.LayerCategory.ABSTRACT_CITY_MODEL
            );
            map.setVisibleLayers(invisibleLayerCategory, false);

        } else {
            if (map != null) {
                map.setMapScheme(Map.Scheme.CARNAV_NIGHT);
            }
        }
        /*Map Customization - End*/
    }

    private MapLocalModel createPosition3dObj() {
        MapLocalModel mapLocalModel = new MapLocalModel();
        LocalModelLoader localModelLoader = new LocalModelLoader();

        LocalMesh localMesh = new LocalMesh();
        localMesh.setVertices(localModelLoader.getObjVertices());
        localMesh.setVertexIndices(localModelLoader.getObjIndices());
        localMesh.setTextureCoordinates(localModelLoader.getObjTexCoords());

        mapLocalModel.setMesh(localMesh);
        Image image = null;
        try {
            image = new Image();
            image.setImageResource(R.drawable.green);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapLocalModel.setTexture(image); //an Image object
        mapLocalModel.setScale(5.0f);
        mapLocalModel.setDynamicScalingEnabled(true);

        m_map.addMapObject(mapLocalModel);
        return mapLocalModel;
    }

    private void initJunctionView() {
        junctionViewImageView = m_activity.findViewById(R.id.junctionImageView);
        junctionViewImageView.setVisibility(View.GONE);
        signpostImageView = m_activity.findViewById(R.id.signpostImageView);
        signpostImageView.setVisibility(View.GONE);
    }

    private void initGuidanceManeuverView(Route route) {
        guidanceManeuverView = m_activity.findViewById(R.id.guidanceManeuverView);
        guidanceManeuverPresenter = new GuidanceManeuverPresenter(m_activity.getApplicationContext(), m_navigationManager, route);
        guidanceManeuverPresenter.addListener(guidanceManeuverListener);
    }

    void shiftMapCenter(Map map) {
        map.setTransformCenter(new PointF(
                (float) (map.getWidth() * 0.5),
                (float) (map.getHeight() * 0.8)
        ));
    }

    private void resetMapCenter(Map map) {
        map.setTransformCenter(new PointF(
                (float) (map.getWidth() * 0.5),
                (float) (map.getHeight() * 0.5)
        ));
    }

    private void intoNavigationMode() {

        m_map.getPositionIndicator().setVisible(false);
        guidanceManeuverPresenter.resume();
        m_activity.findViewById(R.id.guidanceManeuverView).setVisibility(View.VISIBLE);
        m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
        isRoadView = true;
        m_navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        EnumSet<NavigationManager.NaturalGuidanceMode> naturalGuidanceModes = EnumSet.of(
                NavigationManager.NaturalGuidanceMode.JUNCTION,
                NavigationManager.NaturalGuidanceMode.STOP_SIGN,
                NavigationManager.NaturalGuidanceMode.TRAFFIC_LIGHT
        );
        m_navigationManager.setTrafficAvoidanceMode(NavigationManager.TrafficAvoidanceMode.DYNAMIC);
        m_navigationManager.setNaturalGuidanceMode(naturalGuidanceModes);
        shiftMapCenter(m_map);
        //hudMapScheme(m_map);
        m_map.setTilt(60);
        m_navigationManager.startNavigation(m_route);
        m_positioningManager.setMapMatchingEnabled(true);
        EnumSet<NavigationManager.AudioEvent> audioEventEnumSet = EnumSet.of(
                NavigationManager.AudioEvent.MANEUVER,
                NavigationManager.AudioEvent.ROUTE,
                NavigationManager.AudioEvent.SAFETY_SPOT,
                NavigationManager.AudioEvent.SPEED_LIMIT
        );
        m_navigationManager.setEnabledAudioEvents(audioEventEnumSet);
        supportMapFragment.setOnTouchListener(mapOnTouchListener);
        mapLocalModel = createPosition3dObj();
    }

    private void startNavigation() {
        resetMapCenter(m_map);
        m_naviControlButton.setText("Stop Navi");
        m_navigationManager.setMap(m_map);
        m_map.zoomTo(mapRouteBBox, Map.Animation.NONE, 0f);

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
                intoNavigationMode();
                isRouteOverView = false;
                NavigationManager.Error error = m_navigationManager.startNavigation(m_route);
                Log.e("Error: ", error.toString());
                Toast.makeText(m_activity, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                startForegroundService();
            }
        });
        alertDialogBuilder.setPositiveButton("Simulation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                intoNavigationMode();
                NavigationManager.Error error = m_navigationManager.simulate(m_route, simulationSpeedMs);
                Toast.makeText(m_activity, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                startForegroundService();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        addNavigationListeners();
    }

    // https://www.jianshu.com/p/0555b8c1d26a
    // https://stackoverflow.com/questions/33696488/getting-bitmap-from-vector-drawable
    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            assert drawable != null;
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private PointF getMapMarkerAnchorPoint(MapMarker mapMarker) {
        int iconHeight = (int) mapMarker.getIcon().getHeight();
        int iconWidth = (int) mapMarker.getIcon().getWidth();
        return new PointF((float) (iconWidth / 2), (float) iconHeight);
    }

    private void createRoute() throws IOException {

        m_map.removeMapObjects(waypointIconList);
        CoreRouter coreRouter = new CoreRouter();

        RoutePlan routePlan = new RoutePlan();

        RouteOptions routeOptionCarAllowHighway = new RouteOptions();
        routeOptionCarAllowHighway.setTransportMode(RouteOptions.TransportMode.CAR);
        routeOptionCarAllowHighway.setHighwaysAllowed(true);
        routeOptionCarAllowHighway.setRouteType(RouteOptions.Type.FASTEST);
        routeOptionCarAllowHighway.setRouteCount(1);
        routePlan.setRouteOptions(routeOptionCarAllowHighway);

        //waypointList.add(m_positioningManager.getPosition().getCoordinate());
        //waypointList.add(new GeoCoordinate(-26.1001341, 28.173676100000005));
        //waypointList.add(new GeoCoordinate(-26.101369899999987, 28.1758224));
        //waypointList.add(new GeoCoordinate(-26.104472700000002, 28.173528200000003));
        //waypointList.add(new GeoCoordinate(-26.10483949999999, 28.1724682));

        if (waypointList.size() == 1) {
            waypointList.add(0, m_positioningManager.getPosition().getCoordinate());
        } else if (waypointList.isEmpty()) {
            Toast.makeText(m_activity, "wayPointList is empty.", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < waypointList.size(); i++) {
            GeoCoordinate coord = waypointList.get(i);
            RouteWaypoint waypoint = new RouteWaypoint(new GeoCoordinate(coord.getLatitude(), coord.getLongitude()));
            MapMarker mapMarker = new MapMarker();
            Image icon = new Image();
            if (i != 0 && i != waypointList.size() - 1) {
                waypoint.setWaypointType(RouteWaypoint.Type.VIA_WAYPOINT);
                mapMarker.setCoordinate(waypoint.getOriginalPosition());
                MapLabeledMarker mapLabeledMarker = new MapLabeledMarker(waypoint.getOriginalPosition());
                mapLabeledMarker.setLabelText("eng", "Waypoint Index " + i);
                mapLabeledMarker.setFontScalingFactor(2.0f);
                mapLabeledMarker.setZIndex(10);
                m_map.addMapObject(mapLabeledMarker);
            } else {
                if (i == 0) {
                    icon.setBitmap(getBitmapFromVectorDrawable(m_activity, R.drawable.ic_orig));
                    mapMarker.setCoordinate(waypoint.getOriginalPosition()).setIcon(icon);
                } else if (i == waypointList.size() - 1) {
                    icon.setBitmap(getBitmapFromVectorDrawable(m_activity, R.drawable.ic_dest));
                    mapMarker.setCoordinate(waypoint.getOriginalPosition()).setIcon(icon);
                }
            }
            m_map.addMapObject(mapMarker);
            mapMarker.setAnchorPoint(getMapMarkerAnchorPoint(mapMarker));
            routePlan.addWaypoint(waypoint);
        }

        /* Trigger the route calculation,results will be called back via the listener */
        coreRouter.calculateRoute(routePlan, new Router.Listener<List<RouteResult>, RoutingError>() {
            @Override
            public void onProgress(int i) {
                /* The calculation progress can be retrieved in this callback. */
            }

            @Override
            public void onCalculateRouteFinished(List<RouteResult> routeResults, RoutingError routingError) {
                /* Calculation is done.Let's handle the result */
                if (routingError == RoutingError.NONE) {
                    if (routeResults.get(0).getRoute() != null) {
                        isRouteOverView = true;
                        m_route = routeResults.get(0).getRoute();
                        initGuidanceManeuverView(m_route);
                        initJunctionView();
                        mapRoute = new MapRoute(routeResults.get(0).getRoute());
                        //mapRoute.setManeuverNumberVisible(true);
                        mapRoute.setColor(Color.argb(255, 243, 174, 255)); //F3AEFF
                        mapRoute.setOutlineColor(Color.argb(255, 78, 0, 143)); //4E008F
                        mapRoute.setTraveledColor(Color.DKGRAY);
                        m_map.addMapObject(mapRoute);
                        mapRouteBBox = routeResults.get(0).getRoute().getBoundingBox();
                        m_map.zoomTo(mapRouteBBox, Map.Animation.NONE, Map.MOVE_PRESERVE_ORIENTATION);
                        startNavigation();
                    } else {
                        Toast.makeText(m_activity, "Error:route results returned is not valid", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(m_activity, "Error:route calculation returned error code: " + routingError, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addNavigationListeners() {
        /*
         * Register a NavigationManagerEventListener to monitor the status change on
         * NavigationManager
         */
        m_activity.findViewById(R.id.mapFragmentView).getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        m_navigationManager.addNavigationManagerEventListener(new WeakReference<>(m_navigationManagerEventListener));
        m_navigationManager.addLaneInformationListener(new WeakReference<>(m_LaneInformationListener));
        m_navigationManager.addNewInstructionEventListener(new WeakReference<>(m_newInstructionEventListener));
        m_navigationManager.addSafetySpotListener(new WeakReference<>(safetySpotListener));
        m_navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        m_navigationManager.addRealisticViewAspectRatio(NavigationManager.AspectRatio.AR_4x3);
        m_navigationManager.addRealisticViewListener(new WeakReference<>(m_realisticViewListener));
        m_navigationManager.addPositionListener(new WeakReference<>(m_positionListener));

    }

    void onDestroy() {
        /* Stop the navigation when app is destroyed */
        if (m_navigationManager != null) {
            stopForegroundService();
            m_navigationManager.stop();
        }
    }

    class LocalModelLoader {
        InputStream objStream = m_activity.getResources().openRawResource(R.raw.arrow_modified);
        Obj obj;

        {
            try {
                obj = ObjReader.read(objStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FloatBuffer getObjTexCoords() {
            FloatBuffer texCoords = ObjData.getTexCoords(obj, 2);
            FloatBuffer textCoordBuffer = FloatBuffer.allocate(texCoords.capacity());
            while (texCoords.hasRemaining()) {
                textCoordBuffer.put(texCoords.get());
            }
            return textCoordBuffer;

        }

        FloatBuffer getObjVertices() {
            FloatBuffer vertices = ObjData.getVertices(obj);
            FloatBuffer buff = FloatBuffer.allocate(vertices.capacity());
            while (vertices.hasRemaining()) {
                buff.put(vertices.get());
            }
            return buff;
        }

        IntBuffer getObjIndices() {
            IntBuffer indices = ObjData.getFaceVertexIndices(obj);
            IntBuffer vertIndicieBuffer = IntBuffer.allocate(indices.capacity());
            while (indices.hasRemaining()) {
                vertIndicieBuffer.put(indices.get());
            }
            return vertIndicieBuffer;
        }
    }

    class VoiceActivation {
        VoiceCatalog voiceCatalog = VoiceCatalog.getInstance();

        void retryVoiceDownload(final long id) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_activity);
            alertDialogBuilder.setTitle("Voice Download Failed");
            alertDialogBuilder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialoginterface, int i) {
                    downloadVoice(id);
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        void downloadVoice(final long voiceSkinId) {
            Log.d("Test", "Downloading voice skin ID: " + voiceSkinId);
            Toast.makeText(m_activity, "Downloading voice skin ID: " + voiceSkinId, Toast.LENGTH_SHORT).show();
            voiceCatalog.downloadVoice(voiceSkinId, new VoiceCatalog.OnDownloadDoneListener() {
                @Override
                public void onDownloadDone(VoiceCatalog.Error error) {
                    if (error != VoiceCatalog.Error.NONE) {
                        retryVoiceDownload(voiceSkinId);
                        Toast.makeText(m_activity, "Failed downloading voice skin " + voiceSkinId, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(m_activity, "Voice skin " + voiceSkinId + " downloaded and activated.", Toast.LENGTH_SHORT).show();
                        //NavigationManager.getInstance().setVoiceSkin(VoiceCatalog.getInstance().getLocalVoiceSkin(voiceSkinId)); //Deprecated in SDK 3.7
                        VoiceSkin localVoiceSkin = voiceCatalog.getLocalVoiceSkin(voiceSkinId);
                        NavigationManager.getInstance().getVoiceGuidanceOptions().setVoiceSkin(localVoiceSkin);
                        Log.d("Test", "Voice skin " + voiceSkinId + " downloaded and activated.");
                    }
                }
            });
        }

        void downloadCatalogAndSkin() {
            int desiredVoiceId = 217; //Taiwan
            final Boolean[] localVoiceSkinExisted = {false};
            VoiceCatalog.getInstance().downloadCatalog(new VoiceCatalog.OnDownloadDoneListener() {
                @Override
                public void onDownloadDone(VoiceCatalog.Error error) {

                    if (error != VoiceCatalog.Error.NONE) {
                        Log.d("Test", "Failed to download catalog.");
                    } else {
//                        Log.d("Test", "Catalog downloaded");
                        List<VoicePackage> packages = VoiceCatalog.getInstance().getCatalogList();
                        Log.d("Test", "# of available packages: " + packages.size());
                        for (VoicePackage lang : packages)
                            Log.d("Test", "\tLanguage name: " + lang.getLocalizedLanguage() + "\tGender: " + lang.getGender() + "\tis TTS: " + lang.isTts() + "\tID: " + lang.getId());
                        List<VoiceSkin> localInstalledSkins = VoiceCatalog.getInstance().getLocalVoiceSkins();
                        localInstalledSkins.clear();
//                        Log.d("Test", "# of local skins: " + localInstalledSkins.size());
                        for (VoiceSkin voice : localInstalledSkins) {
                            Log.d("Test", "ID: " + voice.getId() + " Language: " + voice.getLanguage());
                            if (voice.getId() == desiredVoiceId) {
                                localVoiceSkinExisted[0] = true;
                            }
                        }
                        Log.d("Test", "" + voiceCatalog.getLocalVoiceSkin(desiredVoiceId));
                        if (!localVoiceSkinExisted[0]) {
                            downloadVoice(desiredVoiceId);
                        } else {
                            Toast.makeText(m_activity, "Voice skin " + desiredVoiceId + " downloaded and activated", Toast.LENGTH_SHORT).show();
                            VoiceGuidanceOptions voiceGuidanceOptions = m_navigationManager.getVoiceGuidanceOptions();
                            voiceGuidanceOptions.setVoiceSkin(voiceCatalog.getLocalVoiceSkin(desiredVoiceId));
                        }
                    }
                }
            });
        }
    }
}

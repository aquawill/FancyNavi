package com.fancynavi.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.here.android.mpa.guidance.TrafficNotification;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoiceGuidanceOptions;
import com.here.android.mpa.mapping.LocalMesh;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapLocalModel;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.PositionIndicator;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.customization.CustomizableScheme;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.DynamicPenalty;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.SearchRequest;
import com.here.msdkui.guidance.GuidanceEstimatedArrivalView;
import com.here.msdkui.guidance.GuidanceEstimatedArrivalViewPresenter;
import com.here.msdkui.guidance.GuidanceManeuverData;
import com.here.msdkui.guidance.GuidanceManeuverListener;
import com.here.msdkui.guidance.GuidanceManeuverPresenter;
import com.here.msdkui.guidance.GuidanceManeuverView;
import com.here.msdkui.guidance.GuidanceNextManeuverData;
import com.here.msdkui.guidance.GuidanceNextManeuverListener;
import com.here.msdkui.guidance.GuidanceNextManeuverPresenter;
import com.here.msdkui.guidance.GuidanceNextManeuverView;
import com.here.msdkui.guidance.GuidanceSpeedLimitView;
import com.here.msdkui.guidance.GuidanceSpeedPresenter;
import com.here.msdkui.guidance.GuidanceSpeedView;
import com.here.msdkui.guidance.GuidanceStreetLabelData;
import com.here.msdkui.guidance.GuidanceStreetLabelListener;
import com.here.msdkui.guidance.GuidanceStreetLabelPresenter;
import com.here.msdkui.guidance.GuidanceStreetLabelView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.fancynavi.app.MainActivity.lightSensorValue;
import static java.util.Locale.TRADITIONAL_CHINESE;

//import com.here.msdkui.guidance.GuidanceEstimatedArrivalViewData;
//import com.here.msdkui.guidance.GuidanceEstimatedArrivalViewListener;
//import com.here.msdkui.guidance.GuidanceSpeedData;
//import com.here.msdkui.guidance.GuidanceSpeedListener;

class MapFragmentView {
    static Map m_map;
    static GeoPosition currentGeoPosition;
    static NavigationManager m_navigationManager;
    static Button m_naviControlButton;
    static Button clearButton;
    boolean isRoadView = false;
    boolean isDragged;
    private MapSchemeChanger mapSchemeChanger;
    private boolean safetyCameraAhead;
    private GeoCoordinate safetyCameraLocation;
    private double distanceToSafetyCamera;
    private double safetyCameraSpeedLimit;
    private int safetyCameraSpeedLimitKM;
    private ImageView safetyCamImageView;
    private TextView safetyCamTextView;
    private TextView safetyCamSpeedTextView;
    private MapMarker safetyCameraMapMarker;
    private int speedLimitLinearLayoutHeight;
    private View speedLimitLinearLayout;
    private boolean isRouteOverView;
    private PositioningManager m_positioningManager;
    private AppCompatActivity m_activity;
    private PositionIndicator positionIndicator;
    private SupportMapFragment supportMapFragment;
    private VoiceActivation voiceActivation;
    private Button northUpButton;
    private Button zoomInButton;
    private Button zoomOutButton;
    private Button carRouteButton;
    private Button truckRouteButton;
    private Button scooterRouteButton;
    private Button bikeRouteButton;
    private Button pedsRouteButton;
    private Button trafficButton;

    private boolean trafficEnabled;
    private ProgressBar progressBar;
    private TextView calculatingTextView;
    private Route m_route;
    private MapRoute mapRoute;
    private GeoBoundingBox mapRouteBBox;
    private MapLocalModel mapLocalModel;
    private boolean m_foregroundServiceStarted;
    private CoreRouter coreRouter;
    //HERE SDK UI KIT components
    private GuidanceManeuverView guidanceManeuverView;
    private GuidanceManeuverPresenter guidanceManeuverPresenter;
    private GuidanceEstimatedArrivalViewPresenter guidanceEstimatedArrivalViewPresenter;
    private GuidanceStreetLabelPresenter guidanceStreetLabelPresenter;
    private GuidanceEstimatedArrivalView guidanceEstimatedArrivalView;
    private GuidanceSpeedLimitView guidanceSpeedLimitView;
    private GuidanceSpeedPresenter guidanceSpeedPresenter;
    private GuidanceStreetLabelView guidanceStreetLabelView;
    private GuidanceNextManeuverView guidanceNextManeuverView;
    private GuidanceNextManeuverPresenter guidanceNextManeuverPresenter;
    private GuidanceSpeedView guidanceSpeedView;
    private ArrayList<GeoCoordinate> waypointList = new ArrayList<>();
    private ArrayList<MapMarker> userInputWaypoints = new ArrayList<>();
    private ArrayList<MapMarker> wayPointIcons = new ArrayList<>();
    private ArrayList<MapMarker> placeSearchResultIcons = new ArrayList<>();

    //HERE UI Kit
    private long simulationSpeedMs = 20; //defines the speed of navigation simulation
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
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
                shiftMapCenter(m_map, 0.5f, 0.6f);
                m_map.setTilt(0);
                m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, 0f);
                m_naviControlButton.setVisibility(View.VISIBLE);
                clearButton.setVisibility(View.VISIBLE);
            }
            return false;
        }
    };
    private NavigationManager.NewInstructionEventListener m_newInstructionEventListener = new NavigationManager.NewInstructionEventListener() {
        @Override
        public void onNewInstructionEvent() {

        }
    };
    private NavigationManager.SafetySpotListener safetySpotListener = new NavigationManager.SafetySpotListener() {
        @Override
        public void onSafetySpot(SafetySpotNotification safetySpotNotification) {
            super.onSafetySpot(safetySpotNotification);
            List<SafetySpotNotificationInfo> safetySpotInfos = safetySpotNotification.getSafetySpotNotificationInfos();
            for (int i = 0; i < safetySpotInfos.size(); i++) {
                safetyCameraMapMarker = new MapMarker();
                SafetySpotNotificationInfo safetySpotInfo = safetySpotInfos.get(i);
                safetyCameraLocation = safetySpotInfo.getSafetySpot().getCoordinate();

                /* Adding MapMarker to indicate selected safety camera */
                safetyCameraMapMarker.setCoordinate(safetyCameraLocation);
                Image icon = new Image();
                icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(m_activity, R.drawable.ic_pin, 128, 128));
                safetyCameraMapMarker.setIcon(icon);
                safetyCameraMapMarker.setAnchorPoint(getMapMarkerAnchorPoint(safetyCameraMapMarker));
                m_map.addMapObject(safetyCameraMapMarker);

                distanceToSafetyCamera = safetySpotInfo.getDistance();
                safetyCameraSpeedLimit = safetySpotInfo.getSafetySpot().getSpeedLimit1();
                Log.d("Test", "safetyCameraSpeedLimit = " + safetyCameraSpeedLimit);
                Log.d("Test", "safetyCameraSpeedLimit * 3.6) % 5 = " + (safetyCameraSpeedLimit * 3.6) % 5);

                if (safetyCameraSpeedLimit * 3.6 % 10 >= 8 || safetyCameraSpeedLimit * 3.6 % 10 <= 2) {
                    safetyCameraSpeedLimitKM = (int) ((Math.round((safetyCameraSpeedLimit * 3.6) / 10)) * 10);
                } else {
                    safetyCameraSpeedLimitKM = (int) (Math.round((safetyCameraSpeedLimit * 3.6)));
                }

                safetyCameraAhead = true;
            }
        }
    };
    private GeoCoordinate lastKnownLocation;
    private OnPositionChangedListener positionListener = new OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
            currentGeoPosition = geoPosition;
            if (lightSensorValue < 50) {
                m_activity.setTheme(R.style.MSDKUIDarkTheme_WhiteAccent);
                mapSchemeChanger.darkenMap();
            } else {
                m_activity.setTheme(R.style.MSDKUIDarkTheme);
                mapSchemeChanger.lightenMap();
            }
            if (!isRouteOverView) {
                if (!isDragged) {
                    m_map.setCenter(geoPosition.getCoordinate(), Map.Animation.NONE);
                }
            }
            if (safetyCameraAhead) {
                if (lastKnownLocation.distanceTo(safetyCameraLocation) < geoPosition.getCoordinate().distanceTo(safetyCameraLocation)) {
                    safetyCameraAhead = false;
                    m_map.removeMapObject(safetyCameraMapMarker);
                    safetyCamImageView.setVisibility(View.INVISIBLE);
                    safetyCamTextView.setVisibility(View.INVISIBLE);
                    safetyCamSpeedTextView.setVisibility(View.INVISIBLE);
                } else {
                    safetyCamImageView.setVisibility(View.VISIBLE);
                    safetyCamTextView.setVisibility(View.VISIBLE);
                    safetyCamSpeedTextView.setVisibility(View.VISIBLE);
                    safetyCamTextView.setText((int) geoPosition.getCoordinate().distanceTo(safetyCameraLocation) + "m");
                    safetyCamSpeedTextView.setText(safetyCameraSpeedLimitKM + "km/h");
                }
            }
            if (lastKnownLocation != null) {
                if (lastKnownLocation.distanceTo(geoPosition.getCoordinate()) > 0) {
                    lastKnownLocation = geoPosition.getCoordinate();
                }
            } else {
                lastKnownLocation = geoPosition.getCoordinate();
            }
        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
            Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "locationMethod: " + locationMethod, Snackbar.LENGTH_SHORT);
            Log.d("Test", "locationMethod: " + locationMethod + " locationStatus: " + locationStatus);
        }
    };
    private MapGesture.OnGestureListener customOnGestureListener = new MapGesture.OnGestureListener() {

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
            for (ViewObject viewObject : list) {
                if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                    MapMarker mapMarkerOnMap = (MapMarker) viewObject;
                    String parkingTitle = mapMarkerOnMap.getTitle();
                    Snackbar goToParkSnackBar = Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), parkingTitle, 30000);
                    goToParkSnackBar.setAction("Go!", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userInputWaypoints.add((MapMarker) viewObject);
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.CAR));
                            m_naviControlButton.setVisibility(View.VISIBLE);
                            clearButton.setVisibility(View.VISIBLE);
                        }
                    });
                    goToParkSnackBar.show();
                }
            }
            return false;
        }

        @Override
        public boolean onTapEvent(PointF pointF) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(PointF pointF) {
            touchToAddWaypoint(pointF);
            switchUiControls(View.VISIBLE);
            m_map.setCenter(pointF, Map.Animation.LINEAR, m_map.getZoomLevel(), m_map.getOrientation(), m_map.getTilt());
            return true;
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
            isDragged = true;
            return false;
        }

        @Override
        public boolean onTiltEvent(float v) {
            isDragged = true;
            return false;
        }

        @Override
        public boolean onLongPressEvent(PointF pointF) {
            return false;
        }

        @Override
        public void onLongPressRelease() {

        }

        @Override
        public boolean onTwoFingerTapEvent(PointF pointF) {
            return false;
        }
    };
    private NavigationManager.NavigationManagerEventListener m_navigationManagerEventListener = new NavigationManager.NavigationManagerEventListener() {
        @Override
        public void onRunningStateChanged() {
        }

        @Override
        public void onNavigationModeChanged() {
        }

        @Override
        public void onEnded(NavigationManager.NavigationMode navigationMode) {
            Snackbar snackbarForSearchParking = Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), navigationMode + " was ended", Snackbar.LENGTH_LONG);
            snackbarForSearchParking.setAction("Find Parking!", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    placeSearchResultIcons.clear();
                    resetMap();
                    /* Places search request */
                    SearchRequest request = new SearchRequest("parking-facility");
                    request.setSearchArea(m_positioningManager.getPosition().getCoordinate(), 2000);
                    request.setCollectionSize(10);
                    ErrorCode error = request.execute(new ResultListener<DiscoveryResultPage>() {
                        @Override
                        public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode) {
                            List<PlaceLink> discoveryResultPlaceLink = discoveryResultPage.getPlaceLinks();
                            for (PlaceLink placeLink : discoveryResultPlaceLink) {
                                Log.d("Test", placeLink.getTitle());
//                                placeResultGeoBoundingBox.merge(placeLink.getBoundingBox());
                                MapMarker placeSearchResultMapMarker = new MapMarker(placeLink.getPosition());
                                placeSearchResultMapMarker.setTitle(placeLink.getTitle());
                                placeSearchResultMapMarker.setDescription(placeLink.getId());
                                Image icon = new Image();
                                icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(m_activity, R.drawable.ic_parking, 64, 64));
                                placeSearchResultMapMarker.setIcon(icon);
//                                placeSearchResultMapMarker.setAnchorPoint(getMapMarkerAnchorPoint(placeSearchResultMapMarker));
//                                m_map.addMapObject(placeSearchResultMapMarker);
//
                                placeSearchResultIcons.add(placeSearchResultMapMarker);
                            }
                            for (MapMarker mapMarker : placeSearchResultIcons) {
                                m_map.addMapObject(mapMarker);
                            }
                            clearButton.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
            snackbarForSearchParking.setDuration(30000);
            snackbarForSearchParking.show();
            stopForegroundService();
        }

        @Override
        public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
            Log.d("Test", "mapUpdateMode is: " + mapUpdateMode);
        }

        @Override
        public void onRouteUpdated(Route route) {
            resetMapRoute(route);
        }

        @Override
        public void onCountryInfo(String s, String s1) {
        }
    };
    private NavigationManager.PositionListener m_positionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(GeoPosition geoPosition) {
            mapLocalModel.setAnchor(geoPosition.getCoordinate());
            mapLocalModel.setYaw((float) geoPosition.getHeading());

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
            View mainLinearLayout = m_activity.findViewById(R.id.main_linear_layout);
            junctionViewImageView.requestLayout();
            signpostImageView.requestLayout();
            int jvViewWidth = (int) (mainLinearLayout.getWidth() / 2.5);
            int jvViewHeight;
            switch (aspectRatio) {
                case AR_16x9:
                    jvViewHeight = jvViewWidth / 16 * 9;
                    break;
                case AR_5x3:
                    jvViewHeight = jvViewWidth / 5 * 3;
                    break;
                case AR_4x3:
                    jvViewHeight = jvViewWidth / 4 * 3;
                    break;
                default:
                    jvViewHeight = jvViewWidth * 2;
            }
            junctionViewImageView.getLayoutParams().height = jvViewHeight;
            junctionViewImageView.getLayoutParams().width = jvViewWidth;
            signpostImageView.getLayoutParams().height = jvViewHeight;
            signpostImageView.getLayoutParams().width = jvViewWidth;
            Bitmap junctionBitmap = junction.getBitmap((int) junction.getWidth(), (int) junction.getHeight());
            Bitmap signpostBitMap = signpost.getBitmap((int) signpost.getWidth(), (int) signpost.getHeight());
            junctionViewImageView.setImageBitmap(junctionBitmap);
            signpostImageView.setImageBitmap(signpostBitMap);
            junctionViewImageView.setVisibility(View.VISIBLE);
            signpostImageView.setVisibility(View.VISIBLE);
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
    }

    private static PointF getMapMarkerAnchorPoint(MapMarker mapMarker) {
        int iconHeight = (int) mapMarker.getIcon().getHeight();
        int iconWidth = (int) mapMarker.getIcon().getWidth();
        return new PointF((float) (iconWidth / 2), (float) iconHeight);
    }

//    private void initGuidanceEstimatedArrivalView(NavigationManager navigationManager) {
//        guidanceEstimatedArrivalView = m_activity.findViewById(R.id.guidance_estimated_arrival_view);
//        guidanceEstimatedArrivalViewPresenter = new GuidanceEstimatedArrivalViewPresenter(navigationManager);
//        guidanceEstimatedArrivalViewPresenter.addListener(new GuidanceEstimatedArrivalViewListener() {
//            @Override
//            public void onDataChanged(GuidanceEstimatedArrivalViewData guidanceEstimatedArrivalViewData) {
//                Log.d("Test", "onDataChanged");
//                guidanceEstimatedArrivalView.setEstimatedArrivalData(guidanceEstimatedArrivalViewData);
//                if (guidanceEstimatedArrivalViewData != null) {
//                    Log.d("Test", "guidanceEstimatedArrivalViewData " + guidanceEstimatedArrivalViewData.getEta());
//                }
//            }
//        });
//    }

    private void initGuidanceStreetLabelView(Context context, NavigationManager navigationManager, Route route) {
        guidanceStreetLabelView = m_activity.findViewById(R.id.guidance_street_label_view);
        guidanceStreetLabelPresenter = new GuidanceStreetLabelPresenter(context, navigationManager, route);
        guidanceStreetLabelPresenter.addListener(new GuidanceStreetLabelListener() {
            @Override
            public void onDataChanged(GuidanceStreetLabelData guidanceStreetLabelData) {
                Log.d("Test", guidanceStreetLabelData.getCurrentStreetName());
                guidanceStreetLabelView.setCurrentStreetData(guidanceStreetLabelData);
            }
        });
    }

//    private void initGuidanceSpeedView(NavigationManager navigationManager, PositioningManager positioningManager) {
//        guidanceSpeedLimitView = m_activity.findViewById(R.id.guidance_speed_limit_view);
//        guidanceSpeedView = m_activity.findViewById(R.id.guidance_speed_view);
//        guidanceSpeedPresenter = new GuidanceSpeedPresenter(navigationManager, positioningManager);
//        guidanceSpeedPresenter.addListener(new GuidanceSpeedListener() {
//            @Override
//            public void onDataChanged(@Nullable GuidanceSpeedData guidanceSpeedData) {
//                guidanceSpeedLimitView.setCurrentSpeedData(guidanceSpeedData);
//                guidanceSpeedView.setCurrentSpeedData(guidanceSpeedData);
//            }
//        });
//    }

    private void initGuidanceManeuverView(Context context, NavigationManager navigationManager, Route route) {
        guidanceManeuverView = m_activity.findViewById(R.id.guidanceManeuverView);
        guidanceManeuverPresenter = new GuidanceManeuverPresenter(context, navigationManager, route);
        guidanceManeuverPresenter.addListener(new GuidanceManeuverListener() {
            @Override
            public void onDataChanged(@Nullable GuidanceManeuverData guidanceManeuverData) {
                guidanceManeuverView.setManeuverData(guidanceManeuverData);
                if (guidanceManeuverData != null) {
                }
            }

            @Override
            public void onDestinationReached() {
            }
        });
    }

    private void initGuidanceNextManeuverView(Context context, NavigationManager navigationManager, Route route) {
        guidanceNextManeuverView = m_activity.findViewById(R.id.guidance_next_maneuver_view);
        guidanceNextManeuverPresenter = new GuidanceNextManeuverPresenter(context, navigationManager, route);
        guidanceNextManeuverPresenter.addListener(new GuidanceNextManeuverListener() {
            @Override
            public void onDataChanged(GuidanceNextManeuverData guidanceNextManeuverData) {
                guidanceNextManeuverView.setNextManeuverData(guidanceNextManeuverData);
            }
        });
    }

    private void switchUiControls(int visibility) {

        northUpButton.setVisibility(visibility);
        carRouteButton.setVisibility(visibility);
        truckRouteButton.setVisibility(visibility);
        scooterRouteButton.setVisibility(visibility);
        bikeRouteButton.setVisibility(visibility);
        pedsRouteButton.setVisibility(visibility);
    }

    private void switchGuidanceUiViews(int visibility) {
        m_activity.findViewById(R.id.guidanceManeuverView).setVisibility(visibility);
        m_activity.findViewById(R.id.guidance_next_maneuver_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_info_linear_layout).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_speed_limit_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_speed_view).setVisibility(visibility);
        m_activity.findViewById(R.id.guidance_street_label_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_estimated_arrival_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.speed_limit_linear_layout).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_speed_limit_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.speed_limit_text_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_info).setVisibility(visibility);

    }

    private void switchGuidanceUiPresenters(Boolean switchOn) {
        if (switchOn) {
            guidanceManeuverPresenter.resume();
//            guidanceEstimatedArrivalViewPresenter.resume();
//            guidanceSpeedPresenter.resume();
            guidanceStreetLabelPresenter.resume();
            guidanceNextManeuverPresenter.resume();
        } else {
            if (guidanceManeuverPresenter != null) {
                guidanceManeuverPresenter.pause();
            }
            if (guidanceEstimatedArrivalViewPresenter != null) {
                guidanceEstimatedArrivalViewPresenter.pause();
            }
            if (guidanceSpeedPresenter != null) {
                guidanceSpeedPresenter.pause();
            }
            if (guidanceStreetLabelPresenter != null) {
                guidanceStreetLabelPresenter.pause();
            }
            if (guidanceNextManeuverPresenter != null) {
                guidanceNextManeuverPresenter.pause();
            }
        }
    }

    private void resetMapRoute(Route route) {
        safetyCameraAhead = false;
        if (safetyCameraMapMarker != null) {
            m_map.removeMapObject(safetyCameraMapMarker);
        }
        if (mapRoute != null) {
            m_map.removeMapObject(mapRoute);
        }
        mapRoute = new MapRoute(route);
        mapRoute.setColor(Color.argb(255, 243, 174, 255)); //F3AEFF
        mapRoute.setOutlineColor(Color.argb(255, 78, 0, 143)); //4E008F
        mapRoute.setTraveledColor(Color.DKGRAY);
        mapRoute.setUpcomingColor(Color.LTGRAY);
        mapRoute.setTrafficEnabled(true);
        m_map.addMapObject(mapRoute);
    }

    private void touchToAddWaypoint(PointF p) {
        isDragged = true;
        GeoCoordinate touchPointGeoCoordinate = m_map.pixelToGeo(p);
        MapMarker mapMarker = new MapMarker(touchPointGeoCoordinate);
        mapMarker.setDraggable(true);
        userInputWaypoints.add(mapMarker);
        mapMarker.setAnchorPoint(getMapMarkerAnchorPoint(mapMarker));
        m_map.addMapObject(mapMarker);
        carRouteButton.setVisibility(View.VISIBLE);
        truckRouteButton.setVisibility(View.VISIBLE);
        scooterRouteButton.setVisibility(View.VISIBLE);
        bikeRouteButton.setVisibility(View.VISIBLE);
        pedsRouteButton.setVisibility(View.VISIBLE);
        m_naviControlButton.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);
    }

    private void initSupportMapFragment() {
        /* Locate the mapFragment UI element */
        supportMapFragment = getMapFragment();
        supportMapFragment.setCopyrightLogoPosition(CopyrightLogoPosition.BOTTOM_CENTER);
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
                            supportMapFragment.getMapGesture().addOnGestureListener(customOnGestureListener, 0, false);

                            m_navigationManager = NavigationManager.getInstance();
                            coreRouter = new CoreRouter();
                            m_map = supportMapFragment.getMap();
                            m_map.setCenter(new GeoCoordinate(25.038137, 121.513936), Map.Animation.NONE);
                            isDragged = false;

                            /* PositioningManager init */
                            m_positioningManager = PositioningManager.getInstance();
                            /* Advanced positioning */
                            /* Disable to run on emulator */
//                            LocationDataSourceHERE m_hereDataSource;
//                            m_hereDataSource = LocationDataSourceHERE.getInstance();
//                            m_positioningManager.setDataSource(m_hereDataSource);
                            m_positioningManager.addListener(new WeakReference<>(positionListener));

                            /* GPS logging function */
//                            EnumSet<PositioningManager.LogType> logTypes = EnumSet.of(
//                                    PositioningManager.LogType.RAW,
//                                    PositioningManager.LogType.DATA_SOURCE
//                            );
//                            m_positioningManager.setLogType(logTypes);

                            /* Start tracking position */
                            if (m_positioningManager != null) {
                                m_positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                            }

                            shiftMapCenter(m_map, 0.5f, 0.6f);
                            mapSchemeChanger = new MapSchemeChanger(m_map, m_navigationManager);

                            m_map.setMapScheme(Map.Scheme.CARNAV_DAY);
                            m_map.setMapDisplayLanguage(TRADITIONAL_CHINESE);
                            m_map.setSafetySpotsVisible(true);
                            m_map.setExtrudedBuildingsVisible(false);
                            m_map.setLandmarksVisible(true);
                            m_map.setExtendedZoomLevelsEnabled(true);

//                            speedLimitLinearLayout = m_activity.findViewById(R.id.speed_limit_linear_layout);
//                            speedLimitLinearLayoutHeight = speedLimitLinearLayout.getLayoutParams().height;
                            switchGuidanceUiViews(View.GONE);
                            /* Listeners of map buttons */
                            northUpButton = m_activity.findViewById(R.id.north_up);
                            northUpButton.setOnClickListener(v -> {
                                m_map.setOrientation(0);
                                m_map.setTilt(0);
                                m_map.setZoomLevel(16);
                                shiftMapCenter(m_map, 0.5f, 0.6f);
                                if (!isRouteOverView) {
                                    m_map.setCenter(m_positioningManager.getPosition().getCoordinate(), Map.Animation.NONE);
                                } else {
                                    m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, Map.MOVE_PRESERVE_ORIENTATION);
                                }
                            });
                            zoomInButton = m_activity.findViewById(R.id.zoom_in);
                            zoomInButton.setOnClickListener(v -> {
                                double zoomLevel = m_map.getZoomLevel();
                                m_map.setZoomLevel(zoomLevel + 1);
                            });
                            zoomOutButton = m_activity.findViewById(R.id.zoom_out);
                            zoomOutButton.setOnClickListener(v -> {
                                double zoomLevel = m_map.getZoomLevel();
                                m_map.setZoomLevel(zoomLevel - 1);
                            });
                            carRouteButton = m_activity.findViewById(R.id.car_route);
                            truckRouteButton = m_activity.findViewById(R.id.truck_route);
                            scooterRouteButton = m_activity.findViewById(R.id.scooter_route);
                            bikeRouteButton = m_activity.findViewById(R.id.bike_route);
                            pedsRouteButton = m_activity.findViewById(R.id.peds_route);
                            carRouteButton.setOnClickListener(vCarRouteButton -> {
                                calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.CAR));
                            });
                            truckRouteButton.setOnClickListener(vTruckRouteButton -> {
                                calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.TRUCK));
                            });
                            scooterRouteButton.setOnClickListener(vScooterRouteButton -> {
                                calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.SCOOTER));
                            });
                            bikeRouteButton.setOnClickListener(vBikeRouteButton -> {
                                calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.BICYCLE));
                            });
                            pedsRouteButton.setOnClickListener(vPedsRouteButton -> {
                                calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.PEDESTRIAN));
                            });
                            trafficButton = m_activity.findViewById(R.id.traffic_button);
                            trafficButton.setTextColor(Color.parseColor("#FF000000"));
                            trafficButton.setOnClickListener(v -> {
                                if (!m_map.isTrafficInfoVisible()) {
                                    trafficEnabled = true;
                                    m_map.setTrafficInfoVisible(true);
                                    trafficButton.setTextColor(Color.parseColor("#FFFF0000"));
                                } else {
                                    trafficEnabled = false;
                                    m_map.setTrafficInfoVisible(false);
                                    trafficButton.setTextColor(Color.parseColor("#FF000000"));
                                }
                            });
                            m_naviControlButton = m_activity.findViewById(R.id.startGuidance);
                            m_naviControlButton.setText("Create Route");
                            m_naviControlButton.setOnClickListener(v -> {
                                if (m_route != null) {
                                    m_navigationManager.stop();
                                    m_map.removeMapObject(mapLocalModel);
                                    shiftMapCenter(m_map, 0.5f, 0.6f);
                                    m_map.setTilt(0);
                                    switchGuidanceUiPresenters(false);
                                    startNavigation(m_route, true);
                                } else {
                                    calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.CAR));
                                }
                            });
                            clearButton = m_activity.findViewById(R.id.clear);
                            clearButton.setOnClickListener(v -> resetMap());

                            safetyCamImageView = m_activity.findViewById(R.id.safety_cam_image_view);
                            safetyCamTextView = m_activity.findViewById(R.id.safety_cam_text_view);
                            safetyCamSpeedTextView = m_activity.findViewById(R.id.safety_cam_speed_text_view);



                            /* Show position indicator */
                            positionIndicator = m_map.getPositionIndicator();
                            positionIndicator.setVisible(true);
                            positionIndicator.setAccuracyIndicatorVisible(true);

                            /* Download voice */
                            voiceActivation = new VoiceActivation(m_activity);
                            voiceActivation.setContext(m_activity);
                            String desiredVoiceLanguageCode = "CHT";
                            voiceActivation.setDesiredLangCode(desiredVoiceLanguageCode);
                            voiceActivation.downloadCatalogAndSkin();
                        } else {
                            Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "ERROR: Cannot initialize Map with error " + error, Snackbar.LENGTH_LONG).show();
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
        LocalModelLoader localModelLoader = new LocalModelLoader(m_activity);
        LocalMesh localMesh = new LocalMesh();
        localMesh.setVertices(localModelLoader.getObjVertices());
        localMesh.setVertexIndices(localModelLoader.getObjIndices());
        localMesh.setTextureCoordinates(localModelLoader.getObjTexCoords());

        mapLocalModel.setMesh(localMesh);
        Image image = null;
        try {
            image = new Image();
            image.setImageResource(R.drawable.grad);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapLocalModel.setTexture(image); //an Image object
        mapLocalModel.setScale(6.0f);
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

    void shiftMapCenter(Map map, float widthOffset, float heightOffset) {
        map.setTransformCenter(new PointF(
                (map.getWidth() * widthOffset),
                (map.getHeight() * heightOffset)
        ));
    }

    private void intoNavigationMode() {
        initJunctionView();

        zoomInButton.setVisibility(View.GONE);
        zoomOutButton.setVisibility(View.GONE);

        switchUiControls(View.GONE);
        initGuidanceManeuverView(m_activity, m_navigationManager, m_route);
        initGuidanceNextManeuverView(m_activity, m_navigationManager, m_route);
//        initGuidanceEstimatedArrivalView(m_navigationManager);
        initGuidanceStreetLabelView(m_activity, m_navigationManager, m_route);
//        initGuidanceSpeedView(m_navigationManager, m_positioningManager);
        initGuidanceManeuverView(m_activity, m_navigationManager, m_route);
        initGuidanceNextManeuverView(m_activity, m_navigationManager, m_route);
        switchGuidanceUiViews(View.VISIBLE);
        positionIndicator.setVisible(false);
        positionIndicator.setAccuracyIndicatorVisible(false);
        switchGuidanceUiPresenters(true);
        m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
        isRoadView = true;
        EnumSet<NavigationManager.NaturalGuidanceMode> naturalGuidanceModes = EnumSet.of(
                NavigationManager.NaturalGuidanceMode.JUNCTION,
                NavigationManager.NaturalGuidanceMode.STOP_SIGN,
                NavigationManager.NaturalGuidanceMode.TRAFFIC_LIGHT
        );
        m_navigationManager.setTrafficAvoidanceMode(NavigationManager.TrafficAvoidanceMode.DYNAMIC);
        m_navigationManager.setNaturalGuidanceMode(naturalGuidanceModes);
        shiftMapCenter(m_map, 0.5f, 0.8f);
        //hudMapScheme(m_map);
        m_map.setTilt(60);
        m_navigationManager.startNavigation(m_route);
        m_positioningManager.setMapMatchingEnabled(true);

        /* Voice Guidance init */
        VoiceCatalog voiceCatalog = voiceActivation.getVoiceCatalog();
        VoiceGuidanceOptions voiceGuidanceOptions = m_navigationManager.getVoiceGuidanceOptions();
        voiceGuidanceOptions.setVoiceSkin(voiceCatalog.getLocalVoiceSkin(voiceActivation.getDesiredVoiceId()));
        EnumSet<NavigationManager.AudioEvent> audioEventEnumSet = EnumSet.of(
                NavigationManager.AudioEvent.MANEUVER,
                NavigationManager.AudioEvent.ROUTE,
                NavigationManager.AudioEvent.SAFETY_SPOT,
                NavigationManager.AudioEvent.SPEED_LIMIT,
                NavigationManager.AudioEvent.GPS
        );
        m_navigationManager.setEnabledAudioEvents(audioEventEnumSet);

        supportMapFragment.setOnTouchListener(mapOnTouchListener);
        supportMapFragment.getMapGesture().removeOnGestureListener(customOnGestureListener);
        mapLocalModel = createPosition3dObj();
    }

    private void startNavigation(Route route, boolean zoomToRoute) {

        resetMapRoute(route);
        for (MapMarker m : placeSearchResultIcons) {
            m_map.removeMapObject(m);
        }

        shiftMapCenter(m_map, 0.5f, 0.6f);
        m_map.setTilt(0);
        m_navigationManager.setMap(m_map);
        if (zoomToRoute) {
            m_map.zoomTo(mapRouteBBox, Map.Animation.NONE, 0f);
        }
        isDragged = false;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_activity);
        alertDialogBuilder.setTitle("Navigation");
        alertDialogBuilder.setMessage("Choose Mode");
        alertDialogBuilder.setNegativeButton("Navigation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                m_naviControlButton.setText("Stop Navi");
                intoNavigationMode();
                isRouteOverView = false;
                NavigationManager.Error error = m_navigationManager.startNavigation(m_route);
                Log.e("Error: ", error.toString());
                startForegroundService();
            }
        });
        alertDialogBuilder.setPositiveButton("Simulation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                m_naviControlButton.setText("Stop Navi");
                intoNavigationMode();
                isRouteOverView = false;
                NavigationManager.Error error = m_navigationManager.simulate(m_route, simulationSpeedMs);
                startForegroundService();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(m_activity.getResources().getColor(R.color.green));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(m_activity.getResources().getColor(R.color.red));
        addNavigationListeners();
    }

    private void resetMap() {
        switchGuidanceUiViews(View.GONE);
        m_activity.findViewById(R.id.junctionImageView).setVisibility(View.INVISIBLE);
        m_activity.findViewById(R.id.signpostImageView).setVisibility(View.INVISIBLE);
        m_naviControlButton.setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
        if (!placeSearchResultIcons.isEmpty()) {
            for (MapMarker m : placeSearchResultIcons) {
                m_map.removeMapObject(m);
            }
        }
        placeSearchResultIcons.clear();
        isRouteOverView = false;
        if (coreRouter != null) {
            if (coreRouter.isBusy()) {
                coreRouter.cancel();
            }
        }
        supportMapFragment.setOnTouchListener(null);
        positionIndicator.setVisible(true);
        positionIndicator.setAccuracyIndicatorVisible(true);
        if (m_navigationManager != null) {
            if (m_navigationManager.getRunningState() == NavigationManager.NavigationState.RUNNING) {
                m_navigationManager.stop();
            }
        }
        m_naviControlButton.setText("Create Route");
        m_route = null;
        switchGuidanceUiPresenters(false);
        if (m_map.isTrafficInfoVisible()) {
            m_map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_DAY);
        } else {
            m_map.setMapScheme(Map.Scheme.CARNAV_DAY);
        }

        shiftMapCenter(m_map, 0.5f, 0.6f);

        m_map.removeMapObject(mapLocalModel);
        m_map.setTilt(0);

        if (mapRoute != null) {
            m_map.removeMapObject(mapRoute);
        }
        if (!userInputWaypoints.isEmpty()) {
            for (MapMarker mkr : userInputWaypoints) {
                m_map.removeMapObject(mkr);
            }
        }
        if (!wayPointIcons.isEmpty()) {
            for (MapMarker mkr : wayPointIcons) {
                m_map.removeMapObject(mkr);
            }
        }
        wayPointIcons.clear();
        userInputWaypoints.clear();
        waypointList.clear();
        isDragged = false;

        northUpButton.callOnClick();
        supportMapFragment.getMapGesture().addOnGestureListener(customOnGestureListener, 0, false);
        switchUiControls(View.GONE);
        zoomInButton.setVisibility(View.VISIBLE);
        zoomOutButton.setVisibility(View.VISIBLE);
        northUpButton.setVisibility(View.VISIBLE);
    }

    private RouteOptions prepareRouteOptions(RouteOptions.TransportMode transportMode) {
        RouteOptions routeOptions = new RouteOptions();
        switch (transportMode) {
            case CAR:
                routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
                routeOptions.setHighwaysAllowed(true);
                if (lightSensorValue < 50) {
                    if (!trafficEnabled) {
                        m_map.setMapScheme(Map.Scheme.CARNAV_NIGHT);
                    } else {
                        m_map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_NIGHT);
                    }
                } else {
                    if (!trafficEnabled) {
                        m_map.setMapScheme(Map.Scheme.CARNAV_DAY);
                    } else {
                        m_map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_DAY);
                    }
                }
                break;
            case TRUCK:
                routeOptions.setTransportMode(RouteOptions.TransportMode.TRUCK);
                routeOptions.setHighwaysAllowed(true);
                if (lightSensorValue < 50) {
                    m_map.setMapScheme(Map.Scheme.TRUCK_NIGHT);
                } else {
                    m_map.setMapScheme(Map.Scheme.TRUCK_DAY);
                }
                break;
            case SCOOTER:
                routeOptions.setTransportMode(RouteOptions.TransportMode.SCOOTER);
                if (lightSensorValue < 50) {
                    if (!trafficEnabled) {
                        m_map.setMapScheme(Map.Scheme.CARNAV_NIGHT);
                    } else {
                        m_map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_NIGHT);
                    }
                } else {
                    if (!trafficEnabled) {
                        m_map.setMapScheme(Map.Scheme.CARNAV_DAY);
                    } else {
                        m_map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_DAY);
                    }
                }
                routeOptions.setHighwaysAllowed(false);
                break;
            case BICYCLE:
                routeOptions.setTransportMode(RouteOptions.TransportMode.BICYCLE);
                m_map.setMapScheme(Map.Scheme.TERRAIN_DAY);
                routeOptions.setHighwaysAllowed(false);
                break;
            case PEDESTRIAN:
                routeOptions.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
                if (lightSensorValue < 50) {
                    m_map.setMapScheme(Map.Scheme.PEDESTRIAN_NIGHT);
                } else {
                    m_map.setMapScheme(Map.Scheme.PEDESTRIAN_DAY);
                }
                routeOptions.setHighwaysAllowed(false);
                break;
        }
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routeOptions.setRouteCount(1);
        return routeOptions;
    }

    private void retryRouting(Context context, RoutingError routingError, RouteOptions m_routeOptions) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Route Calculation Failed:\n" + routingError.name());
        alertDialogBuilder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                calculateRoute(m_routeOptions);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(m_activity.getResources().getColor(R.color.green));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(m_activity.getResources().getColor(R.color.red));
        alertDialog.show();
    }


    private void calculateRoute(RouteOptions routeOptions) {
        HereRouter hereRouter = new HereRouter(m_activity, routeOptions);
        hereRouter.setContext(m_activity);
        if (wayPointIcons.size() == 0) {
            for (int i = 0; i < userInputWaypoints.size(); i++) {
                MapMarker mapMarker = userInputWaypoints.get(i);
                waypointList.add(mapMarker.getCoordinate());
                m_map.removeMapObject(mapMarker);
            }
            if (mapRoute != null) {
                m_map.removeMapObject(mapRoute);
            }
            wayPointIcons = hereRouter.getOutputWaypointIcons();
        }
        hereRouter.setWaypoints(waypointList);

        hereRouter.createRouteForNavi();
        for (MapMarker m : wayPointIcons) {
            m.setAnchorPoint(getMapMarkerAnchorPoint(m));
            m_map.addMapObject(m);
        }
        Log.d("Test", "wayPointIcons: " + wayPointIcons.size());


        if (m_map.isTrafficInfoVisible()) {
            DynamicPenalty dynamicPenalty = new DynamicPenalty();
            dynamicPenalty.setTrafficPenaltyMode(Route.TrafficPenaltyMode.OPTIMAL);
            coreRouter.setDynamicPenalty(dynamicPenalty);
        }

        progressBar = m_activity.findViewById(R.id.progressBar);
        calculatingTextView = m_activity.findViewById(R.id.calculatingTextView);

        coreRouter.calculateRoute(hereRouter.getRoutePlan(), new Router.Listener<List<RouteResult>, RoutingError>() {
            @Override
            public void onProgress(int i) {
                if (i < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    calculatingTextView.setVisibility(View.VISIBLE);
                    progressBar.setProgress(i);
                } else {
                    calculatingTextView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCalculateRouteFinished(List<RouteResult> routeResults, RoutingError routingError) {
                if (routingError == RoutingError.NONE) {
                    if (routeResults.get(0).getRoute() != null) {
                        isRouteOverView = true;
                        m_route = routeResults.get(0).getRoute();

                        resetMapRoute(m_route);
                        mapRouteBBox = m_route.getBoundingBox();
                        GeoBoundingBoxDimensionCalculator geoBoundingBoxDimensionCalculator = new GeoBoundingBoxDimensionCalculator(mapRouteBBox);

                        mapRouteBBox.expand((float) (geoBoundingBoxDimensionCalculator.getBBoxHeight() * 0.8), (float) (geoBoundingBoxDimensionCalculator.getBBoxWidth() * 0.6));
                        m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, Map.MOVE_PRESERVE_ORIENTATION);
                        m_naviControlButton.setText("Start Navi");
                        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Length: " + m_route.getLength() + "m", Snackbar.LENGTH_LONG).show();
                        supportMapFragment.getMapGesture().removeOnGestureListener(customOnGestureListener);
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_activity);
                        alertDialogBuilder.setTitle("Can't find a route.");
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } else {
                    calculatingTextView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    if (mapRoute != null) {
                        m_map.removeMapObject(mapRoute);
                    }
                    retryRouting(m_activity, routingError, routeOptions);
                }
            }
        });
    }

    private void addNavigationListeners() {
        m_activity.findViewById(R.id.mapFragmentView).getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        m_navigationManager.addNavigationManagerEventListener(new WeakReference<>(m_navigationManagerEventListener));
        m_navigationManager.addLaneInformationListener(new WeakReference<>(m_LaneInformationListener));
        m_navigationManager.addNewInstructionEventListener(new WeakReference<>(m_newInstructionEventListener));
        m_navigationManager.addSafetySpotListener(new WeakReference<>(safetySpotListener));
        m_navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        m_navigationManager.addRealisticViewAspectRatio(NavigationManager.AspectRatio.AR_16x9);
        m_navigationManager.addRealisticViewListener(new WeakReference<>(m_realisticViewListener));
        m_navigationManager.addPositionListener(new WeakReference<>(m_positionListener));
        m_navigationManager.addRerouteListener(new WeakReference<>(new NavigationManager.RerouteListener() {
            @Override
            public void onRerouteBegin() {
                super.onRerouteBegin();
            }

            @Override
            public void onRerouteEnd(RouteResult routeResult, RoutingError routingError) {
                super.onRerouteEnd(routeResult, routingError);
                resetMapRoute(routeResult.getRoute());
            }
        }));
        m_navigationManager.addTrafficRerouteListener(new WeakReference<>(new NavigationManager.TrafficRerouteListener() {
            @Override
            public void onTrafficRerouted(RouteResult routeResult) {
                super.onTrafficRerouted(routeResult);
                resetMapRoute(routeResult.getRoute());
            }

            @Override
            public void onTrafficRerouteFailed(TrafficNotification trafficNotification) {
                super.onTrafficRerouteFailed(trafficNotification);
            }

            @Override
            public void onTrafficRerouteBegin(TrafficNotification trafficNotification) {
                super.onTrafficRerouteBegin(trafficNotification);
            }

            @Override
            public void onTrafficRerouteState(TrafficEnabledRoutingState trafficEnabledRoutingState) {
                super.onTrafficRerouteState(trafficEnabledRoutingState);
            }
        }));

    }

    void onDestroy() {
        /* Stop the navigation when app is destroyed */
        if (m_navigationManager != null) {
            stopForegroundService();
            m_navigationManager.stop();
        }
    }


}

package com.fancynavi.android.app;

import android.app.AlertDialog;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Rational;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.here.android.mpa.common.CopyrightLogoPosition;
import com.here.android.mpa.common.DataNotReadyException;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPolyline;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.MapSettings;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.PositioningManager.OnPositionChangedListener;
import com.here.android.mpa.common.RoadElement;
import com.here.android.mpa.common.TrafficSign;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.customlocation2.CLE2CorridorRequest;
import com.here.android.mpa.customlocation2.CLE2DataManager;
import com.here.android.mpa.customlocation2.CLE2Geometry;
import com.here.android.mpa.customlocation2.CLE2PointGeometry;
import com.here.android.mpa.customlocation2.CLE2ProximityRequest;
import com.here.android.mpa.customlocation2.CLE2Request;
import com.here.android.mpa.customlocation2.CLE2Result;
import com.here.android.mpa.guidance.LaneInformation;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.SafetySpotNotification;
import com.here.android.mpa.guidance.SafetySpotNotificationInfo;
import com.here.android.mpa.guidance.TrafficNotification;
import com.here.android.mpa.guidance.TrafficNotificationInfo;
import com.here.android.mpa.guidance.TrafficWarner;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoiceGuidanceOptions;
import com.here.android.mpa.mapping.LocalMesh;
import com.here.android.mpa.mapping.Location;
import com.here.android.mpa.mapping.LocationInfo;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapCartoMarker;
import com.here.android.mpa.mapping.MapCircle;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapLocalModel;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapOverlay;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.MapState;
import com.here.android.mpa.mapping.OnMapRenderListener;
import com.here.android.mpa.mapping.SafetySpotObject;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.TrafficEvent;
import com.here.android.mpa.mapping.TrafficEventObject;
import com.here.android.mpa.mapping.customization.CustomizableScheme;
import com.here.android.mpa.mapping.customization.CustomizableVariables;
import com.here.android.mpa.mapping.customization.ZoomRange;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.DynamicPenalty;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeMode;
import com.here.android.mpa.search.ReverseGeocodeRequest;
import com.here.android.mpa.search.SearchRequest;
import com.here.android.mpa.tce.TollCostError;
import com.here.android.mpa.tce.TollCostRequest;
import com.here.android.mpa.tce.TollCostResult;
import com.here.msdkui.guidance.GuidanceEstimatedArrivalView;
import com.here.msdkui.guidance.GuidanceEstimatedArrivalViewData;
import com.here.msdkui.guidance.GuidanceEstimatedArrivalViewListener;
import com.here.msdkui.guidance.GuidanceEstimatedArrivalViewPresenter;
import com.here.msdkui.guidance.GuidanceManeuverData;
import com.here.msdkui.guidance.GuidanceManeuverListener;
import com.here.msdkui.guidance.GuidanceManeuverPresenter;
import com.here.msdkui.guidance.GuidanceManeuverView;
import com.here.msdkui.guidance.GuidanceNextManeuverData;
import com.here.msdkui.guidance.GuidanceNextManeuverListener;
import com.here.msdkui.guidance.GuidanceNextManeuverPresenter;
import com.here.msdkui.guidance.GuidanceNextManeuverView;
import com.here.msdkui.guidance.GuidanceSpeedData;
import com.here.msdkui.guidance.GuidanceSpeedLimitView;
import com.here.msdkui.guidance.GuidanceStreetLabelData;
import com.here.msdkui.guidance.GuidanceStreetLabelListener;
import com.here.msdkui.guidance.GuidanceStreetLabelPresenter;
import com.here.msdkui.guidance.GuidanceStreetLabelView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import static com.fancynavi.android.app.MainActivity.isMapRotating;
import static com.fancynavi.android.app.MainActivity.textToSpeech;
import static java.util.Locale.TRADITIONAL_CHINESE;


class MapFragmentView {
    static boolean isPipMode;
    static boolean isDragged;
    static MapOverlay laneInformationMapOverlay;
    static GeoPosition currentGeoPosition;
    static OnTouchListener emptyMapOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };
    static Button m_naviControlButton;
    static Button clearButton;
    static GeoBoundingBox mapRouteBBox;
    static ImageView junctionViewImageView;
    static ImageView signpostImageView;
    static boolean isRoadView = false;
    static boolean isSatMap = false;
    static boolean isRouteOverView;
    static boolean isNavigating;
    static boolean isSignShowing;
    static MapLocalModel currentPositionMapLocalModel;
    static Button northUpButton;
    static TextView trafficWarningTextView;
    static List<MapOverlay> distanceMarkerMapOverlayList = new ArrayList<>();
    static OnTouchListener mapOnTouchListenerForNavigation = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            intoRouteOverView();
            return false;
        }
    };
    static NavigationListeners navigationListeners;
    private Map m_map;
    private NavigationManager m_navigationManager;
    private PositioningManager m_positioningManager;
    private CLE2ProximityRequest cle2ProximityRequest;
    private SupportMapFragment supportMapFragment;
    private LinearLayout distanceMarkerLinearLayout;
    private ImageView distanceMarkerFreeIdImageView;
    private TextView distanceMarkerDistanceValue;
    private TrafficWarner trafficWarner;
    private boolean isPositionLogging = false;
    private MapSchemeChanger mapSchemeChanger;
    private LinearLayout laneDcmLinearLayout;
    private LinearLayout safetyCamLinearLayout;
    private LinearLayout laneInfoLinearLayoutOverlay;
    private double distanceToSafetyCamera;
    private HereRouter hereRouter;
    private List<TrafficSign> lastTrafficSignList = new ArrayList<>();
    private RoadElement lastRoadElement;
    private String signName;
    private ElectronicHorizonActivation electronicHorizonActivation;
    private Snackbar searchResultSnackbar;
    private String searchResultString;
    private MapMarker selectedFeatureMapMarker;
    private MapCircle positionAccuracyMapCircle;
    private ImageView gpsStatusImageView;
    private GeoPolyline croppedRoute;
    private List<GeoCoordinate> routeShapePointGeoCoordinateList;
    private ImageView signImageView1;
    private ImageView signImageView2;
    private ImageView signImageView3;
    private Switch gpsSwitch;
    private boolean safetyCameraAhead;
    private GeoCoordinate safetyCameraLocation;
    private double safetyCameraSpeedLimit;
    private int safetyCameraSpeedLimitKM;
    private ImageView safetyCamImageView;
    private TextView safetyCamTextView;
    private TextView safetyCamSpeedTextView;
    private MapMarker safetyCameraMapMarker;
    private int speedLimitLinearLayoutHeight;
    private View speedLimitLinearLayout;
    private AppCompatActivity m_activity;
    private NavigationManager.LaneInformationListener laneInformationListener = new NavigationManager.LaneInformationListener() {

        @Override
        public void onLaneInformation(List<LaneInformation> list, RoadElement roadElement) {
            super.onLaneInformation(list, roadElement);
            boolean isLaneDisplayed = false;
            if (laneInformationMapOverlay != null) {
                m_map.removeMapOverlay(laneInformationMapOverlay);
            }
            laneDcmLinearLayout = new LinearLayout(m_activity);
            laneDcmLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            laneDcmLinearLayout.setVisibility(View.VISIBLE);
            laneInfoLinearLayoutOverlay = new LinearLayout(m_activity);
            laneInfoLinearLayoutOverlay.setOrientation(LinearLayout.VERTICAL);
            laneInfoLinearLayoutOverlay.setVisibility(View.VISIBLE);
            if (list.size() > 0) {
                for (LaneInformation laneInformation : list) {
                    LaneInformation.RecommendationState recommendationState = laneInformation.getRecommendationState();
                    EnumSet<LaneInformation.Direction> directions = laneInformation.getDirections();
                    int laneDirectionCategory = 0;
                    ImageView laneDcmImageView = new ImageView(m_activity);
                    for (LaneInformation.Direction direction : directions) {
                        laneDirectionCategory += direction.value();
                    }

                    Drawable laneDcmIcon = LaneDirectionCategoryPresenter.getLaneDirectionCategoryPresenter(laneDirectionCategory, m_activity);
                    laneDcmImageView.setImageDrawable(laneDcmIcon);
                    isLaneDisplayed = LaneDirectionCategoryPresenter.isLaneDirectionCategoryShowing();
                    laneDcmImageView.setCropToPadding(false);

                    if (recommendationState == LaneInformation.RecommendationState.HIGHLY_RECOMMENDED) {
                        laneDcmImageView.setBackgroundColor(Color.argb(255, 0, 160, 0));
                    } else if (recommendationState == LaneInformation.RecommendationState.RECOMMENDED) {
                        laneDcmImageView.setBackgroundColor(Color.argb(64, 0, 128, 0));
                    } else {
                        laneDcmImageView.setBackgroundColor(Color.argb(32, 64, 64, 64));
                    }
                    int laneDcmImageViewPadding = (int) DpConverter.convertDpToPixel(4, m_activity);
                    laneDcmLinearLayout.addView(laneDcmImageView);
                    laneDcmImageView.setPadding(laneDcmImageViewPadding, laneDcmImageViewPadding, laneDcmImageViewPadding, laneDcmImageViewPadding);
                }
                laneInfoLinearLayoutOverlay.addView(laneDcmLinearLayout);
                ImageView downArrowImageView = new ImageView(m_activity);
                downArrowImageView.setImageResource(R.drawable.ic_arrow_point_to_down);
                laneInfoLinearLayoutOverlay.addView(downArrowImageView);

                laneInfoLinearLayoutOverlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        laneInfoLinearLayoutOverlay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        laneInformationMapOverlay.setAnchorPoint(getMapOverlayAnchorPoint(laneInfoLinearLayoutOverlay.getWidth(), laneInfoLinearLayoutOverlay.getHeight()));
                    }
                });
                laneInformationMapOverlay = new MapOverlay(laneInfoLinearLayoutOverlay, roadElement.getGeometry().get(roadElement.getGeometry().size() - 1));
                if (!isRouteOverView && isLaneDisplayed) {
                    m_map.addMapOverlay(laneInformationMapOverlay);
                }
            }
        }
    };
    private VoiceActivation voiceActivation;
    private Button zoomInButton;
    private Button zoomOutButton;
    private Button carRouteButton;
    private Button truckRouteButton;
    private Button scooterRouteButton;
    private Button bikeRouteButton;
    private Button pedsRouteButton;
    private Button trafficButton;
    private Button satMapButton;
    private Button minimizeMapButton;
    private Button searchButton;
    private EditText searchTextBar;
    private LinearLayout searchBarLinearLayout;
    private boolean trafficEnabled;
    private ProgressBar progressBar;
    private TextView calculatingTextView;
    private Route m_route;
    private MapRoute mapRoute;
    private boolean m_foregroundServiceStarted;
    private CoreRouter coreRouter;
    //HERE SDK UI KIT components
    private GuidanceManeuverView guidanceManeuverView;
    private GuidanceManeuverPresenter guidanceManeuverPresenter;
    private GuidanceEstimatedArrivalViewPresenter guidanceEstimatedArrivalViewPresenter;
    private GuidanceStreetLabelPresenter guidanceStreetLabelPresenter;
    private GuidanceEstimatedArrivalView guidanceEstimatedArrivalView;
    private GuidanceSpeedLimitView guidanceSpeedLimitView;
    private GuidanceStreetLabelView guidanceStreetLabelView;
    private GuidanceNextManeuverView guidanceNextManeuverView;
    private GuidanceNextManeuverPresenter guidanceNextManeuverPresenter;
    private TextView guidanceSpeedView;
    private TextView speedLabelTextView;
    private ArrayList<GeoCoordinate> waypointList = new ArrayList<>();
    private ArrayList<MapMarker> userInputWaypoints = new ArrayList<>();
    private ArrayList<MapMarker> wayPointIcons = new ArrayList<>();
    private ArrayList<MapMarker> placeSearchResultIcons = new ArrayList<>();
    private String diskCacheRoot = Environment.getExternalStorageDirectory().getPath() + File.separator + ".isolated-here-maps";
    private long simulationSpeedMs = 16; //defines the speed of navigation simulation
    private GeoCoordinate lastKnownLocation;
    private double proceedingDistance = 0;
    private NavigationManager.PositionListener positionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(GeoPosition geoPosition) {

//            if (electronicHorizonActivation != null) {
//                electronicHorizonActivation.startElectronicHorizonUpdate();
//            }
//            Log.d("test", "croppedRoute.getNearestIndex(geoPosition.getCoordinate()): " + croppedRoute.getNearestIndex(geoPosition.getCoordinate()) + "/" + croppedRoute.getAllPoints().size());
            if (routeShapePointGeoCoordinateList.size() > 1) {
                if (croppedRoute.getNearestIndex(geoPosition.getCoordinate()) == croppedRoute.getAllPoints().size() - 1) {
                    cle2CorridorRequestForRoute(routeShapePointGeoCoordinateList, 70);
                }
            }

            if (lastKnownLocation != null) {
                proceedingDistance = lastKnownLocation.distanceTo(geoPosition.getCoordinate());
                if (lastKnownLocation.distanceTo(geoPosition.getCoordinate()) > 0) {
                    lastKnownLocation = geoPosition.getCoordinate();
                }
            } else {
                lastKnownLocation = geoPosition.getCoordinate();

            }
            GeoCoordinate geoPositionGeoCoordinate = geoPosition.getCoordinate();
            geoPositionGeoCoordinate.setAltitude(1);
            currentPositionMapLocalModel.setAnchor(geoPositionGeoCoordinate);
            currentPositionMapLocalModel.setYaw((float) geoPosition.getHeading());
        }

    };
    private NavigationManager.NavigationManagerEventListener navigationManagerEventListener = new NavigationManager.NavigationManagerEventListener() {
        @Override
        public void onRunningStateChanged() {
        }

        @Override
        public void onNavigationModeChanged() {
        }

        @Override
        public void onEnded(NavigationManager.NavigationMode navigationMode) {
            if (!m_activity.isInPictureInPictureMode()) {
                m_navigationManager.removeLaneInformationListener(navigationListeners.getLaneinformationListener());
                minimizeMapButton = m_activity.findViewById(R.id.minimize_map_button);
                minimizeMapButton.setVisibility(View.GONE);
                distanceMarkerLinearLayout.setVisibility(View.GONE);
                mapSchemeChanger.navigationMapOff();
                isNavigating = false;
                isRoadView = false;
                isRouteOverView = true;
                junctionViewImageView.setVisibility(View.GONE);
                signpostImageView.setVisibility(View.GONE);
                m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
                if (m_activity.isInMultiWindowMode()) {
                    new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.7f);
                } else {
                    new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.6f);
                }
                m_map.setTilt(0);
                m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, 0f);
                m_naviControlButton.setVisibility(View.VISIBLE);
                clearButton.setVisibility(View.VISIBLE);
                stopForegroundService();
            } else {
                m_activity.finish();
            }

        }

        @Override
        public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
//            Log.d("Test", "mapUpdateMode is: " + mapUpdateMode);
        }

        @Override
        public void onRouteUpdated(Route route) {
            resetMapRoute(route);
        }

        @Override
        public void onCountryInfo(String s, String s1) {
        }
    };
    /* Navigation Listeners */
    private TrafficWarner.Listener trafficWarnerListener = new TrafficWarner.Listener() {
        @Override
        public void onTraffic(TrafficNotification trafficNotification) {
            String warningText = "";
            List<TrafficNotificationInfo> trafficNotificationInfoList = trafficNotification.getInfoList();
            for (TrafficNotificationInfo trafficNotificationInfo : trafficNotificationInfoList) {
                TrafficNotificationInfo.Type trafficNotificationInfoType = trafficNotificationInfo.getType();
                TrafficEvent.Severity trafficEventSeverity = trafficNotificationInfo.getSeverity();
                if (trafficEventSeverity == TrafficEvent.Severity.VERY_HIGH || trafficEventSeverity == TrafficEvent.Severity.BLOCKING) {
                    long trafficNotificationInfoDistance = trafficNotificationInfo.getDistanceInMeters();
                    if (trafficNotificationInfoDistance < 1000 && trafficNotificationInfoDistance > 0) {
                        switch (trafficNotificationInfoType) {
                            case ON_ROUTE:
                                if ((trafficNotificationInfoDistance / 100) * 100 > 0) {
                                    warningText = (trafficNotificationInfoDistance / 100) * 100 + "m 後\n壅塞路段";
                                    textToSpeech.speak((trafficNotificationInfoDistance / 100) * 100 + "公尺後為壅塞路段。", TextToSpeech.QUEUE_FLUSH, null);
                                } else {
                                    warningText = "經過\n壅塞路段";
                                    textToSpeech.speak("經過壅塞路段。", TextToSpeech.QUEUE_FLUSH, null);
                                }
                                break;
                            case ON_HIGHWAY:
                                if ((trafficNotificationInfoDistance / 100) * 100 > 0) {
                                    warningText = (trafficNotificationInfoDistance / 100) * 100 + "m 後\n壅塞路段";
                                    textToSpeech.speak((trafficNotificationInfoDistance / 100) * 100 + "公尺後經過壅塞路段，請耐心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
                                } else {
                                    warningText = "經過\n壅塞路段";
                                    textToSpeech.speak("經過壅塞路段，請耐心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
                                }
                                break;
                            case NEAR_DESTINATION:
                                warningText = "目的地\n附近壅塞";
                                textToSpeech.speak("目的地附近為壅塞路段，請小心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                        }
                        showTrafficWarningTextView(trafficWarningTextView, warningText);
                    }
                }


            }
        }
    };

    private NavigationManager.TrafficRerouteListener trafficRerouteListener = new NavigationManager.TrafficRerouteListener() {
        @Override
        public void onTrafficRerouted(RouteResult routeResult) {
            super.onTrafficRerouted(routeResult);
            textToSpeech.speak("發現避開壅塞路徑，是否使用？", TextToSpeech.QUEUE_FLUSH, null);
            Snackbar trafficReRoutedSnackBar = Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Found a better route, follow?", Snackbar.LENGTH_LONG);
            trafficReRoutedSnackBar.setAction("Yes", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech.speak("路徑已避開壅塞路段，請小心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
                    resetMapRoute(routeResult.getRoute());
                    Log.d("test", "traffic rerouted.");
                    m_route = routeResult.getRoute();
                    resetMapRoute(m_route);
                    safetyCameraAhead = false;
                    safetyCameraMapMarker.setTransparency(0);
                    safetyCamLinearLayout.setVisibility(View.GONE);
                    cle2CorridorRequestForRoute(routeResult.getRoute().getRouteGeometry(), 70);
                }
            });
            trafficReRoutedSnackBar.show();
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
    };
    private NavigationManager.RerouteListener rerouteListener = new NavigationManager.RerouteListener() {
        @Override
        public void onRerouteBegin() {
            super.onRerouteBegin();
            safetyCameraAhead = false;
            safetyCameraMapMarker.setTransparency(0);
            safetyCamLinearLayout.setVisibility(View.GONE);
//            gpsStatusImageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRerouteEnd(RouteResult routeResult, RoutingError routingError) {
            super.onRerouteEnd(routeResult, routingError);
            if (routingError == RoutingError.NONE) {
                m_route = routeResult.getRoute();
                resetMapRoute(m_route);
                resetMapRoute(routeResult.getRoute());
                cle2CorridorRequestForRoute(routeResult.getRoute().getRouteGeometry(), 70);
            }
        }
    };
    private NavigationManager.RealisticViewListener realisticViewListener = new NavigationManager.RealisticViewListener() {
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
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
        }
    };

    private NavigationManager.ManeuverEventListener maneuverEventListener = new NavigationManager.ManeuverEventListener() {
        @Override
        public void onManeuverEvent() {
            super.onManeuverEvent();
//            Log.d("test", "onManeuverEvent");
//            String nextRoadName = m_navigationManager.getNextManeuver().getNextRoadName();
//            int turn = m_navigationManager.getNextManeuver().getTurn().value();
//            Long distance = m_navigationManager.getNextManeuverDistance();
//            NotificationChannel notificationChannel = new NotificationChannel(
//                    "heresdk",
//                    "HERE_SDK_TEST",
//                    NotificationManager.IMPORTANCE_HIGH);
//            NotificationManager notificationManager = (NotificationManager) m_activity.getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(notificationChannel);
//            Notification.Builder builder = new Notification.Builder(m_activity);
//            builder.setSmallIcon(R.mipmap.ic_launcher)
//                    .setLargeIcon(VectorDrawableConverter.getBitmapFromVectorDrawable(m_activity,   , 128, 128))
//                    .setTicker("")
//                    .setContentTitle(nextRoadName)
//                    .setContentText(distance.toString())
//                    .setChannelId("heresdk");
//            notificationManager.cancel(133);
//            notificationManager.notify(133, builder.build());
        }
    };

    private NavigationManager.SafetySpotListener safetySpotListener = new NavigationManager.SafetySpotListener() {
        @Override
        public void onSafetySpot(SafetySpotNotification safetySpotNotification) {
            super.onSafetySpot(safetySpotNotification);
            List<SafetySpotNotificationInfo> safetySpotNotificationInfoList = safetySpotNotification.getSafetySpotNotificationInfos();
            for (int i = 0; i < safetySpotNotificationInfoList.size(); i++) {
                SafetySpotNotificationInfo safetySpotInfo = safetySpotNotificationInfoList.get(i);
                safetyCameraLocation = safetySpotInfo.getSafetySpot().getCoordinate();
                /* Adding MapMarker to indicate selected safety camera */
                safetyCameraMapMarker.setCoordinate(safetyCameraLocation);
                Image icon = new Image();
                icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(m_activity, R.drawable.ic_pin, 128, 128));
                safetyCameraMapMarker.setIcon(icon);
                safetyCameraMapMarker.setAnchorPoint(getMapMarkerAnchorPoint(safetyCameraMapMarker));
                safetyCameraMapMarker.setTransparency(1);
                distanceToSafetyCamera = safetySpotInfo.getDistance();
                safetyCameraSpeedLimit = safetySpotInfo.getSafetySpot().getSpeedLimit1();
                if (safetyCameraSpeedLimit * 3.6 % 10 >= 8 || safetyCameraSpeedLimit * 3.6 % 10 <= 2) {
                    safetyCameraSpeedLimitKM = (int) ((Math.round((safetyCameraSpeedLimit * 3.6) / 10)) * 10);
                } else {
                    safetyCameraSpeedLimitKM = (int) (Math.round((safetyCameraSpeedLimit * 3.6)));
                }
                textToSpeech.speak("前方有測速照相，速限：" + safetyCameraSpeedLimitKM + "公里", TextToSpeech.QUEUE_FLUSH, null);
                safetyCameraAhead = true;
            }
        }
    };
    private OnPositionChangedListener positionChangedListener = new OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {

            GeoCoordinate geoPositionGeoCoordinate = geoPosition.getCoordinate();
            geoPositionGeoCoordinate.setAltitude(1);
            GeoCoordinate geoPositionGeoCoordinateOnGround = geoPosition.getCoordinate();
            geoPositionGeoCoordinateOnGround.setAltitude(0);

            if (!isNavigating && m_map.getZoomLevel() >= 17) {
                positionAccuracyMapCircle.setCenter(geoPositionGeoCoordinateOnGround);
                float radius = (geoPosition.getLatitudeAccuracy() + geoPosition.getLongitudeAccuracy()) / 2;
                if (radius > 0) {
                    positionAccuracyMapCircle.setRadius(radius);
                }
            }

            if (m_positioningManager.getRoadElement() != null) {
                RoadElement roadElement = m_positioningManager.getRoadElement();
                List<GeoCoordinate> geoCoordinateList = roadElement.getGeometry();
                RoadElement.FormOfWay formOfWay = roadElement.getFormOfWay();
                String routeName = roadElement.getRouteName();
                if (geoPosition.getSpeed() >= 0 && geoPosition.getSpeed() <= 300) {
                    guidanceSpeedView.setVisibility(View.VISIBLE);
                    guidanceSpeedView.setText((int) (geoPosition.getSpeed() * 3.6) + "");
                    if (geoPosition.getSpeed() > roadElement.getSpeedLimit()) {
                        guidanceSpeedView.setTextColor(m_activity.getResources().getColor(R.color.red));
                        speedLabelTextView.setTextColor(m_activity.getResources().getColor(R.color.red));
                    } else {
                        if (m_map.getMapScheme().contains("hybrid") || m_map.getMapScheme().contains("night")) {
                            guidanceSpeedView.setTextColor(m_activity.getResources().getColor(R.color.white));
                            speedLabelTextView.setTextColor(m_activity.getResources().getColor(R.color.white));
                        } else {
                            guidanceSpeedView.setTextColor(m_activity.getResources().getColor(R.color.black));
                            speedLabelTextView.setTextColor(m_activity.getResources().getColor(R.color.black));
                        }

                    }
                } else {
                    guidanceSpeedView.setText("0");
                }
                if (roadElement.getSpeedLimit() >= 0) {
                    guidanceSpeedLimitView.setVisibility(View.VISIBLE);
                    guidanceSpeedLimitView.setCurrentSpeedData(new GuidanceSpeedData(geoPosition.getSpeed(), roadElement.getSpeedLimit()));
                } else {
                    guidanceSpeedLimitView.setVisibility(View.GONE);
                }

                if (isNavigating) {
                    if (formOfWay == RoadElement.FormOfWay.MOTORWAY && !routeName.equals("")) {
                        String layerId = "TWN_HWAY_MILEAGE";
                        int radius = 200;
                        cle2ProximityRequest = new CLE2ProximityRequest(layerId, geoPositionGeoCoordinateOnGround, radius);
                        cle2ProximityRequest.setConnectivityMode(CLE2Request.CLE2ConnectivityMode.OFFLINE);
                        cle2ProximityRequest.setCachingEnabled(true);
                        cle2ProximityRequest.execute(new CLE2Request.CLE2ResultListener() {
                            @Override
                            public void onCompleted(CLE2Result result, String error) {
//                                Log.d("test", "cle2ProximityRequest completed: " + result.getConnectivityModeUsed());
                                /*Display mileage and route number on the upper right corner*/
                                if (error.equals(CLE2Request.CLE2Error.NONE)) {
                                    List<CLE2Geometry> geometries = result.getGeometries();
                                    List<Double> distanceList = new ArrayList<>();
                                    if (geometries.size() > 0) {
                                        for (CLE2Geometry cle2Geometry : geometries) {
                                            CLE2PointGeometry cle2PointGeometry = (CLE2PointGeometry) cle2Geometry;
                                            Double distance = cle2PointGeometry.getPoint().distanceTo(geoPositionGeoCoordinateOnGround);
                                            java.util.Map<String, String> geometryAttributeMap = cle2PointGeometry.getAttributes();
                                            String freeWayId = geometryAttributeMap.get("FREE_WAY_ID");
                                            Log.d("test", distance + " : " + geometryAttributeMap.get("DISTANCE_VALUE"));
                                            if (routeName.equals(freeWayId)) {
                                                distanceList.add(distance);
                                            }
                                        }
                                        double minimumDistance = Collections.min(distanceList);
                                        CLE2Geometry geometry = geometries.get(distanceList.indexOf(minimumDistance));
                                        java.util.Map<String, String> geometryAttributeMap = geometry.getAttributes();
                                        String distanceValue = geometryAttributeMap.get("DISTANCE_VALUE");
                                        Log.d("test", "selected : " + geometryAttributeMap.get("DISTANCE_VALUE"));
                                        String freeWayId = geometryAttributeMap.get("FREE_WAY_ID");
                                        if (routeName.equals(freeWayId) && !isRouteOverView) {
                                            Drawable routeIconDrawable = RouteIconPresenter.getRouteIconName(freeWayId, m_activity);
//                                            Log.d("test", freeWayId + " \\ " + routeIconDrawable + " \\ " + distanceValue);
                                            if (routeIconDrawable != null) {
                                                distanceMarkerFreeIdImageView.setBackground(routeIconDrawable);
                                                distanceMarkerFreeIdImageView.setVisibility(View.VISIBLE);
                                            }
                                            distanceMarkerDistanceValue.setText(distanceValue);
                                            distanceMarkerDistanceValue.setVisibility(View.VISIBLE);
                                            distanceMarkerLinearLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            distanceMarkerLinearLayout.setVisibility(View.GONE);
                                        }
                                    } else {
                                        distanceMarkerLinearLayout.setVisibility(View.GONE);
                                    }
                                } else {
                                    distanceMarkerLinearLayout.setVisibility(View.GONE);
                                    Log.d("test", "CLE2ResultError: " + error);
                                }
                            }
                        });
                    } else {
                        distanceMarkerLinearLayout.setVisibility(View.GONE);
                    }
                }

                /* Traffic Sign display*/
                if (!roadElement.equals(lastRoadElement)) {
                    List<TrafficSign> targetTrafficSignList = new ArrayList<>();
                    GeoCoordinate trafficSignGeoCoordinate = null;
                    try {
                        List<TrafficSign> trafficSignList = roadElement.getTrafficSigns();
                        for (TrafficSign trafficSign : trafficSignList) {
                            double distanceToSign = geoCoordinateList.get(0).distanceTo(trafficSign.coordinate);
                            if (distanceToSign > 0 && !lastTrafficSignList.equals(trafficSignList)) {
                                targetTrafficSignList.add(trafficSign);
                                trafficSignGeoCoordinate = trafficSign.coordinate;
                            }
                        }
                        lastTrafficSignList = targetTrafficSignList;
                    } catch (DataNotReadyException e) {
                        e.printStackTrace();
                    }
                    if (trafficSignGeoCoordinate != null && lastKnownLocation != null) {
                        if (lastKnownLocation.distanceTo(trafficSignGeoCoordinate) < geoPosition.getCoordinate().distanceTo(trafficSignGeoCoordinate)) {
                            isSignShowing = false;
                            TrafficSignPresenter trafficSignPresenter = new TrafficSignPresenter();
                            trafficSignPresenter.setSignImageViews(signImageView1, signImageView2, signImageView3);
                            trafficSignPresenter.showTrafficSigns(targetTrafficSignList, roadElement, m_activity);
                        }
                    }

                }
                lastRoadElement = roadElement;
            } else {
                guidanceSpeedView.setVisibility(View.INVISIBLE);
                guidanceSpeedLimitView.setVisibility(View.INVISIBLE);
            }

            currentPositionMapLocalModel.setAnchor(geoPositionGeoCoordinate);
            if (locationMethod.equals(PositioningManager.LocationMethod.GPS)) {
                if (!isNavigating && m_map.getZoomLevel() >= 17) {
                    positionAccuracyMapCircle.setLineWidth(16);
                    positionAccuracyMapCircle.setLineColor(Color.argb(64, 0, 255, 0));
                    positionAccuracyMapCircle.setFillColor(Color.argb(32, 0, 255, 0));
                }
                gpsStatusImageView.setImageResource(R.drawable.ic_gps_fixed_white_24dp);
                gpsStatusImageView.setImageTintList(m_activity.getResources().getColorStateList(R.color.green));
            } else if (locationMethod.equals(PositioningManager.LocationMethod.NETWORK)) {
                if (!isNavigating && m_map.getZoomLevel() >= 17) {
                    positionAccuracyMapCircle.setLineWidth(16);
                    positionAccuracyMapCircle.setLineColor(Color.argb(64, 255, 255, 0));
                    positionAccuracyMapCircle.setFillColor(Color.argb(32, 255, 255, 0));
                }
                gpsStatusImageView.setImageResource(R.drawable.ic_gps_not_fixed_white_24dp);
                gpsStatusImageView.setImageTintList(m_activity.getResources().getColorStateList(R.color.yellow));
            }

            currentGeoPosition = geoPosition;

            if (!isRouteOverView) {
                if (!isDragged) {
                    m_map.setCenter(geoPosition.getCoordinate(), Map.Animation.NONE);
                }
            }
//            Log.d("test", "safetyCameraAhead: " + safetyCameraAhead);

            if (safetyCameraAhead) {
                distanceToSafetyCamera -= proceedingDistance;
                Log.d("test", "distanceToSafetyCamera: " + distanceToSafetyCamera);
                if (distanceToSafetyCamera < 0) {
                    safetyCameraAhead = false;
                    safetyCameraMapMarker.setTransparency(0);
                    safetyCamLinearLayout.setVisibility(View.GONE);
//                    gpsStatusImageView.setVisibility(View.VISIBLE);
                } else {
                    safetyCamLinearLayout.setVisibility(View.VISIBLE);
                    safetyCamTextView.setText((int) distanceToSafetyCamera + "m");
//                    safetyCamSpeedTextView.setText(safetyCameraSpeedLimitKM + "km/h");
//                    gpsStatusImageView.setVisibility(View.INVISIBLE);
                }

            } else {
                safetyCameraAhead = false;
                safetyCameraMapMarker.setTransparency(0);
                safetyCamLinearLayout.setVisibility(View.GONE);
//                    gpsStatusImageView.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
            Log.d("test", "locationMethod: " + locationMethod.toString());
            Log.d("test", "locationStatus: " + locationStatus.toString());
            if (locationStatus.equals(PositioningManager.LocationStatus.OUT_OF_SERVICE) || locationStatus.equals(PositioningManager.LocationStatus.TEMPORARILY_UNAVAILABLE)) {
                gpsStatusImageView.setImageResource(R.drawable.ic_gps_off_white_24dp);
                gpsStatusImageView.setImageTintList(m_activity.getResources().getColorStateList(R.color.red));
                gpsSwitch.setEnabled(false);
            } else {
                gpsSwitch.setEnabled(true);
            }
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
//            Log.d("Test", "onMapObjectsSelected: " + list.size());
            for (ViewObject viewObject : list) {
                if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                    if (viewObject.equals(currentPositionMapLocalModel)) {
                        isMapRotating = !isMapRotating;
                    } else if (viewObject.equals(positionAccuracyMapCircle)) {
                        isMapRotating = !isMapRotating;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean onTapEvent(PointF pointF) {
            searchBarLinearLayout.setVisibility(View.GONE);
            if (searchResultSnackbar != null) {
                searchResultSnackbar.dismiss();
            }
            if (selectedFeatureMapMarker != null) {
                m_map.removeMapObject(selectedFeatureMapMarker);
            }
            InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(searchTextBar.getWindowToken(), 0);
            new SearchResultHandler(m_activity.findViewById(R.id.mapFragmentView), pointF, m_map);
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(PointF pointF) {
            searchBarLinearLayout.setVisibility(View.GONE);
            touchToAddWaypoint(pointF);
            switchUiControls(View.VISIBLE);
            InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(searchTextBar.getWindowToken(), 0);
            m_map.setCenter(pointF, Map.Animation.LINEAR, m_map.getZoomLevel(), m_map.getOrientation(), m_map.getTilt());
            return true;
        }

        @Override
        public void onPinchLocked() {

        }

        @Override
        public boolean onPinchZoomEvent(float v, PointF pointF) {
            isDragged = true;
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
            GeoCoordinate touchPointGeoCoordinate = m_map.pixelToGeo(pointF);
            GeoCoordinate coordinate = new GeoCoordinate(touchPointGeoCoordinate);
            InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(searchTextBar.getWindowToken(), 0);
            ReverseGeocodeRequest reverseGeocodeRequest = new ReverseGeocodeRequest(coordinate, ReverseGeocodeMode.RETRIEVE_ADDRESSES, 0);
            reverseGeocodeRequest.execute(new ResultListener<com.here.android.mpa.search.Location>() {
                @Override
                public void onCompleted(com.here.android.mpa.search.Location location, ErrorCode errorCode) {
                    if (errorCode == ErrorCode.NONE) {
                        if (location != null) {
                            new SearchResultHandler(m_activity.findViewById(R.id.mapFragmentView), location, m_map);
                        } else {
                            Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Unable to find an address at " + touchPointGeoCoordinate.toString(), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), errorCode.name(), Snackbar.LENGTH_INDEFINITE).show();
                    }
                }
            });
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
    MapMarker.OnDragListener mapMarkerOnDragListenerForRoute = new MapMarker.OnDragListener() {
        @Override
        public void onMarkerDrag(MapMarker mapMarker) {

        }

        @Override
        public void onMarkerDragEnd(MapMarker mapMarker) {
            int mapMarkerIndex = Integer.valueOf(mapMarker.getTitle());
            wayPointIcons.get(mapMarkerIndex).setCoordinate(mapMarker.getCoordinate());
            waypointList.clear();
            RoutePlan routePlan = m_route.getRoutePlan();
            RouteOptions.TransportMode transportMode = routePlan.getRouteOptions().getTransportMode();
            routePlan.removeAllWaypoints();
            for (MapMarker waypointMapMarker : wayPointIcons) {
//                Log.d("test", "mapMarkerIndex: " + waypointMapMarker.getTitle() + " getCoordinate: " + mapMarker.getCoordinate());
                routePlan.addWaypoint(new RouteWaypoint(waypointMapMarker.getCoordinate()));
                waypointList.add(waypointMapMarker.getCoordinate());
            }
            hereRouter.setRoutePlan(routePlan);
            hereRouter.setWaypoints(waypointList);
            calculateRoute(prepareRouteOptions(transportMode));
        }

        @Override
        public void onMarkerDragStart(MapMarker mapMarker) {

        }
    };

    MapFragmentView(AppCompatActivity activity) {
        DataHolder.setActivity(activity);
        m_activity = DataHolder.getActivity();
        initSupportMapFragment();
    }

    private static void intoRouteOverView() {
        if (DataHolder.getNavigationManager() != null) {
            DataHolder.getNavigationManager().pause();
        }
        isRoadView = false;
        isRouteOverView = true;
        if (laneInformationMapOverlay != null) {
            DataHolder.getMap().removeMapOverlay(laneInformationMapOverlay);
        }
        if (distanceMarkerMapOverlayList.size() > 0) {
            for (MapOverlay o : distanceMarkerMapOverlayList) {
                DataHolder.getMap().removeMapOverlay(o);
            }
        }
        trafficWarningTextView.setVisibility(View.INVISIBLE);
        DataHolder.getNavigationManager().setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
        new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.6f);
        DataHolder.getMap().setTilt(0);
        DataHolder.getMap().zoomTo(mapRouteBBox, Map.Animation.LINEAR, 0f);
        m_naviControlButton.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);
        junctionViewImageView.setAlpha(0f);
        signpostImageView.setAlpha(0f);
        DataHolder.getSupportMapFragment().setOnTouchListener(emptyMapOnTouchListener);
    }

    private static PointF getMapMarkerAnchorPoint(MapMarker mapMarker) {
        int iconHeight = (int) mapMarker.getIcon().getHeight();
        int iconWidth = (int) mapMarker.getIcon().getWidth();
        return new PointF((float) (iconWidth / 2), (float) iconHeight);
    }

    private static PointF getMapOverlayAnchorPoint(int width, int height) {
        return new PointF((float) (width / 2), (float) height);
    }

    private void initGuidanceEstimatedArrivalView(NavigationManager navigationManager) {

        guidanceEstimatedArrivalViewPresenter = new GuidanceEstimatedArrivalViewPresenter(navigationManager);
        guidanceEstimatedArrivalViewPresenter.addListener(new GuidanceEstimatedArrivalViewListener() {
            @Override
            public void onDataChanged(GuidanceEstimatedArrivalViewData guidanceEstimatedArrivalViewData) {
                Log.d("Test", "onDataChanged");
                guidanceEstimatedArrivalView.setEstimatedArrivalData(guidanceEstimatedArrivalViewData);
                if (guidanceEstimatedArrivalViewData != null) {
                    Log.d("Test", "guidanceEstimatedArrivalViewData " + guidanceEstimatedArrivalViewData.getEta());
                }
            }
        });
    }

    private void showTrafficWarningTextView(TextView textView, String warningString) {
        textView.setText(warningString);
        textView.setVisibility(View.VISIBLE);
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                textView.setVisibility(View.GONE);
                textView.setText("");
            }
        }.start();
    }

    private void hideTrafficSigns() {
        signName = "";
        signImageView1.setVisibility(View.GONE);
        signImageView2.setVisibility(View.GONE);
        signImageView3.setVisibility(View.GONE);
    }

    private void clearDistanceMarkerMapOverlay() {
        if (distanceMarkerMapOverlayList.size() > 0) {
            for (MapOverlay o : distanceMarkerMapOverlayList) {
                m_map.removeMapOverlay(o);
            }
        }
    }

    private void cle2CorridorRequestForRoute(List<GeoCoordinate> geoCoordinateList, int radius) {
        clearDistanceMarkerMapOverlay();
        distanceMarkerMapOverlayList.clear();
        CLE2DataManager.getInstance().newPurgeLocalStorageTask().start();
        List<GeoCoordinate> croppedShapePointGeoCoordinateList = new ArrayList<>();
        int distance = 0;
        int shapePointIndex = 0;
        while (shapePointIndex < geoCoordinateList.size() - 1) {
//            Log.d("test", routeShapePointGeoCoordinateList.size() + " / " + shapePointIndex);
            if (shapePointIndex < routeShapePointGeoCoordinateList.size()) {
                distance += routeShapePointGeoCoordinateList.get(shapePointIndex).distanceTo(routeShapePointGeoCoordinateList.get(shapePointIndex + 1));
                if (distance < 10000) {
                    croppedShapePointGeoCoordinateList.add(routeShapePointGeoCoordinateList.get(shapePointIndex));
                    routeShapePointGeoCoordinateList.remove(shapePointIndex);
                } else {
                    break;
                }
            } else {
                break;
            }
            shapePointIndex += 1;
        }
        if (croppedShapePointGeoCoordinateList.size() > 1) {
            croppedRoute = new GeoPolyline(croppedShapePointGeoCoordinateList);
            CLE2CorridorRequest cle2CorridorRequest = new CLE2CorridorRequest("TWN_HWAY_MILEAGE", croppedShapePointGeoCoordinateList, radius);
            cle2CorridorRequest.setConnectivityMode(CLE2Request.CLE2ConnectivityMode.AUTO);
            cle2CorridorRequest.setCachingEnabled(true);
            cle2CorridorRequest.execute(new CLE2Request.CLE2ResultListener() {
                @Override
                public void onCompleted(CLE2Result cle2Result, String s) {
                    int numberOfStoredGeometries = CLE2DataManager.getInstance().getNumberOfStoredGeometries("TWN_HWAY_MILEAGE");
                    Log.d("Test", "CLE2CorridorRequest numberOfStoredGeometries: " + numberOfStoredGeometries);
                    for (CLE2Geometry cle2Geometry : cle2Result.getGeometries()) {
                        CLE2PointGeometry cle2PointGeometry = (CLE2PointGeometry) cle2Geometry;
                        String distanceValue = cle2PointGeometry.getAttributes().get("DISTANCE_VALUE");
                        if (distanceValue != null && distanceValue.endsWith("0K")) {
                            TextView distanceMarkerTextView = new TextView(m_activity);
                            distanceMarkerTextView.setText(distanceValue);
                            distanceMarkerTextView.setTextScaleX(0.8f);
                            distanceMarkerTextView.setBackgroundColor(Color.argb(128, 6, 70, 39));
                            distanceMarkerTextView.setTextColor(m_activity.getResources().getColor(R.color.white));
                            MapOverlay distanceMarkerMapOverlay = new MapOverlay(distanceMarkerTextView, cle2PointGeometry.getPoint());
                            m_map.addMapOverlay(distanceMarkerMapOverlay);
                            distanceMarkerMapOverlayList.add(distanceMarkerMapOverlay);
                        }
                    }
                }
            });
        }
    }

    private void initGuidanceStreetLabelView(Context context, NavigationManager navigationManager, Route route) {
        guidanceStreetLabelView = m_activity.findViewById(R.id.guidance_street_label_view);
        guidanceStreetLabelView.setVisibility(View.VISIBLE);
        guidanceStreetLabelPresenter = new GuidanceStreetLabelPresenter(context, navigationManager, route);
        guidanceStreetLabelPresenter.addListener(new GuidanceStreetLabelListener() {
            @Override
            public void onDataChanged(GuidanceStreetLabelData guidanceStreetLabelData) {
                guidanceStreetLabelView.setCurrentStreetData(guidanceStreetLabelData);
            }
        });
    }

    private void initGuidanceManeuverView(Context context, NavigationManager navigationManager, Route route) {
        guidanceManeuverView = m_activity.findViewById(R.id.guidanceManeuverView);
        guidanceManeuverView.setVisibility(View.VISIBLE);
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
        guidanceNextManeuverView.setVisibility(View.VISIBLE);
        guidanceNextManeuverPresenter = new GuidanceNextManeuverPresenter(context, navigationManager, route);
        guidanceNextManeuverPresenter.addListener(new GuidanceNextManeuverListener() {
            @Override
            public void onDataChanged(GuidanceNextManeuverData guidanceNextManeuverData) {
                guidanceNextManeuverView.setNextManeuverData(guidanceNextManeuverData);
            }
        });
    }

    private void switchUiControls(int visibility) {
        m_activity.findViewById(R.id.vehicleTypeTableLayout).setVisibility(visibility);
        northUpButton.setVisibility(visibility);
        carRouteButton.setVisibility(visibility);
        truckRouteButton.setVisibility(visibility);
        scooterRouteButton.setVisibility(visibility);
        bikeRouteButton.setVisibility(visibility);
        pedsRouteButton.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            carRouteButton.setAlpha(1.0f);
            truckRouteButton.setAlpha(1.0f);
            scooterRouteButton.setAlpha(1.0f);
            bikeRouteButton.setAlpha(1.0f);
            pedsRouteButton.setAlpha(1.0f);
        }

    }

    private void switchGuidanceUiViews(int visibility) {
        m_activity.findViewById(R.id.guidanceManeuverView).setVisibility(visibility);
        m_activity.findViewById(R.id.guidance_next_maneuver_view).setVisibility(visibility);
        m_activity.findViewById(R.id.guidance_speed_limit_view).setVisibility(visibility);
        m_activity.findViewById(R.id.guidance_speed_view).setVisibility(visibility);
        m_activity.findViewById(R.id.guidance_street_label_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_estimated_arrival_view).setVisibility(visibility);
        m_activity.findViewById(R.id.guidance_speed_limit_view).setVisibility(visibility);

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
        safetyCameraMapMarker.setTransparency(0);
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
        if (selectedFeatureMapMarker != null) {
            m_map.removeMapObject(selectedFeatureMapMarker);
        }
        GeoCoordinate touchPointGeoCoordinate = m_map.pixelToGeo(p);
        MapMarker mapMarker = new MapMarker(touchPointGeoCoordinate);
        mapMarker.setDraggable(true);
        userInputWaypoints.add(mapMarker);
        mapMarker.setAnchorPoint(getMapMarkerAnchorPoint(mapMarker));
        m_map.addMapObject(mapMarker);
        m_activity.findViewById(R.id.vehicleTypeTableLayout).setVisibility(View.VISIBLE);
        carRouteButton.setVisibility(View.VISIBLE);
        truckRouteButton.setVisibility(View.VISIBLE);
        scooterRouteButton.setVisibility(View.VISIBLE);
        bikeRouteButton.setVisibility(View.VISIBLE);
        pedsRouteButton.setVisibility(View.VISIBLE);
        m_naviControlButton.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);
    }

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

    private void customizeMapScheme(Map map) {
        /*Map Customization - Start*/
        CustomizableScheme m_colorScheme;
        String m_colorSchemeName = "colorScheme";
        if (map != null && map.getCustomizableScheme(m_colorSchemeName) == null) {
            map.createCustomizableScheme(m_colorSchemeName, Map.Scheme.CARNAV_DAY);
            m_colorScheme = map.getCustomizableScheme(m_colorSchemeName);
            ZoomRange range = new ZoomRange(0.0, 20.0);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_WIDTH, 40, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_WIDTH, 40, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_WIDTH, 30, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_WIDTH, 10, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_TUNNELCOLOR, Color.GRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_TUNNELCOLOR, Color.GRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_TUNNELCOLOR, Color.GRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_TUNNELCOLOR, Color.GRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_TUNNELCOLOR, Color.GRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
//            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_FONTSTYLE_SIZE, 30, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_FONTSTYLE_SIZE, 30, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_FONTSTYLE_SIZE, 30, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_FONTSTYLE_SIZE, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_FONTSTYLE_SIZE, 20, range);


            map.setMapScheme(m_colorScheme);
            map.setLandmarksVisible(false);
            map.setExtrudedBuildingsVisible(false);
            map.setCartoMarkersVisible(false);


        } else {
            if (map != null) {
                map.setMapScheme(Map.Scheme.CARNAV_NIGHT);
            }
        }
        /*Map Customization - End*/
    }

    private void createPosition3dObj() {
        currentPositionMapLocalModel = new MapLocalModel();
        LocalModelLoader localModelLoader = new LocalModelLoader(m_activity);
        LocalMesh localMesh = new LocalMesh();
        localMesh.setVertices(localModelLoader.getObjVertices());
        localMesh.setVertexIndices(localModelLoader.getObjIndices());
        localMesh.setTextureCoordinates(localModelLoader.getObjTexCoords());

        currentPositionMapLocalModel.setMesh(localMesh);
        Image image = null;
        try {
            image = new Image();
            image.setImageResource(R.drawable.grad);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPositionMapLocalModel.setTexture(image); //an Image object
        currentPositionMapLocalModel.setScale(6.0f);
        currentPositionMapLocalModel.setDynamicScalingEnabled(true);
        m_map.addMapObject(currentPositionMapLocalModel);
    }

    private void initJunctionView() {
        junctionViewImageView = m_activity.findViewById(R.id.junctionImageView);
        junctionViewImageView.setVisibility(View.GONE);
        signpostImageView = m_activity.findViewById(R.id.signpostImageView);
        signpostImageView.setVisibility(View.GONE);

    }

    private void intoNavigationMode() {
        initJunctionView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            minimizeMapButton = m_activity.findViewById(R.id.minimize_map_button);
            minimizeMapButton.setVisibility(View.VISIBLE);
            minimizeMapButton.setBackgroundResource(R.drawable.round_button_off);
            minimizeMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPipMode = true;
                    Rational aspectRatio = new Rational(23, 10);
                    PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder().setAspectRatio(aspectRatio).build();
                    m_activity.enterPictureInPictureMode(pictureInPictureParams);

                }
            });
        }

        safetyCameraAhead = false;
        safetyCamLinearLayout.setVisibility(View.GONE);
        zoomInButton.setVisibility(View.GONE);
        zoomOutButton.setVisibility(View.GONE);
//        gpsStatusImageView.setVisibility(View.GONE);
        gpsSwitch.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        switchUiControls(View.GONE);
        initGuidanceManeuverView(m_activity, m_navigationManager, m_route);
        initGuidanceNextManeuverView(m_activity, m_navigationManager, m_route);
//        initGuidanceEstimatedArrivalView(m_navigationManager);
        initGuidanceStreetLabelView(m_activity, m_navigationManager, m_route);
//        initGuidanceSpeedView(m_navigationManager, m_positioningManager);
        initGuidanceManeuverView(m_activity, m_navigationManager, m_route);
        switchGuidanceUiViews(View.VISIBLE);
        switchGuidanceUiPresenters(true);

        EnumSet<NavigationManager.NaturalGuidanceMode> naturalGuidanceModes = EnumSet.of(
                NavigationManager.NaturalGuidanceMode.JUNCTION,
                NavigationManager.NaturalGuidanceMode.STOP_SIGN,
                NavigationManager.NaturalGuidanceMode.TRAFFIC_LIGHT
        );
        m_navigationManager.setTrafficAvoidanceMode(NavigationManager.TrafficAvoidanceMode.DYNAMIC);
        m_navigationManager.setRouteRequestInterval(180);

        m_navigationManager.setNaturalGuidanceMode(naturalGuidanceModes);
        if (m_activity.isInMultiWindowMode()) {
            m_map.setTilt(0);
            m_map.setZoomLevel(17);
            m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW_NOZOOM);

//            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.5f);
            MapModeChanger.setSimpleMode();
        } else {
            m_map.setTilt(60);
            m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);

//            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.8f);
            MapModeChanger.setFullMode();
        }
        m_navigationManager.startNavigation(m_route);
        m_positioningManager.setMapMatchingEnabled(true);
        isRoadView = true;
        /* Voice Guidance init */
        VoiceCatalog voiceCatalog = voiceActivation.getVoiceCatalog();
        VoiceGuidanceOptions voiceGuidanceOptions = m_navigationManager.getVoiceGuidanceOptions();
        voiceGuidanceOptions.setVoiceSkin(voiceCatalog.getLocalVoiceSkin(voiceActivation.getDesiredVoiceId()));
        EnumSet<NavigationManager.AudioEvent> audioEventEnumSet = EnumSet.of(
                NavigationManager.AudioEvent.MANEUVER,
                NavigationManager.AudioEvent.ROUTE,
                NavigationManager.AudioEvent.SPEED_LIMIT,
                NavigationManager.AudioEvent.GPS
        );
        m_navigationManager.setEnabledAudioEvents(audioEventEnumSet);

        supportMapFragment.getMapGesture().removeOnGestureListener(customOnGestureListener);
        routeShapePointGeoCoordinateList = m_route.getRouteGeometry();
        cle2CorridorRequestForRoute(routeShapePointGeoCoordinateList, 70);

    }

    private RouteOptions prepareRouteOptions(RouteOptions.TransportMode transportMode) {
        EnumSet<Map.PedestrianFeature> pedestrianFeatureEnumSet = EnumSet.of(
                Map.PedestrianFeature.BRIDGE,
                Map.PedestrianFeature.CROSSWALK,
                Map.PedestrianFeature.ELEVATOR,
                Map.PedestrianFeature.ESCALATOR,
                Map.PedestrianFeature.STAIRS,
                Map.PedestrianFeature.TUNNEL
        );
        RouteOptions routeOptions = new RouteOptions();
        switch (transportMode) {
            case CAR:
                routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
                routeOptions.setHighwaysAllowed(true);
                break;
            case TRUCK:
                routeOptions.setTransportMode(RouteOptions.TransportMode.TRUCK);
                routeOptions.setHighwaysAllowed(true);
                break;
            case SCOOTER:
                routeOptions.setTransportMode(RouteOptions.TransportMode.SCOOTER);
                routeOptions.setHighwaysAllowed(false);
                break;
            case BICYCLE:
                routeOptions.setTransportMode(RouteOptions.TransportMode.BICYCLE);
                routeOptions.setHighwaysAllowed(false);
                break;
            case PEDESTRIAN:
                routeOptions.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
                m_map.setPedestrianFeaturesVisible(pedestrianFeatureEnumSet);
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
        searchBarLinearLayout.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        if (selectedFeatureMapMarker != null) {
            m_map.removeMapObject(selectedFeatureMapMarker);
        }
        hereRouter = new HereRouter(m_activity, routeOptions);
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
        hereRouter.setContext(m_activity);
        hereRouter.createRouteForNavi();
        for (MapMarker m : wayPointIcons) {
            m.setAnchorPoint(getMapMarkerAnchorPoint(m));
            m_map.addMapObject(m);
        }
//        Log.d("Test", "wayPointIcons: " + wayPointIcons.size());


        if (m_map.isTrafficInfoVisible()) {
            DynamicPenalty dynamicPenalty = new DynamicPenalty();
            dynamicPenalty.setTrafficPenaltyMode(Route.TrafficPenaltyMode.OPTIMAL);
            coreRouter.setDynamicPenalty(dynamicPenalty);
        }

        progressBar = m_activity.findViewById(R.id.progressBar);
        calculatingTextView = m_activity.findViewById(R.id.calculatingTextView);
//        Log.d("Test", "Route Calculation Started.");
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
//                Log.d("Test", "Route Calculation Ended.");
//                Log.d("Test", routingError.toString());
                if (routingError == RoutingError.NONE) {
                    if (routeResults.get(0).getRoute() != null) {
                        isRouteOverView = true;
                        clearDistanceMarkerMapOverlay();
                        m_route = routeResults.get(0).getRoute();
                        supportMapFragment.setMapMarkerDragListener(mapMarkerOnDragListenerForRoute);
                        resetMapRoute(m_route);
                        mapRouteBBox = m_route.getBoundingBox();
                        GeoBoundingBoxDimensionCalculator geoBoundingBoxDimensionCalculator = new GeoBoundingBoxDimensionCalculator(mapRouteBBox);

                        mapRouteBBox.expand((float) (geoBoundingBoxDimensionCalculator.getBBoxHeight() * 0.8), (float) (geoBoundingBoxDimensionCalculator.getBBoxWidth() * 0.6));
                        m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, Map.MOVE_PRESERVE_ORIENTATION);
                        m_naviControlButton.setText("Start Navi");
                        String routeLength;
                        if (m_route.getLength() > 999) {
                            routeLength = m_route.getLength() / 1000 + "." + m_route.getLength() % 1000 + " km";
                        } else {
                            routeLength = m_route.getLength() + " m";
                        }

                        switch (m_route.getRoutePlan().getRouteOptions().getTransportMode()) {
                            case CAR:
                            case TRUCK:
                                new TollCostRequest(m_route).execute(new TollCostRequest.Listener<TollCostResult>() {
                                    @Override
                                    public void onComplete(TollCostResult tollCostResult, TollCostError tollCostError) {
                                        if (tollCostError.getErrorCode() == TollCostError.ErrorCode.SUCCESS) {
                                            Log.d("test", "getTransportMode: " + m_route.getRoutePlan().getRouteOptions().getTransportMode());
                                            Log.d("test", "getErrorCode: " + tollCostError.getErrorCode());
                                            Log.d("test", "getErrorMessage: " + tollCostError.getErrorMessage());
                                            Log.d("test", "getTollCostByCountry: " + tollCostResult.getTollCostByCountry());
                                            Log.d("test", "getTollCostByCountry: " + tollCostResult.getTollCostByCountry());
                                            Log.d("test", "getTotalTollCost: " + tollCostResult.getTotalTollCost().doubleValue());
                                            Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Route of " + m_route.getRoutePlan().getRouteOptions().getTransportMode() + " / " + routeLength + " / Cost: " + tollCostResult.getTotalTollCost().doubleValue(), Snackbar.LENGTH_LONG).show();
                                        } else {
                                            Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Route of " + m_route.getRoutePlan().getRouteOptions().getTransportMode() + " / " + routeLength, Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                break;
                            default:
                                Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Route of " + m_route.getRoutePlan().getRouteOptions().getTransportMode() + " / " + routeLength, Snackbar.LENGTH_LONG).show();
                        }
                        supportMapFragment.getMapGesture().removeOnGestureListener(customOnGestureListener);

                    } else {
                        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Can't find a route.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Error: " + routingError.name(), Snackbar.LENGTH_LONG).show();
                    calculatingTextView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    if (mapRoute != null) {
                        m_map.removeMapObject(mapRoute);
                    }
                }
//                retryRouting(m_activity, routingError, routeOptions);
            }
        });
    }

    void onDestroy() {
        /* Stop the navigation when app is destroyed */
        CLE2DataManager.getInstance().newPurgeLocalStorageTask().start();
        if (m_navigationManager != null) {
            stopForegroundService();
            m_navigationManager.stop();
        }
    }

    private void initSupportMapFragment() {
        DataHolder.setSupportMapFragment(getMapFragment());
        supportMapFragment = DataHolder.getSupportMapFragment();
        supportMapFragment.setCopyrightLogoPosition(CopyrightLogoPosition.TOP_CENTER);
        // Set path of isolated disk cache
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }


        boolean success = MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            if (supportMapFragment != null) supportMapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    if (error == Error.NONE) {
                        coreRouter = new CoreRouter();
                        DataHolder.setMap(supportMapFragment.getMap());
                        m_map = DataHolder.getMap();
                        navigationListeners = new NavigationListeners();
                        MapScaleView mapScaleView = m_activity.findViewById(R.id.map_scale_view);
                        mapScaleView.setMap(m_map);
                        mapScaleView.setColor(R.color.black);

                        mapSchemeChanger = new MapSchemeChanger(m_map);

                        CustomRasterTileOverlay customRasterTileOverlay = new CustomRasterTileOverlay();
                        customRasterTileOverlay.setTileUrl("");
                        m_map.addRasterTileSource(customRasterTileOverlay);
                        Geocoder geocoder = new Geocoder(m_activity, Locale.ENGLISH);
                        isNavigating = false;
                        GeoCoordinate defaultMapCenter = new GeoCoordinate(25.038137, 121.513936);
                        m_map.setCenter(defaultMapCenter, Map.Animation.NONE);
                        isDragged = false;

                        /* Rotate compass icon*/
                        m_map.addTransformListener(new Map.OnTransformListener() {
                            @Override
                            public void onMapTransformStart() {
                            }

                            @Override
                            public void onMapTransformEnd(MapState mapState) {
                                if (isNavigating || m_map.getZoomLevel() < 17) {
                                    positionAccuracyMapCircle.setLineWidth(0);
                                    positionAccuracyMapCircle.setFillColor(Color.argb(0, 0, 0, 0));
                                }
                                northUpButton.setRotation(mapState.getOrientation() * -1);
                                if (!isNavigating) {
                                    if (mapState.getZoomLevel() > 8) {
                                        GeoCoordinate mapCenterGeoCoordinate = m_map.getCenter();
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(mapCenterGeoCoordinate.getLatitude(), mapCenterGeoCoordinate.getLongitude(), 1);
                                            if (addresses.size() > 0) {
                                                for (Address address : addresses) {
                                                    String countryName = address.getCountryName();
                                                    String adminAreaName = address.getAdminArea();
                                                    if (countryName != null && adminAreaName != null) {
                                                        Log.d("test", "countryName: " + countryName + " adminAreaName: " + adminAreaName);
//                                                    if (countryName.equals("Taiwan") && adminAreaName.equals("Taipei City")) {
//                                                        if (mapState.getZoomLevel() >= 15 && mapState.getZoomLevel() <= 22) {
//                                                            if (!customRasterTileOverlay.getTileUrl().equals("https://raw.githubusercontent.com/aquawill/taipei_city_parking_layer/master/tiles/%s/%s/%s.png")) {
//                                                                customRasterTileOverlay.setTileUrl("https://raw.githubusercontent.com/aquawill/taipei_city_parking_layer/master/tiles/%s/%s/%s.png");
//                                                            }
//                                                        }
//                                                    }
                                                        if (countryName.equals("China") || countryName.equals("中国")) {
                                                            if (mapState.getZoomLevel() >= 8 && mapState.getZoomLevel() <= 22) {
                                                                if (!customRasterTileOverlay.getTileUrl().equals("https://%s.tile.openstreetmap.org/%s/%s/%s.png")) {
                                                                    String[] subDomainsArray = {"a", "b", "c"};
                                                                    customRasterTileOverlay.setSubDomains(subDomainsArray);
                                                                    customRasterTileOverlay.setTileUrl("https://%s.tile.openstreetmap.org/%s/%s/%s.png");
                                                                }
                                                            }
                                                        } else {
                                                            customRasterTileOverlay.setTileUrl("");
                                                        }
                                                    } else {
                                                        customRasterTileOverlay.setTileUrl("");
                                                    }
                                                }
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });

                        m_map.addSchemeChangedListener(s -> Log.d("test", "onMapSchemeChanged: " + s));

                        supportMapFragment.addOnMapRenderListener(new OnMapRenderListener() {
                            @Override
                            public void onPreDraw() {
                            }

                            @Override
                            public void onPostDraw(boolean b, long l) {
                            }

                            @Override
                            public void onSizeChanged(int i, int i1) {

                                if (!isNavigating) {
                                    if (m_activity.isInMultiWindowMode()) {
                                        new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.7f);
                                    } else {
                                        new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.6f);
                                    }
                                } else {
                                    if (m_activity.isInMultiWindowMode()) {
                                        new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.5f);
                                    } else {
                                        new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.8f);
                                    }
                                }
//                                Log.d("test", "isRouteOverView " + isRouteOverView);

                                if (isRouteOverView) {
                                    m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, Map.MOVE_PRESERVE_ORIENTATION);
                                }
                            }

                            @Override
                            public void onGraphicsDetached() {

                            }

                            @Override
                            public void onRenderBufferCreated() {

                            }
                        });

                        DataHolder.setPositioningManager(new PositionActivation(PositioningManager.LocationMethod.GPS_NETWORK).getPositioningManager());
                        m_positioningManager = DataHolder.getPositioningManager();
                        m_positioningManager.addListener(new WeakReference<>(positionChangedListener));

                        trafficWarningTextView = m_activity.findViewById(R.id.traffic_warning_text_view);

                        gpsSwitch = m_activity.findViewById(R.id.gps_switch);
                        gpsSwitch.setChecked(true);
                        gpsSwitch.setEnabled(false);
                        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    m_positioningManager.stop();
                                    m_positioningManager = null;
                                    m_positioningManager = new PositionActivation(PositioningManager.LocationMethod.GPS_NETWORK).getPositioningManager();
                                } else {
                                    m_positioningManager.stop();
                                    m_positioningManager = null;
                                    m_positioningManager = new PositionActivation(PositioningManager.LocationMethod.NETWORK).getPositioningManager();
                                }
                            }
                        });

                        signImageView1 = m_activity.findViewById(R.id.sign_imageView_1);
                        signImageView2 = m_activity.findViewById(R.id.sign_imageView_2);
                        signImageView3 = m_activity.findViewById(R.id.sign_imageView_3);

                        if (m_activity.isInMultiWindowMode()) {
                            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.7f);
                        } else {
                            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.6f);
                        }
                        m_map.setMapScheme(Map.Scheme.NORMAL_DAY);
//                        customizeMapScheme(m_map);
                        m_map.setMapDisplayLanguage(TRADITIONAL_CHINESE);
                        m_map.setSafetySpotsVisible(true);
                        m_map.setExtrudedBuildingsVisible(false);
                        m_map.setLandmarksVisible(true);
                        m_map.setExtendedZoomLevelsEnabled(false);

                        switchGuidanceUiViews(View.GONE);
                        gpsStatusImageView = m_activity.findViewById(R.id.gps_status_image_view);
                        /* Listeners of map buttons */
                        northUpButton = m_activity.findViewById(R.id.north_up);
                        northUpButton.setOnClickListener(v -> {
                            if (searchResultSnackbar != null) {
                                searchResultSnackbar.dismiss();
                            }
                            isMapRotating = false;
                            m_map.setOrientation(0);
                            northUpButton.setRotation(0);
                            m_map.setTilt(0);
                            m_map.setZoomLevel(16);
                            if (m_activity.isInMultiWindowMode()) {
                                new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.7f);
                            } else {
                                new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.6f);
                            }
                            if (!isRouteOverView) {
                                m_map.setCenter(m_positioningManager.getPosition().getCoordinate(), Map.Animation.LINEAR);
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
                            carRouteButton.setAlpha(1.0f);
                            truckRouteButton.setAlpha(0.3f);
                            scooterRouteButton.setAlpha(0.3f);
                            bikeRouteButton.setAlpha(0.3f);
                            pedsRouteButton.setAlpha(0.3f);

                        });
                        truckRouteButton.setOnClickListener(vTruckRouteButton -> {
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.TRUCK));
                            carRouteButton.setAlpha(0.3f);
                            truckRouteButton.setAlpha(1.0f);
                            scooterRouteButton.setAlpha(0.3f);
                            bikeRouteButton.setAlpha(0.3f);
                            pedsRouteButton.setAlpha(0.3f);
                        });
                        scooterRouteButton.setOnClickListener(vScooterRouteButton -> {
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.SCOOTER));
                            carRouteButton.setAlpha(0.3f);
                            truckRouteButton.setAlpha(0.3f);
                            scooterRouteButton.setAlpha(1.0f);
                            bikeRouteButton.setAlpha(0.3f);
                            pedsRouteButton.setAlpha(0.3f);
                        });
                        bikeRouteButton.setOnClickListener(vBikeRouteButton -> {
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.BICYCLE));
                            carRouteButton.setAlpha(0.3f);
                            truckRouteButton.setAlpha(0.3f);
                            scooterRouteButton.setAlpha(0.3f);
                            bikeRouteButton.setAlpha(1.0f);
                            pedsRouteButton.setAlpha(0.3f);
                        });
                        pedsRouteButton.setOnClickListener(vPedsRouteButton -> {
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.PEDESTRIAN));
                            carRouteButton.setAlpha(0.3f);
                            truckRouteButton.setAlpha(0.3f);
                            scooterRouteButton.setAlpha(0.3f);
                            bikeRouteButton.setAlpha(0.3f);
                            pedsRouteButton.setAlpha(1.0f);
                        });
                        trafficButton = m_activity.findViewById(R.id.traffic_button);
                        trafficButton.setBackgroundResource(R.drawable.round_button_off);
                        trafficButton.setOnClickListener(v -> {
                            if (!m_map.isTrafficInfoVisible()) {
                                trafficEnabled = true;
                                m_map.setTrafficInfoVisible(true);
                                mapSchemeChanger.trafficMapOn();
                                trafficButton.setBackgroundResource(R.drawable.round_button_on);
                            } else {
                                trafficEnabled = false;
                                m_map.setTrafficInfoVisible(false);
                                mapSchemeChanger.trafficMapOff();
                                trafficButton.setBackgroundResource(R.drawable.round_button_off);
                            }
                        });
                        satMapButton = m_activity.findViewById(R.id.sat_map_button);
                        satMapButton.setBackgroundResource(R.drawable.round_button_off);
                        satMapButton.setOnClickListener(v -> {
                            if (!isSatMap) {
                                isSatMap = true;
                                satMapButton.setBackgroundResource(R.drawable.round_button_on);
                                mapSchemeChanger.satelliteMapOn();

                            } else {
                                isSatMap = false;
                                satMapButton.setBackgroundResource(R.drawable.round_button_off);
                                mapSchemeChanger.satelliteMapOff();
                            }
                        });

                        searchButton = m_activity.findViewById(R.id.search_button);
                        searchTextBar = m_activity.findViewById(R.id.search_input_text);
                        searchBarLinearLayout = m_activity.findViewById(R.id.search_bar_linear_layout);
                        searchButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (searchResultSnackbar != null) {
                                    searchResultSnackbar.dismiss();
                                }
                                searchTextBar.setText("");
                                searchBarLinearLayout.setVisibility(View.VISIBLE);
                                searchTextBar.requestFocus();
                                InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(searchTextBar, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                        searchTextBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                    new SearchResultHandler(m_activity.findViewById(R.id.mapFragmentView), m_map.getCenter(), searchTextBar.getText().toString(), m_map);
                                    searchTextBar.clearFocus();
                                    InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(searchTextBar.getWindowToken(), 0);
                                    return true;
                                }
                                return false;
                            }
                        });

                        m_activity.findViewById(R.id.log_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isPositionLogging) {
                                    isPositionLogging = true;
                                    m_activity.findViewById(R.id.log_button).setBackgroundTintList(m_activity.getResources().getColorStateList(R.color.red));
                                    m_positioningManager.setLogType(EnumSet.of(
                                            PositioningManager.LogType.RAW,
                                            PositioningManager.LogType.MATCHED,
                                            PositioningManager.LogType.DATA_SOURCE
                                    ));
                                    Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "GPX Logging: " + m_activity.getFilesDir().getAbsolutePath() + File.separator + "gpx/", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

                        m_naviControlButton = m_activity.findViewById(R.id.startGuidance);
                        m_naviControlButton.setText("Create Route");
                        m_naviControlButton.setOnClickListener(v -> {
                            if (m_route != null) {
                                if (laneInformationMapOverlay != null) {
                                    m_map.removeMapOverlay(laneInformationMapOverlay);
                                }
                                m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, 0f);
                                laneInformationMapOverlay = null;
                                if (m_navigationManager != null) {
                                    m_navigationManager.stop();
                                }
                                m_map.setTilt(0);
                                switchGuidanceUiPresenters(false);
                                startNavigation(m_route, true);
                            } else {
                                calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.CAR));
                                carRouteButton.setAlpha(1.0f);
                                truckRouteButton.setAlpha(0.5f);
                                scooterRouteButton.setAlpha(0.5f);
                                bikeRouteButton.setAlpha(0.5f);
                                pedsRouteButton.setAlpha(0.5f);
                            }
                        });
                        clearButton = m_activity.findViewById(R.id.clear);
                        clearButton.setOnClickListener(v -> resetMap());
                        safetyCamLinearLayout = m_activity.findViewById(R.id.safety_cam_linear_layout);
                        safetyCamImageView = m_activity.findViewById(R.id.safety_cam_image_view);
                        safetyCamTextView = m_activity.findViewById(R.id.safety_cam_text_view);
//                        safetyCamSpeedTextView = m_activity.findViewById(R.id.safety_cam_speed_text_view);
                        distanceMarkerLinearLayout = m_activity.findViewById(R.id.distance_marker_linear_layout);
                        distanceMarkerFreeIdImageView = m_activity.findViewById(R.id.distance_marker_freeway_id);
                        distanceMarkerDistanceValue = m_activity.findViewById(R.id.distance_marker_distance_value);

                        supportMapFragment.getMapGesture().addOnGestureListener(customOnGestureListener, 0, false);

                        /* Download voice */
                        voiceActivation = new VoiceActivation(m_activity);
                        voiceActivation.setContext(m_activity);
                        String desiredVoiceLanguageCode = "CHT";
                        voiceActivation.setDesiredLangCode(desiredVoiceLanguageCode);
                        voiceActivation.downloadCatalogAndSkin();

                        /* adding rotatable position indicator to the map */
                        createPosition3dObj();
                        positionAccuracyMapCircle = new MapCircle();
                        positionAccuracyMapCircle.setRadius(1f);
                        positionAccuracyMapCircle.setLineWidth(0);
                        positionAccuracyMapCircle.setFillColor(Color.argb(0, 0, 0, 0));
                        positionAccuracyMapCircle.setLineColor(Color.argb(0, 0, 0, 0));
                        positionAccuracyMapCircle.setCenter(defaultMapCenter);
                        m_map.addMapObject(positionAccuracyMapCircle);

                        /* init safetyCameraMapMarker */
                        safetyCameraMapMarker = new MapMarker();
                        m_map.addMapObject(safetyCameraMapMarker);

                        Typeface tf = Typeface.createFromAsset(m_activity.getAssets(), "fonts/SFDigitalReadout-Medium.ttf");
                        guidanceSpeedView = m_activity.findViewById(R.id.guidance_speed_view);
                        guidanceSpeedView.setTypeface(tf);
                        speedLabelTextView = m_activity.findViewById(R.id.spd_text_view);
                        speedLabelTextView.setTypeface(tf);
                        guidanceSpeedLimitView = m_activity.findViewById(R.id.guidance_speed_limit_view);

                        TextView distanceTextView = m_activity.findViewById(R.id.distanceView);
                        distanceTextView.setTextSize(DpConverter.convertDpToPixel(16, m_activity));

                    } else {
                        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "ERROR: Cannot initialize Map with error " + error, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void startNavigation(Route route, boolean zoomToRoute) {
        resetMapRoute(route);
        for (MapMarker m : placeSearchResultIcons) {
            m_map.removeMapObject(m);
        }
        m_map.setTilt(0);
        if (zoomToRoute) {
            m_map.zoomTo(mapRouteBBox, Map.Animation.NONE, 0f);
        }
        isDragged = false;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_activity);
        alertDialogBuilder.setTitle("Navigation");
        alertDialogBuilder.setMessage("Choose Mode");
        alertDialogBuilder.setNegativeButton("Navigation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                isNavigating = true;
                hideTrafficSigns();
                isSignShowing = false;
                m_naviControlButton.setText("Stop Navi");
                intoNavigationMode();
                isRouteOverView = false;
                NavigationManager.Error error = m_navigationManager.startNavigation(m_route);
                trafficWarner = m_navigationManager.getTrafficWarner();
                trafficWarner.init();
                trafficWarner.addListener(new WeakReference<>(trafficWarnerListener));
                mapSchemeChanger.navigationMapOn();
                m_naviControlButton.setVisibility(View.GONE);
                clearButton.setVisibility(View.GONE);
                Log.e("Error: ", "NavigationManager.Error: " + error);
                startForegroundService();
            }
        });
        alertDialogBuilder.setPositiveButton("Simulation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                isNavigating = true;
                hideTrafficSigns();
                isSignShowing = false;
                m_naviControlButton.setText("Stop Navi");
                intoNavigationMode();
                isRouteOverView = false;
                NavigationManager.Error error = m_navigationManager.simulate(m_route, simulationSpeedMs);
                trafficWarner = m_navigationManager.getTrafficWarner();
                trafficWarner.init();
                trafficWarner.addListener(new WeakReference<>(trafficWarnerListener));
                mapSchemeChanger.navigationMapOn();
                Log.e("Error: ", "NavigationManager.Error: " + error);
//                ElectronicHorizonActivation electronicHorizonActivation = new ElectronicHorizonActivation();
//                electronicHorizonActivation.setRoute(m_route);
                m_naviControlButton.setVisibility(View.GONE);
                clearButton.setVisibility(View.GONE);
                startForegroundService();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(m_activity.getResources().getColor(R.color.green));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(m_activity.getResources().getColor(R.color.red));
        DataHolder.setNavigationManager(NavigationManager.getInstance());
        m_navigationManager = DataHolder.getNavigationManager();
        m_navigationManager.setMap(m_map);
        mapSchemeChanger = new MapSchemeChanger(m_map, m_navigationManager);
        addNavigationListeners();
    }

    private void resetMap() {
        CLE2DataManager.getInstance().newPurgeLocalStorageTask().start();
        safetyCameraMapMarker.setTransparency(0);
        trafficWarningTextView.setVisibility(View.GONE);
        trafficWarningTextView.setText("");
        distanceMarkerLinearLayout.setVisibility(View.GONE);
        mapSchemeChanger = new MapSchemeChanger(m_map);
        if (trafficWarner != null) {
            trafficWarner.stop();
        }
        mapSchemeChanger.navigationMapOff();
        if (searchResultSnackbar != null) {
            searchResultSnackbar.dismiss();
        }
        isMapRotating = false;
        isNavigating = false;
        m_navigationManager = null;
        switchGuidanceUiViews(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
//        gpsStatusImageView.setVisibility(View.VISIBLE);
        gpsSwitch.setVisibility(View.VISIBLE);
        m_activity.findViewById(R.id.junctionImageView).setVisibility(View.INVISIBLE);
        m_activity.findViewById(R.id.signpostImageView).setVisibility(View.INVISIBLE);
        if (!m_activity.isInMultiWindowMode()) {
            zoomInButton.setVisibility(View.VISIBLE);
            zoomOutButton.setVisibility(View.VISIBLE);
        } else {
            zoomInButton.setVisibility(View.GONE);
            zoomOutButton.setVisibility(View.GONE);
        }
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
        if (m_navigationManager != null) {
            if (m_navigationManager.getRunningState() == NavigationManager.NavigationState.RUNNING) {
                m_navigationManager.stop();
            }
        }
        m_naviControlButton.setText("Create Route");
        m_route = null;
        switchGuidanceUiPresenters(false);

        if (m_activity.isInMultiWindowMode()) {
            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.7f);
        } else {
            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.6f);
        }

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
        northUpButton.setVisibility(View.VISIBLE);

        EnumSet<Map.LayerCategory> poiLayers = EnumSet.of(
                Map.LayerCategory.POI_ICON,
                Map.LayerCategory.POI_LABEL,
                Map.LayerCategory.POINT_ADDRESS
        );
        m_map.setVisibleLayers(poiLayers, true);
        if (laneInformationMapOverlay != null) {
            m_map.removeMapOverlay(laneInformationMapOverlay);
        }
        if (m_navigationManager != null) {
            removeNavigationListeners();
        }
    }

    private void removeNavigationListeners() {
        m_navigationManager.removeNavigationManagerEventListener(navigationListeners.getNavigationManagerEventListener());
        m_navigationManager.removeSafetySpotListener(navigationListeners.getSafetySpotListener());
        m_navigationManager.removeRealisticViewListener(navigationListeners.getRealisticViewListener());
        m_navigationManager.removePositionListener(navigationListeners.getPositionListener());
        m_navigationManager.removeLaneInformationListener(navigationListeners.getLaneinformationListener());
        m_navigationManager.removeRerouteListener(navigationListeners.getRerouteListener());
        m_navigationManager.removeTrafficRerouteListener(navigationListeners.getTrafficRerouteListener());
    }

    private void addNavigationListeners() {
        m_activity.findViewById(R.id.mapFragmentView).getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        navigationListeners.setLaneinformationListener(laneInformationListener);
        navigationListeners.setNavigationManagerEventListener(navigationManagerEventListener);
        navigationListeners.setPositionListener(positionListener);
        navigationListeners.setRealisticViewListener(realisticViewListener);
        navigationListeners.setRerouteListener(rerouteListener);
        navigationListeners.setTrafficRerouteListener(trafficRerouteListener);
        navigationListeners.setSafetySpotListener(safetySpotListener);
        navigationListeners.setManeuverEventListener(maneuverEventListener);

        m_navigationManager.addNavigationManagerEventListener(new WeakReference<>(navigationListeners.getNavigationManagerEventListener()));
        m_navigationManager.addSafetySpotListener(new WeakReference<>(navigationListeners.getSafetySpotListener()));
        m_navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        m_navigationManager.addRealisticViewAspectRatio(NavigationManager.AspectRatio.AR_16x9);
        m_navigationManager.addRealisticViewListener(new WeakReference<>(navigationListeners.getRealisticViewListener()));
        m_navigationManager.addPositionListener(new WeakReference<>(navigationListeners.getPositionListener()));
        if (m_route.getFirstManeuver().getTransportMode() == RouteOptions.TransportMode.CAR || m_route.getFirstManeuver().getTransportMode() == RouteOptions.TransportMode.TRUCK) {
            m_navigationManager.addLaneInformationListener(new WeakReference<>(navigationListeners.getLaneinformationListener()));
        }
        m_navigationManager.addRerouteListener(new WeakReference<>(navigationListeners.getRerouteListener()));
        m_navigationManager.addTrafficRerouteListener(new WeakReference<>(navigationListeners.getTrafficRerouteListener()));
        m_navigationManager.addManeuverEventListener(new WeakReference<>(navigationListeners.getManeuverEventListener()));
    }

    class SearchResultHandler {
        String placesSearchResultTitle;
        String placesSearchCategoryName;
        private Map map = m_map;

        public SearchResultHandler(View view, String placesSearchResultTitle) {
            this.placesSearchResultTitle = placesSearchResultTitle;
        }

        SearchResultHandler(View view, GeoCoordinate geoCoordinate, String inputString, Map map) {
            if (selectedFeatureMapMarker != null) {
                map.removeMapObject(selectedFeatureMapMarker);
            }
            SearchRequest request = new SearchRequest(inputString);
            request.setSearchCenter(geoCoordinate);
            request.setCollectionSize(1);
            ErrorCode error = request.execute(new ResultListener<DiscoveryResultPage>() {
                @Override
                public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode) {
                    if (discoveryResultPage.getPlaceLinks().size() > 0) {
                        List<PlaceLink> discoveryResultPlaceLink = discoveryResultPage.getPlaceLinks();
                        for (PlaceLink placeLink : discoveryResultPlaceLink) {
//                            Log.d("Test", placeLink.getTitle());
                            placesSearchResultTitle = placeLink.getTitle();
//                            placesSearchCategoryName = placeLink.getCategory().getName();
                            GeoCoordinate placesSearchresultGeoCoordinate = placeLink.getPosition();
//                            searchResultString = placesSearchResultTitle + " / " + placesSearchCategoryName;
                            showSelectionFocus(placesSearchresultGeoCoordinate, placesSearchResultTitle);
                            showResultSnackbar(placesSearchresultGeoCoordinate, placesSearchResultTitle, view, Snackbar.LENGTH_INDEFINITE);
                            searchBarLinearLayout.setVisibility(View.GONE);
                            isDragged = true;
                            map.setCenter(placesSearchresultGeoCoordinate, Map.Animation.LINEAR);
                        }
                    } else {
                        Snackbar.make(view, "No result returned.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        SearchResultHandler(View view, com.here.android.mpa.search.Location location, Map map) {
            if (selectedFeatureMapMarker != null) {
                map.removeMapObject(selectedFeatureMapMarker);
            }
            searchResultString = location.getAddress().getText();
            this.showSelectionFocus(location.getCoordinate(), searchResultString);
            showResultSnackbar(location.getCoordinate(), location.getAddress().getText(), view, Snackbar.LENGTH_INDEFINITE);
        }

        SearchResultHandler(View view, PointF pointF, Map map) {
            if (selectedFeatureMapMarker != null) {
                map.removeMapObject(selectedFeatureMapMarker);
            }
            List<ViewObject> selectedMapObjects = m_map.getSelectedObjectsNearby(pointF);
            if (selectedMapObjects.size() > 0) {
//                Log.d("test", selectedMapObjects.get(0).getClass().getName());
                switch (selectedMapObjects.get(0).getClass().getName()) {
                    case "com.here.android.mpa.mapping.MapCartoMarker":
                        MapCartoMarker selectedMapCartoMarker = (MapCartoMarker) selectedMapObjects.get(0);
                        Location location = selectedMapCartoMarker.getLocation();
                        String placeName = location.getInfo().getField(LocationInfo.Field.PLACE_NAME);
                        String category = location.getInfo().getField(LocationInfo.Field.PLACE_CATEGORY);
                        searchResultString = placeName + " / " + category;
                        showSelectionFocus(location.getCoordinate(), searchResultString);
                        showResultSnackbar(location.getCoordinate(), searchResultString, view, Snackbar.LENGTH_INDEFINITE);
                        break;
                    case "com.here.android.mpa.mapping.TrafficEventObject":
                        TrafficEventObject trafficEventObject = (TrafficEventObject) selectedMapObjects.get(0);
                        GeoCoordinate trafficEventObjectGeoCoordinate = trafficEventObject.getCoordinate();
                        String trafficEventShortText = trafficEventObject.getTrafficEvent().getEventText();
                        String trafficEventAffectedStreet = trafficEventObject.getTrafficEvent().getFirstAffectedStreet();
                        searchResultString = trafficEventShortText + " / " + trafficEventAffectedStreet;
                        showResultSnackbar(trafficEventObjectGeoCoordinate, searchResultString, view, Snackbar.LENGTH_SHORT);
                        break;
                    case "com.here.android.mpa.mapping.SafetySpotObject":
                        SafetySpotObject safetySpotObject = (SafetySpotObject) selectedMapObjects.get(0);
                        safetyCameraSpeedLimit = safetySpotObject.getSafetySpotInfo().getSpeedLimit1();
                        int safetyCameraSpeedLimitKM;
                        if (safetyCameraSpeedLimit * 3.6 % 10 >= 8 || safetyCameraSpeedLimit * 3.6 % 10 <= 2) {
                            safetyCameraSpeedLimitKM = (int) ((Math.round((safetyCameraSpeedLimit * 3.6) / 10)) * 10);
                        } else {
                            safetyCameraSpeedLimitKM = (int) (Math.round((safetyCameraSpeedLimit * 3.6)));
                        }
                        searchResultString = "Safety Camera / " + safetyCameraSpeedLimitKM + " km/h";
                        showResultSnackbar(safetySpotObject.getSafetySpotInfo().getCoordinate(), searchResultString, view, Snackbar.LENGTH_SHORT);
                        break;
                }
            }
        }

        private void showSelectionFocus(GeoCoordinate geoCoordinate, String string) {
            selectedFeatureMapMarker = new MapMarker();
            selectedFeatureMapMarker.setCoordinate(geoCoordinate);
            Image icon = new Image();
            icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(m_activity, R.drawable.ic_search_result));
            selectedFeatureMapMarker.setIcon(icon);
            selectedFeatureMapMarker.setTitle(string);
            selectedFeatureMapMarker.setAnchorPoint(getMapMarkerAnchorPoint(selectedFeatureMapMarker));
            m_map.addMapObject(selectedFeatureMapMarker);
        }

        private void showResultSnackbar(GeoCoordinate waypointMapMakerGeoCoordinate, String stringToShow, View view, int duration) {

            searchResultSnackbar = Snackbar.make(view, stringToShow, duration);
            searchResultSnackbar.setAction("Add Waypoint", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedFeatureMapMarker != null) {
                        map.removeMapObject(selectedFeatureMapMarker);
                    }
                    addingWaypointMapMarker(waypointMapMakerGeoCoordinate);
                    map.setCenter(waypointMapMakerGeoCoordinate, Map.Animation.LINEAR);
                    isDragged = true;
                    switchUiControls(View.VISIBLE);
                }
            });
            searchResultSnackbar.show();
        }

        private void addingWaypointMapMarker(GeoCoordinate geoCoordinate) {
            MapMarker mapMarker = new MapMarker(geoCoordinate);
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
    }

    class OverlayLayers {

    }

}

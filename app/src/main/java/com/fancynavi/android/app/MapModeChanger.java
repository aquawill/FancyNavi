package com.fancynavi.android.app;

import android.view.View;

import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.SafetySpotNotification;

import java.lang.ref.WeakReference;
import java.util.EnumSet;

import static com.fancynavi.android.app.DataHolder.emptyMapOnTouchListener;
import static com.fancynavi.android.app.DataHolder.isNavigating;
import static com.fancynavi.android.app.DataHolder.mapOnTouchListenerForNavigation;
import static com.fancynavi.android.app.MapFragmentView.laneInformationMapOverlay;
import static com.fancynavi.android.app.MapFragmentView.navigationListeners;

class MapModeChanger {

    private static final NavigationManager.SafetySpotListener simpleSafetySpotListener = new NavigationManager.SafetySpotListener() {
        @Override
        public void onSafetySpot(SafetySpotNotification safetySpotNotification) {
            super.onSafetySpot(safetySpotNotification);
        }
    };

    static void setMapUpdateMode(NavigationManager.MapUpdateMode mapUpdateMode) {
        DataHolder.getNavigationManager().setMapUpdateMode(mapUpdateMode);
    }

    static void setMapTilt(float tilt) {
        DataHolder.getMap().setTilt(tilt);
    }

    static void setMapZoomLevel(double zoomLevel) {
        DataHolder.getMap().setZoomLevel(zoomLevel);
    }

    static void addNavigationListeners() {
        if (DataHolder.getNavigationManager() != null) {
            EnumSet<NavigationManager.AudioEvent> audioEventEnumSet = EnumSet.of(
                    NavigationManager.AudioEvent.MANEUVER,
                    NavigationManager.AudioEvent.ROUTE,
                    NavigationManager.AudioEvent.SPEED_LIMIT,
                    NavigationManager.AudioEvent.GPS
            );
            DataHolder.getNavigationManager().setEnabledAudioEvents(audioEventEnumSet);
            DataHolder.getNavigationManager().removeSafetySpotListener(simpleSafetySpotListener);
            DataHolder.getNavigationManager().addLaneInformationListener(new WeakReference<>(navigationListeners.getLaneInformationListener()));
            DataHolder.getNavigationManager().addRealisticViewListener(new WeakReference<>(navigationListeners.getRealisticViewListener()));
            DataHolder.getNavigationManager().addSafetySpotListener(new WeakReference<>(navigationListeners.getSafetySpotListener()));
        }
    }

    static void removeNavigationListeners() {
        if (DataHolder.getNavigationManager() != null) {
            DataHolder.getNavigationManager().removeLaneInformationListener(navigationListeners.getLaneInformationListener());
            DataHolder.getNavigationManager().removeRealisticViewListener(navigationListeners.getRealisticViewListener());
            DataHolder.getNavigationManager().removeSafetySpotListener(navigationListeners.getSafetySpotListener());
            DataHolder.getNavigationManager().addSafetySpotListener(new WeakReference<>(simpleSafetySpotListener));
            EnumSet<NavigationManager.AudioEvent> audioEventEnumSet = EnumSet.of(
                    NavigationManager.AudioEvent.MANEUVER,
                    NavigationManager.AudioEvent.ROUTE,
                    NavigationManager.AudioEvent.SPEED_LIMIT,
                    NavigationManager.AudioEvent.GPS,
                    NavigationManager.AudioEvent.SAFETY_SPOT
            );
            DataHolder.getNavigationManager().setEnabledAudioEvents(audioEventEnumSet);

        }
    }

    static void intoSimpleMode() {
        DataHolder.setSimpleMode(true);
        DataHolder.getActivity().findViewById(R.id.guidance_speed_view).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.sat_map_button).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.traffic_button).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.junctionImageView).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.signpostImageView).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_1).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_2).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_3).setAlpha(0);
//        DataHolder.getActivity().findViewById(R.id.zoom_in).setVisibility(View.GONE);
//        DataHolder.getActivity().findViewById(R.id.zoom_out).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.log_button).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.map_scale_view).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.minimize_map_button).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.guidance_current_street_view).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.traffic_warning_text_view).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.download_button).setAlpha(0);
        new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.7f);
        DataHolder.getAndroidXMapFragment().setOnTouchListener(emptyMapOnTouchListener);
        if (laneInformationMapOverlay != null) {
            DataHolder.getMap().removeMapOverlay(laneInformationMapOverlay);
        }

    }

    static void intoFullMode() {
        DataHolder.setSimpleMode(false);
//        if (DataHolder.isOffScreenRendererEnabled()) {
//            DataHolder.getMapOffScreenRenderer().stop();
//            DataHolder.setOffScreenRendererEnabled(false);
//        }
        DataHolder.getActivity().findViewById(R.id.guidance_speed_view).setAlpha(1);
        DataHolder.getActivity().findViewById(R.id.sat_map_button).setVisibility(View.VISIBLE);
        DataHolder.getActivity().findViewById(R.id.traffic_button).setVisibility(View.VISIBLE);
        DataHolder.getActivity().findViewById(R.id.junctionImageView).setAlpha(1);
        DataHolder.getActivity().findViewById(R.id.signpostImageView).setAlpha(1);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_1).setAlpha(0.7f);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_2).setAlpha(0.7f);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_3).setAlpha(0.7f);
        DataHolder.getActivity().findViewById(R.id.download_button).setAlpha(1);
        DataHolder.getActivity().findViewById(R.id.minimize_map_button).setAlpha(1);
        DataHolder.getActivity().findViewById(R.id.map_scale_view).setVisibility(View.VISIBLE);
        DataHolder.getActivity().findViewById(R.id.guidance_current_street_view).setAlpha(1);
        if (!isNavigating) {
//            DataHolder.getActivity().findViewById(R.id.zoom_in).setVisibility(View.VISIBLE);
//            DataHolder.getActivity().findViewById(R.id.zoom_out).setVisibility(View.VISIBLE);
        }
        DataHolder.getActivity().findViewById(R.id.log_button).setVisibility(View.VISIBLE);
        DataHolder.getActivity().findViewById(R.id.traffic_warning_text_view).setAlpha(1);
        if (isNavigating) {
            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.75f);
            DataHolder.getNavigationManager().setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
            DataHolder.getAndroidXMapFragment().setOnTouchListener(mapOnTouchListenerForNavigation);
        } else {
            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.6f);
        }
    }
}

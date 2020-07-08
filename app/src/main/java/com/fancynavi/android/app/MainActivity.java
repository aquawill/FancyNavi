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

package com.fancynavi.android.app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.here.android.mpa.customlocation2.CLE2DataManager;
import com.here.android.mpa.customlocation2.CLE2OperationResult;
import com.here.android.mpa.customlocation2.CLE2Request;
import com.here.android.mpa.customlocation2.CLE2Task;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapOverlay;
import com.here.odnp.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.fancynavi.android.app.DataHolder.TAG;
import static com.fancynavi.android.app.DataHolder.isDragged;
import static com.fancynavi.android.app.DataHolder.isNavigating;
import static com.fancynavi.android.app.DataHolder.isPipMode;
import static com.fancynavi.android.app.DataHolder.isRouteOverView;
import static com.fancynavi.android.app.MapFragmentView.clearButton;
import static com.fancynavi.android.app.MapFragmentView.currentPositionMapLocalModel;
import static com.fancynavi.android.app.MapFragmentView.distanceMarkerMapOverlayList;
import static com.fancynavi.android.app.MapFragmentView.junctionViewImageView;
import static com.fancynavi.android.app.MapFragmentView.laneInformationMapOverlay;
import static com.fancynavi.android.app.MapFragmentView.mapOnTouchListenerForNavigation;
import static com.fancynavi.android.app.MapFragmentView.navigationControlButton;
import static com.fancynavi.android.app.MapFragmentView.northUpButton;
import static com.fancynavi.android.app.MapFragmentView.signpostImageView;
import static com.fancynavi.android.app.MapFragmentView.trafficWarningTextView;

//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.SettingsClient;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Main activity which launches map view and handles Android run-time requesting permission.
 */

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    static boolean isMapRotating = false;
    static float lightSensorValue;
    static float azimuth = 0f;
    static boolean isVisible = true;
    static TextToSpeech textToSpeech;
    SensorManager mySensorManager;
    Bundle mViewBundle = new Bundle();
    List<Float> azimuthArrayList = new ArrayList<>();
    private boolean isDarkMode = false;
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] mGravity;
        float[] mGeomagnetic;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "onSensorChanged");
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                lightSensorValue = event.values[0];
                Log.d(TAG, String.valueOf(lightSensorValue));
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic = event.values;
            }
            if (!Build.FINGERPRINT.contains("generic")) {
                if (DataHolder.getMap() != null) {
                    if (lightSensorValue < 30) {
                        if (!isDarkMode) {
                            setTheme(R.style.MSDKUIDarkTheme_WhiteAccent);
                            new MapSchemeChanger(DataHolder.getMap(), DataHolder.getNavigationManager()).darkenMap();
                            ((MapScaleView) findViewById(R.id.map_scale_view)).setColor(Color.WHITE);
                            findViewById(R.id.north_up).setBackgroundResource(R.drawable.compass_dark);
                            isDarkMode = true;
                        }
                    } else {
                        if (isDarkMode) {
                            setTheme(R.style.MSDKUIDarkTheme);
                            new MapSchemeChanger(DataHolder.getMap(), DataHolder.getNavigationManager()).lightenMap();
                            ((MapScaleView) findViewById(R.id.map_scale_view)).setColor(Color.BLACK);
                            findViewById(R.id.north_up).setBackgroundResource(R.drawable.compass_bright);
                            isDarkMode = false;
                        }
                    }
                }
            }
            if (mGravity != null && mGeomagnetic != null) {
                float[] R = new float[9];
                float[] I = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    azimuth = (float) Math.toDegrees(orientation[0]);
                    if (azimuthArrayList.size() > 64) {
                        azimuthArrayList.remove(0);
                    }
                    azimuthArrayList.add(azimuth);
                    float averagedAzimuth = getAverageFromFloatArrayList(azimuthArrayList);
                    if (currentPositionMapLocalModel != null && !isNavigating) {
                        float rotatingAngle = new BigDecimal(averagedAzimuth).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        currentPositionMapLocalModel.setYaw(rotatingAngle);
                        if (!isMapRotating) {
                            if (!isDragged) {
                                northUpButton.setRotation(0);
                                DataHolder.getMap().setOrientation(0);
                            }
                        } else {
                            northUpButton.setRotation(rotatingAngle * -1);
                            DataHolder.getMap().setCenter(DataHolder.getPositioningManager().getPosition().getCoordinate(), Map.Animation.LINEAR);
                            DataHolder.getMap().setOrientation(rotatingAngle);
                            isDragged = false;
                        }
                    }
                }
            }
        }
    };
    private Configuration configuration;
    private DisplayMetrics metrics;
    private MapFragmentView mapFragmentView;
    //    private LocationRequest locationRequest;
    private long UPDATE_INTERVAL = 5 * 1000;
    private long FASTEST_INTERVAL = 1000;

    public void hideGuidanceView() {
        View guidanceView = findViewById(R.id.guidance_maneuver_view);
        guidanceView.setVisibility(View.GONE);
    }

    public void hideJunctionView() {
        View junctionView = findViewById(R.id.junctionImageView);
        junctionView.setVisibility(View.GONE);
        View signpostView = findViewById(R.id.signpostImageView);
        signpostView.setVisibility(View.GONE);
    }

    public void setData(Bundle data) {
        mViewBundle = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        configuration = getResources().getConfiguration();
        metrics = getResources().getDisplayMetrics();

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (textToSpeech.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        hideGuidanceView();
        hideJunctionView();
        requestPermissions();
//        startLocationUpdates();
        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor accelerometerSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magneticSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (lightSensor != null) {
            mySensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (magneticSensor != null) {
            mySensorManager.registerListener(sensorEventListener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (accelerometerSensor != null) {
            mySensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        Snackbar purgeCacheSnackBar = Snackbar.make(this.findViewById(R.id.mapFragmentView), "HERE SDK v" + com.here.android.mpa.common.Version.getSdkVersion(), Snackbar.LENGTH_LONG);
        purgeCacheSnackBar.setAction("Clear Cache", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLE2DataManager.getInstance().newPurgeLocalStorageTask().start(new CLE2Task.Callback<CLE2OperationResult>() {
                    @Override
                    public void onTaskFinished(CLE2OperationResult cle2OperationResult, CLE2Request.CLE2Error cle2Error) {
                        Log.d(TAG, "getAffectedLayerIds: " + cle2OperationResult.getAffectedLayerIds());
                        Log.d(TAG, "getAffectedItemCount: " + cle2OperationResult.getAffectedItemCount());
                        Log.d(TAG, "getErrorCode: " + cle2Error.getErrorCode());
                    }
                });
                File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + ".isolated-here-maps");
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
                int mPendingIntentId = 528491;
                PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        });
        purgeCacheSnackBar.show();

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (!isPipMode) {
            isVisible = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        isVisible = true;
        if (isNavigating) {
            intoGuidanceMode();
        } else {
            isDragged = false;
        }
    }

    private float getAverageFromFloatArrayList(List<Float> list) {
        float sum = 0;
        for (float e : list) {
            sum += e;
        }
        return sum / list.size();
    }

//    protected void startLocationUpdates() {
//
//        // Create the location request to start receiving updates
//        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(UPDATE_INTERVAL);
//        locationRequest.setFastestInterval(FASTEST_INTERVAL);
//
//        // Create LocationSettingsRequest object using location request
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(locationRequest);
//        LocationSettingsRequest locationSettingsRequest = builder.build();
//
//        // Check whether location settings are satisfied
//        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
//        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
//        settingsClient.checkLocationSettings(locationSettingsRequest);
//
//        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            checkPermissions();
//            return;
//        }
//        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
//                    @Override
//                    public void onLocationResult(LocationResult locationResult) {
//                        // do work here
//                        onLocationChanged(locationResult.getLastLocation());
//                    }
//                },
//                Looper.myLooper());
//    }

//    public void onLocationChanged(Location location) {
//        //updatedLatLng = new GeoCoordinate(location.getLatitude(), location.getLongitude());
//    }

//    public void getLastLocation() {
//        // Get last known recent location using new Google Play Services SDK (v11+)
//        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            checkPermissions();
//            return;
//        }
//        locationClient.getLastLocation()
//                .addOnSuccessListener(new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // GPS location can be null if GPS is switched off
//                        if (location != null) {
//                            onLocationChanged(location);
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//    }

//    private boolean checkPermissions() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            requestPermissions();
//            return false;
//        }
//    }

    void intoGuidanceMode() {
        if (DataHolder.getNavigationManager() != null) {
            DataHolder.getNavigationManager().setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
            if (isInMultiWindowMode()) {
                new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.6f);
                MapModeChanger.intoSimpleMode();
            } else {
                new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.75f);
                MapModeChanger.intoFullMode();
            }
            DataHolder.getMap().setTilt(60);
            isRouteOverView = false;
            if (laneInformationMapOverlay != null) {
                DataHolder.getMap().addMapOverlay(laneInformationMapOverlay);
            }
            trafficWarningTextView.setVisibility(View.VISIBLE);
            junctionViewImageView.setAlpha(1f);
            signpostImageView.setAlpha(1f);
            navigationControlButton.setVisibility(View.GONE);
            clearButton.setVisibility(View.GONE);
            DataHolder.getNavigationManager().resume();
            if (distanceMarkerMapOverlayList.size() > 0) {
                for (MapOverlay o : distanceMarkerMapOverlayList) {
                    DataHolder.getMap().addMapOverlay(o);
                }
            }
            if (!DataHolder.isSimpleMode()) {
                DataHolder.getAndroidXMapFragment().setOnTouchListener(mapOnTouchListenerForNavigation);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!DataHolder.isRoadView && isNavigating) {
            intoGuidanceMode();
        } else {
            isDragged = false;
        }
        //super.onBackPressed();
    }

    /**
     * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
     * needs when the app is running.
     */
    void requestPermissions() {

        final List<String> requiredSDKPermissions = new ArrayList<String>();
        requiredSDKPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredSDKPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredSDKPermissions.add(Manifest.permission.INTERNET);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
//        requiredSDKPermissions.add(Manifest.permission.CAMERA);

        ActivityCompat.requestPermissions(this,
                requiredSDKPermissions.toArray(new String[requiredSDKPermissions.size()]),
                REQUEST_CODE_ASK_PERMISSIONS);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            for (int index = 0; index < permissions.length; index++) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(findViewById(R.id.mapFragmentView), "Required permission " + permissions[index] + " not granted. ", Snackbar.LENGTH_LONG).show();
                }
            }

            mapFragmentView = new MapFragmentView(this);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        if (isInMultiWindowMode) {
            if (!isPipMode) {
                Intent intent = new Intent(this, this.getClass());
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
            if (isNavigating) {
                MapModeChanger.setMapTilt(0);
                MapModeChanger.setMapZoomLevel(17);
                MapModeChanger.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW_NOZOOM);
            }
            MapModeChanger.intoSimpleMode();
            MapModeChanger.removeNavigationListeners();
        } else {
            MapModeChanger.intoFullMode();
            if (isNavigating) {
                MapModeChanger.addNavigationListeners();
            }
            if (isNavigating) {
                MapModeChanger.setMapTilt(60);
                MapModeChanger.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        setVisible(true);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
//            findViewById(R.id.guidance_next_maneuver_view).setVisibility(View.GONE);
//            findViewById(R.id.map_constraint_layout).setVisibility(View.GONE);
//            findViewById(R.id.guidance_maneuver_view).setVisibility(View.GONE);
//            findViewById(R.id.guidance_maneuver_panel_layout).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
//            TextView distanceTextView = findViewById(R.id.distanceView);
//            distanceTextView.setTextSize(DpConverter.convertDpToPixel(8, this));
            MapModeChanger.intoSimpleMode();
            MapModeChanger.removeNavigationListeners();
            DataHolder.getAndroidXMapFragment().onPictureInPictureModeChanged(isInPictureInPictureMode);
            isPipMode = isInPictureInPictureMode;
        } else {
//            findViewById(R.id.guidance_next_maneuver_view).setVisibility(View.VISIBLE);
//            findViewById(R.id.map_constraint_layout).setVisibility(View.VISIBLE);
//            findViewById(R.id.guidance_maneuver_panel_layout).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
//            TextView distanceTextView = findViewById(R.id.distanceView);
//            distanceTextView.setTextSize(DpConverter.convertDpToPixel(16, this));
            MapModeChanger.intoFullMode();
            if (isNavigating) {
                MapModeChanger.addNavigationListeners();
            }
            MapModeChanger.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
            DataHolder.getAndroidXMapFragment().onPictureInPictureModeChanged(isInPictureInPictureMode);
            isPipMode = isInPictureInPictureMode;
        }
    }

    @Override
    public void onDestroy() {
        mapFragmentView.onDestroy();
        Log.d(TAG, "onDestroy");
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        isNavigating = false;
        isRouteOverView = false;
        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        LinearLayout mainLinearLayout = DataHolder.getActivity().findViewById(R.id.main_linear_layout);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            findViewById(R.id.zoom_in).setVisibility(View.GONE);
//            findViewById(R.id.zoom_out).setVisibility(View.GONE);
            findViewById(R.id.guidance_maneuver_view).setVisibility(View.GONE);
            findViewById(R.id.guidance_next_maneuver_view).setVisibility(View.GONE);
//            mainLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            findViewById(R.id.zoom_in).setVisibility(View.VISIBLE);
//            findViewById(R.id.zoom_out).setVisibility(View.VISIBLE);
//            mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
            if (isNavigating) {
                findViewById(R.id.guidance_maneuver_view).setVisibility(View.VISIBLE);
                findViewById(R.id.guidance_next_maneuver_view).setVisibility(View.VISIBLE);
            }
        }
    }
}

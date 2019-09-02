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
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.fancynavi.app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static com.fancynavi.android.app.MapFragmentView.clearButton;
import static com.fancynavi.android.app.MapFragmentView.currentPositionMapLocalModel;
import static com.fancynavi.android.app.MapFragmentView.distanceMarkerMapOverlayList;
import static com.fancynavi.android.app.MapFragmentView.isDragged;
import static com.fancynavi.android.app.MapFragmentView.isNavigating;
import static com.fancynavi.android.app.MapFragmentView.isRouteOverView;
import static com.fancynavi.android.app.MapFragmentView.junctionViewImageView;
import static com.fancynavi.android.app.MapFragmentView.laneMapOverlay;
import static com.fancynavi.android.app.MapFragmentView.m_map;
import static com.fancynavi.android.app.MapFragmentView.m_naviControlButton;
import static com.fancynavi.android.app.MapFragmentView.m_navigationManager;
import static com.fancynavi.android.app.MapFragmentView.m_positioningManager;
import static com.fancynavi.android.app.MapFragmentView.mapOnTouchListener;
import static com.fancynavi.android.app.MapFragmentView.northUpButton;
import static com.fancynavi.android.app.MapFragmentView.signpostImageView;
import static com.fancynavi.android.app.MapFragmentView.supportMapFragment;
import static com.fancynavi.android.app.MapFragmentView.trafficWarningTextView;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Main activity which launches map view and handles Android run-time requesting permission.
 */

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    static boolean isMapRotating = false;
    static float lightSensorValue;
    static float azimuth = 0f;
    static TextToSpeech textToSpeech;
    SensorManager mySensorManager;
    View mapFragmentView;
    Bundle mViewBundle = new Bundle();

    ArrayList<Float> azimuthArrayList = new ArrayList<>();


    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] mGravity;
        float[] mGeomagnetic;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                lightSensorValue = event.values[0];
                Log.d("test", String.valueOf(lightSensorValue));
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic = event.values;
            }
            if (!Build.FINGERPRINT.contains("generic")) {
                if (m_map != null) {
                    if (lightSensorValue < 50) {
                        setTheme(R.style.MSDKUIDarkTheme_WhiteAccent);
                        new MapSchemeChanger(m_map, m_navigationManager).darkenMap();
                        ((MapScaleView) findViewById(R.id.map_scale_view)).setColor(Color.WHITE);
                        findViewById(R.id.north_up).setBackgroundResource(R.drawable.compass_dark);
                    } else {
                        setTheme(R.style.MSDKUIDarkTheme);
                        new MapSchemeChanger(m_map, m_navigationManager).lightenMap();
                        ((MapScaleView) findViewById(R.id.map_scale_view)).setColor(Color.BLACK);
                        findViewById(R.id.north_up).setBackgroundResource(R.drawable.compass_bright);
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
                    if (azimuthArrayList.size() > 255) {
                        azimuthArrayList.remove(0);
                    }
                    azimuthArrayList.add(azimuth);
                    if (currentPositionMapLocalModel != null && !isNavigating) {
                        float rotatingAngle = 0f;
                        float total = 0f;
                        if (azimuthArrayList.size() > 0) {
                            for (float v : azimuthArrayList) {
                                total += v;
                            }
                            rotatingAngle = new BigDecimal(total / azimuthArrayList.size()).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                        }
                        currentPositionMapLocalModel.setYaw(rotatingAngle);
                        if (!isMapRotating) {
                            if (!isDragged) {
                                northUpButton.setRotation(0);
                                m_map.setOrientation(0);
                            }
                        } else {
                            northUpButton.setRotation(rotatingAngle * -1);
                            m_map.setCenter(m_positioningManager.getPosition().getCoordinate(), Map.Animation.NONE);
                            m_map.setOrientation(rotatingAngle);
                            isDragged = false;
                        }
                    }
                }
            }
        }
    };
    private MapFragmentView m_mapFragmentView;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    public void hideGuidanceView() {
        View guidanceView = findViewById(R.id.guidanceManeuverView);
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

        mapFragmentView = findViewById(R.id.mapFragmentView);
        setContentView(R.layout.activity_main);
        hideGuidanceView();
        hideJunctionView();
        requestPermissions();
        startLocationUpdates();
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
        Snackbar purgeCacheSnackBar = Snackbar.make(this.findViewById(R.id.mapFragmentView), "Refresh Map Cache?", Snackbar.LENGTH_LONG);
        purgeCacheSnackBar.setAction("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLE2DataManager.getInstance().newPurgeLocalStorageTask().start(new CLE2Task.Callback<CLE2OperationResult>() {
                    @Override
                    public void onTaskFinished(CLE2OperationResult cle2OperationResult, CLE2Request.CLE2Error cle2Error) {
                        Log.d("Test", "getAffectedLayerIds: " + cle2OperationResult.getAffectedLayerIds());
                        Log.d("Test", "getAffectedItemCount: " + cle2OperationResult.getAffectedItemCount());
                        Log.d("Test", "getErrorCode: " + cle2Error.getErrorCode());
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

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        //updatedLatLng = new GeoCoordinate(location.getLatitude(), location.getLongitude());
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        if (!MapFragmentView.isRoadView) {
            if (m_navigationManager != null) {
                m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
                new ShiftMapCenter(m_map, 0.5f, 0.8f);
                m_map.setTilt(60);
                isRouteOverView = false;
                if (laneMapOverlay != null) {
                    m_map.addMapOverlay(laneMapOverlay);
                }
                trafficWarningTextView.setVisibility(View.VISIBLE);
                junctionViewImageView.setAlpha(1f);
                signpostImageView.setAlpha(1f);
                m_naviControlButton.setVisibility(View.GONE);
                clearButton.setVisibility(View.GONE);
                m_navigationManager.resume();
                if (distanceMarkerMapOverlayList.size() > 0) {
                    for (MapOverlay o : distanceMarkerMapOverlayList) {
                        m_map.addMapOverlay(o);
                    }
                }
                supportMapFragment.setOnTouchListener(mapOnTouchListener);
            }
        } else {
            MapFragmentView.isDragged = false;
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
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /**
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                permissions[index])) {
                            Snackbar.make(mapFragmentView, "Required permission " + permissions[index] + " not granted. ", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mapFragmentView, "Required permission " + permissions[index] + " not granted. ", Snackbar.LENGTH_LONG).show();

                        }
                    }
                }

                /**
                 * All permission requests are being handled.Create map fragment view.Please note
                 * the HERE SDK requires all permissions defined above to operate properly.
                 */
                m_mapFragmentView = new MapFragmentView(this);
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onDestroy() {
        m_mapFragmentView.onDestroy();
        super.onDestroy();
    }
}

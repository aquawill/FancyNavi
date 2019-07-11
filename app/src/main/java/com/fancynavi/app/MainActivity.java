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

package com.fancynavi.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.guidance.NavigationManager;

import java.util.ArrayList;
import java.util.List;

import static com.fancynavi.app.MapFragmentView.clearButton;
import static com.fancynavi.app.MapFragmentView.junctionViewImageView;
import static com.fancynavi.app.MapFragmentView.laneMapOverlay;
import static com.fancynavi.app.MapFragmentView.m_map;
import static com.fancynavi.app.MapFragmentView.m_naviControlButton;
import static com.fancynavi.app.MapFragmentView.signpostImageView;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Main activity which launches map view and handles Android run-time requesting permission.
 */

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    static GeoCoordinate updatedLatLng;
    static float lightSensorValue;
    private final SensorEventListener lightSensorListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                lightSensorValue = event.values[0];
            }
        }
    };
    View mapFragmentView;
    Bundle mViewBundle = new Bundle();
    private Button offlineMapButton;
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();

        mapFragmentView = findViewById(R.id.mapFragmentView);
        setContentView(R.layout.activity_main);
        hideGuidanceView();
        hideJunctionView();
        requestPermissions();
        startLocationUpdates();
        SensorManager mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            mySensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
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
        if (!m_mapFragmentView.isRoadView) {
            new ShiftMapCenter(m_map, 0.5f, 0.8f);
            m_map.setTilt(60);
            MapFragmentView.m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
            if (laneMapOverlay != null) {
                m_map.addMapOverlay(laneMapOverlay);
            }
            junctionViewImageView.setAlpha(1f);
            signpostImageView.setAlpha(1f);
            m_naviControlButton.setVisibility(View.GONE);
            clearButton.setVisibility(View.GONE);
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

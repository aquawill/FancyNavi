package com.fancynavi.android.app;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.odml.MapLoader;
import com.here.android.mpa.odml.MapPackage;

import java.util.ArrayList;
import java.util.List;

import static com.fancynavi.android.app.DataHolder.TAG;

class OfflineMapDownloader {
    private final MapLoader mapLoader;
    private final Snackbar offlineDownloadSnackbar;
    private final TextView progressingTextView;
    private final ProgressBar progressBar;
    private final Button cancelButton;
    private final List<Integer> mapIdList = new ArrayList<>();
    private final List<String> mapNameList = new ArrayList<>();
    private final List<String> mapEnglishNameList = new ArrayList<>();
    private int progress = -1;
    private final View v;
    private final ConstraintLayout l;

    private final MapLoader.Listener mapLoaderListener = new MapLoader.Listener() {
        public void onUninstallMapPackagesComplete(MapPackage rootMapPackage,
                                                   MapLoader.ResultCode mapLoaderResultCode) {
            progress = -1;
            darkenAllViews(false);
            DataHolder.getActivity().findViewById(R.id.download_button).setVisibility(View.VISIBLE);
            offlineDownloadSnackbar.setText("Mma");
            Log.d(TAG, "onUninstallMapPackagesComplete");
        }

        public void onProgress(int progressPercentage) {
            progress = progressPercentage;
            Log.d(TAG, "offline map download: " + progressPercentage);

            if (progressPercentage < 100) {
                progressingTextView.setText(String.format("%s%d%%", DataHolder.getAndroidXMapFragment().getString(R.string.downloading), progressPercentage));
                progressBar.setProgress(progressPercentage);
            } else {
                darkenAllViews(false);
                progressingTextView.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                offlineDownloadSnackbar.setText(R.string.download_completed);
                offlineDownloadSnackbar.setAction("", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                offlineDownloadSnackbar.show();
            }
        }

        public void onPerformMapDataUpdateComplete(MapPackage rootMapPackage,
                                                   MapLoader.ResultCode mapLoaderResultCode) {
            progress = -1;
            darkenAllViews(false);
            DataHolder.getActivity().findViewById(R.id.download_button).setVisibility(View.VISIBLE);
            progressingTextView.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            offlineDownloadSnackbar.setText(R.string.download_completed);
            offlineDownloadSnackbar.setAction("", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            offlineDownloadSnackbar.show();
            Log.d(TAG, "onPerformMapDataUpdateComplete");

        }

        public void onInstallationSize(long diskSize, long networkSize) {
            Log.d(TAG, "diskSize: " + diskSize + "/ networkSize: " + networkSize);
        }

        public void onInstallMapPackagesComplete(MapPackage rootMapPackage,
                                                 MapLoader.ResultCode mapLoaderResultCode) {
            progress = -1;
            darkenAllViews(false);
            DataHolder.getActivity().findViewById(R.id.download_button).setVisibility(View.VISIBLE);
            Log.d(TAG, "onInstallMapPackagesComplete");

        }

        public void onGetMapPackagesComplete(MapPackage rootMapPackage,
                                             MapLoader.ResultCode mapLoaderResultCode) {
            Log.d(TAG, "mapLoaderResultCode: " + mapLoaderResultCode.name());
            if (rootMapPackage != null) {
                String rootMapPackageTitle = rootMapPackage.getTitle();
                String rootMapPackageEnglishTitle = rootMapPackage.getEnglishTitle();
                int rootMapPackageEnglishId = rootMapPackage.getId();
                long rootMapPackageSize = rootMapPackage.getSize();
                Log.d(TAG, "Root offline map title:" + rootMapPackageTitle + " | " + rootMapPackageEnglishTitle + " | id: " + rootMapPackageEnglishId + " | size: " + rootMapPackageSize + "KB");
                List<MapPackage> mapPackageLevel1List = rootMapPackage.getChildren();
                for (MapPackage mapPackageLevel1 : mapPackageLevel1List) {
                    String level1MapPackageTitle = mapPackageLevel1.getTitle();
                    String level1MapPackageEnglishTitle = mapPackageLevel1.getEnglishTitle();
                    int level1MapPackageEnglishId = mapPackageLevel1.getId();
                    long level1MapPackageSize = mapPackageLevel1.getSize();
                    Log.d(TAG, "\tL1 offline map title:" + level1MapPackageTitle + " | " + level1MapPackageEnglishTitle + " | id: " + level1MapPackageEnglishId + " | size: " + level1MapPackageSize + "KB");
                    List<MapPackage> mapPackageLevel2List = mapPackageLevel1.getChildren();
                    for (MapPackage mapPackageLevel2 : mapPackageLevel2List) {
                        String level2MapPackageTitle = mapPackageLevel2.getTitle();
                        String level2MapPackageEnglishTitle = mapPackageLevel2.getEnglishTitle();
                        int level2MapPackageEnglishId = mapPackageLevel2.getId();
                        long level2MapPackageEnglishSize = mapPackageLevel2.getSize();
                        Log.d(TAG, "\t\tL2 offline map title:" + level2MapPackageTitle + " | " + level2MapPackageEnglishTitle + " | id: " + level2MapPackageEnglishId + " | size: " + level2MapPackageEnglishSize + "KB");
                    }
                }
            }
        }

        public void onCheckForUpdateComplete(boolean updateAvailable,
                                             String currentMapVersion, String newestMapVersion,
                                             MapLoader.ResultCode mapLoaderResultCode) {
            Log.d(TAG, "updateAvailable: " + updateAvailable);
            Log.d(TAG, "mapLoaderResultCode: " + mapLoaderResultCode.name());
            if (mapLoaderResultCode == MapLoader.ResultCode.OPERATION_SUCCESSFUL) {
                if (updateAvailable) {
                    offlineDownloadSnackbar.setText(DataHolder.getAndroidXMapFragment().getString(R.string.update_available) + currentMapVersion + " --> " + newestMapVersion);
                    offlineDownloadSnackbar.setAction(R.string.update, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progress = 0;
                            progressBar.setProgress(0);
                            mapLoader.addListener(mapLoaderListener);
                            boolean successInstall = mapLoader.performMapDataUpdate();
                            if (successInstall) {
                                darkenAllViews(true);
                                progressingTextView.setVisibility(View.VISIBLE);
                                progressingTextView.setText(R.string.downloading_start);
                                cancelButton.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.VISIBLE);
                            } else {
                                Log.d(TAG, "installMapPackages() failed.");

                            }
                        }
                    });
                    offlineDownloadSnackbar.show();
                } else {
                    offlineDownloadSnackbar.setText(R.string.no_available_map_update);
//                    offlineDownloadSnackbar.setText("No available map update.\nRemove map of " + mapNameList.get(0) + "/" + mapEnglishNameList.get(0) + " ?");
//                    offlineDownloadSnackbar.setAction("REMOVE", new View.OnClickListener(
//
//                    ) {
//                        @Override
//                        public void onClick(View v) {
//                            mapLoader.uninstallMapPackages(mapIdList);
//                        }
//                    });
                    offlineDownloadSnackbar.setAction("", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    offlineDownloadSnackbar.show();
                }
            } else {
                offlineDownloadSnackbar.setText(DataHolder.getAndroidXMapFragment().getString(R.string.error) + mapLoaderResultCode.name());
                offlineDownloadSnackbar.show();
            }

        }
    };

    OfflineMapDownloader() {
//        https://developer.here.com/documentation/android-premium/dev_guide/topics/maps-offline-maploader.html
        mapLoader = MapLoader.getInstance();
        mapLoader.selectDataGroup(MapPackage.SelectableDataGroup.ScooterAttributes);
        mapLoader.selectDataGroup(MapPackage.SelectableDataGroup.TruckAttributes);
        mapLoader.selectDataGroup(MapPackage.SelectableDataGroup.Terrain3D);
        mapLoader.selectDataGroup(MapPackage.SelectableDataGroup.RenderBuildingExt);
        mapLoader.selectDataGroup(MapPackage.SelectableDataGroup.WorldwideExtendedPOI);
        mapLoader.selectDataGroup(MapPackage.SelectableDataGroup.WorldwidePointAddresses);
        mapLoader.selectDataGroup(MapPackage.SelectableDataGroup.ADAS);
        progressBar = DataHolder.getActivity().findViewById(R.id.progress_bar);
        cancelButton = DataHolder.getActivity().findViewById(R.id.cancel_button);
        progressingTextView = DataHolder.getActivity().findViewById(R.id.progressing_text_view);
        offlineDownloadSnackbar = Snackbar.make(DataHolder.getActivity().findViewById(R.id.mapFragmentView), "", Snackbar.LENGTH_LONG);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapLoader.cancelCurrentOperation();
                darkenAllViews(false);
                progressBar.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                progressingTextView.setVisibility(View.GONE);
                DataHolder.getActivity().findViewById(R.id.download_button).setVisibility(View.VISIBLE);
            }
        });
        offlineDownloadSnackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                Log.d(TAG, "" + progress);
                if (progress == -1) {
                    DataHolder.getActivity().findViewById(R.id.download_button).setVisibility(View.VISIBLE);
                }
                super.onDismissed(transientBottomBar, event);

            }

            @Override
            public void onShown(Snackbar transientBottomBar) {
                super.onShown(transientBottomBar);
            }
        });
        mapLoader.addListener(mapLoaderListener);
        boolean success = mapLoader.getMapPackages();
        if (success) {
            Log.d(TAG, "getMapPackages() success.");
        } else {
            Log.d(TAG, "getMapPackages() failed.");
        }
        v = new View(DataHolder.getActivity());
        v.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        v.setBackgroundColor(Color.argb(128, 0, 0, 0));
        v.setElevation(10);
        l = DataHolder.getActivity().findViewById(R.id.map_constraint_layout);
    }

    private void darkenAllViews(boolean bool) {
        Log.d(TAG, "darkenAllViews: " + bool);
        if (bool) {
            l.addView(v);
            DataHolder.getActivity().findViewById(R.id.search_button).setClickable(!bool);
//            DataHolder.getActivity().findViewById(R.id.zoom_out).setClickable(!bool);
//            DataHolder.getActivity().findViewById(R.id.zoom_in).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.traffic_button).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.sat_map_button).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.log_button).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.gps_switch).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.north_up).setClickable(!bool);
            DataHolder.getAndroidXMapFragment().getMapGesture().setAllGesturesEnabled(!bool);
        } else {
            l.removeView(v);
            DataHolder.getActivity().findViewById(R.id.search_button).setClickable(!bool);
//            DataHolder.getActivity().findViewById(R.id.zoom_out).setClickable(!bool);
//            DataHolder.getActivity().findViewById(R.id.zoom_in).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.traffic_button).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.sat_map_button).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.log_button).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.gps_switch).setClickable(!bool);
            DataHolder.getActivity().findViewById(R.id.north_up).setClickable(!bool);
            DataHolder.getAndroidXMapFragment().getMapGesture().setAllGesturesEnabled(!bool);
        }
    }

    void downloadOfflineMapPackageOnMapCenter(GeoCoordinate geoCoordinate) {
        mapLoader.addMapPackageAtCoordinateListener(new MapLoader.MapPackageAtCoordinateListener() {
            @Override
            public void onGetMapPackageAtCoordinateComplete(@Nullable MapPackage mapPackage, @Nullable GeoCoordinate geoCoordinate, MapLoader.ResultCode resultCode) {
                Log.d(TAG, "mapLoaderResultCode: " + resultCode.name());
                mapIdList.clear();
                mapNameList.clear();
                mapEnglishNameList.clear();
                if (resultCode == MapLoader.ResultCode.OPERATION_SUCCESSFUL) {
                    if (mapPackage != null) {
                        String rootMapPackageTitle = mapPackage.getTitle();
                        String rootMapPackageEnglishTitle = mapPackage.getEnglishTitle();
                        int mapPackageId = mapPackage.getId();
                        long mapPackageSize = mapPackage.getSize();
                        mapIdList.add(mapPackageId);
                        mapNameList.add(rootMapPackageTitle);
                        mapEnglishNameList.add(rootMapPackageEnglishTitle);
                        Log.d(TAG, "MapPackageOnMapCenter:" + rootMapPackageTitle + " | " + rootMapPackageEnglishTitle + " | id: " + mapPackageId + " | size: " + mapPackageSize);
                        Log.d(TAG, "mapPackage.getInstallationState(): " + mapPackage.getInstallationState().name());
                        switch (mapPackage.getInstallationState()) {
                            case INSTALLED:
                                offlineDownloadSnackbar.setText(DataHolder.getAndroidXMapFragment().getString(R.string.map_of) + mapNameList.get(0) + "/" + mapEnglishNameList.get(0) + DataHolder.getAndroidXMapFragment().getString(R.string.installed_check_for_updates));
                                offlineDownloadSnackbar.setAction(R.string.check, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        progress = 0;
                                        mapLoader.checkForMapDataUpdate();
                                    }
                                });

                                offlineDownloadSnackbar.show();
                                break;
                            case PARTIALLY_INSTALLED:
                                mapLoader.checkForMapDataUpdate();
                                break;
                            case NOT_INSTALLED:
                                offlineDownloadSnackbar.setText(DataHolder.getAndroidXMapFragment().getString(R.string.download_offline_map_for) + rootMapPackageTitle + "/" + rootMapPackageEnglishTitle + " (" + Math.round((float) mapPackageSize / 1024) + "MB)?");
                                offlineDownloadSnackbar.setAction(R.string.download, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        progress = 0;
                                        progressBar.setProgress(0);
                                        Log.d(TAG, "mapIdsToInstall: " + mapIdList);
                                        boolean successInstall = mapLoader.installMapPackages(mapIdList);
                                        if (successInstall) {
                                            Log.d(TAG, "installMapPackages() success.");
                                            darkenAllViews(true);
                                            progressingTextView.setVisibility(View.VISIBLE);
                                            progressingTextView.setText(R.string.downloading_start);
                                            cancelButton.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.VISIBLE);
                                        } else {
                                            Log.d(TAG, "installMapPackages() failed.");
                                            DataHolder.getActivity().findViewById(R.id.download_button).setVisibility(View.VISIBLE);
                                        }
                                    }
                                });

                                offlineDownloadSnackbar.show();
                                break;
                        }
                    } else {
                        offlineDownloadSnackbar.setText(DataHolder.getAndroidXMapFragment().getString(R.string.no_offline_map_at) + geoCoordinate.getLatitude() + ", " + geoCoordinate.getLongitude());
                        offlineDownloadSnackbar.setAction("", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        offlineDownloadSnackbar.show();
                    }
                } else {
                    offlineDownloadSnackbar.setText(R.string.error + resultCode.name());
                    offlineDownloadSnackbar.setAction("", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    offlineDownloadSnackbar.show();
                }
            }
        });
        boolean success = mapLoader.getMapPackageAtCoordinate(geoCoordinate);
        if (success) {
            Log.d(TAG, "getMapPackages() success.");
        } else {
            Log.d(TAG, "getMapPackages() failed.");
        }
    }
}

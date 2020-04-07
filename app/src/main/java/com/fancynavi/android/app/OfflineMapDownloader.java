package com.fancynavi.android.app;

import android.util.Log;

import com.here.android.mpa.odml.MapLoader;
import com.here.android.mpa.odml.MapPackage;

import java.util.List;

import static com.fancynavi.android.app.DataHolder.TAG;

class OfflineMapDownloader {
    OfflineMapDownloader() {

        MapLoader.Listener mapLoaderListener = new MapLoader.Listener() {
            public void onUninstallMapPackagesComplete(MapPackage rootMapPackage,
                                                       MapLoader.ResultCode mapLoaderResultCode) {
            }

            public void onProgress(int progressPercentage) {
            }

            public void onPerformMapDataUpdateComplete(MapPackage rootMapPackage,
                                                       MapLoader.ResultCode mapLoaderResultCode) {
            }

            public void onInstallationSize(long diskSize, long networkSize) {
            }

            public void onInstallMapPackagesComplete(MapPackage rootMapPackage,
                                                     MapLoader.ResultCode mapLoaderResultCode) {
            }

            public void onGetMapPackagesComplete(MapPackage rootMapPackage,
                                                 MapLoader.ResultCode mapLoaderResultCode) {
                Log.d(TAG, "mapLoaderResultCode: " + mapLoaderResultCode.name());
                String rootMapPackageTitle = rootMapPackage.getTitle();
                String rootMapPackageEnglishTitle = rootMapPackage.getEnglishTitle();
                int rootMapPackageEnglishId = rootMapPackage.getId();
                long rootMapPackageSize = rootMapPackage.getSize();
                Log.d(TAG, "Root offline map title:" + rootMapPackageTitle + " | " + rootMapPackageEnglishTitle + " | id: " + rootMapPackageEnglishId + " | size: " + rootMapPackageSize);
                List<MapPackage> mapPackageLevel1List = rootMapPackage.getChildren();
                for (MapPackage mapPackageLevel1 : mapPackageLevel1List) {
                    String level1MapPackageTitle = mapPackageLevel1.getTitle();
                    String level1MapPackageEnglishTitle = mapPackageLevel1.getEnglishTitle();
                    int level1MapPackageEnglishId = mapPackageLevel1.getId();
                    long level1MapPackageSize = mapPackageLevel1.getSize();
                    Log.d(TAG, "\tL1 offline map title:" + level1MapPackageTitle + " | " + level1MapPackageEnglishTitle + " | id: " + level1MapPackageEnglishId + " | size: " + level1MapPackageSize);
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

            public void onCheckForUpdateComplete(boolean updateAvailable,
                                                 String currentMapVersion, String newestMapVersion,
                                                 MapLoader.ResultCode mapLoaderResultCode) {
            }
        };
        MapLoader mapLoader = MapLoader.getInstance();
        mapLoader.addListener(mapLoaderListener);
        boolean success = mapLoader.getMapPackages();
        if (success) {
            Log.d(TAG, "getMapPackages() success.");
        } else {
            Log.d(TAG, "getMapPackages() failed.");
        }
    }
}

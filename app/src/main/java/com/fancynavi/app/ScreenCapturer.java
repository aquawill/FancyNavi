package com.fancynavi.app;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

class ScreenCapturer {

    private File downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private File outputPath = new File(downloadsPath + File.separator + "SCREENSHOT");

    ScreenCapturer(Bitmap bitmap, AppCompatActivity appCompatActivity) {
        if (!outputPath.exists())
            outputPath.mkdir();
        File file = new File(outputPath + File.separator + "SCREENSHOT_" + System.currentTimeMillis() + ".PNG");
        Log.d("test", file.getPath());
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.d("test", "image saved.");
            Snackbar.make(appCompatActivity.findViewById(R.id.mapFragmentView), file.getAbsolutePath(), Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

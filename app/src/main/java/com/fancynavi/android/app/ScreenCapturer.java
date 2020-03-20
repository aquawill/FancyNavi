package com.fancynavi.android.app;

import android.graphics.Bitmap;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;

class ScreenCapturer {

    ScreenCapturer(Bitmap bitmap, AppCompatActivity appCompatActivity) {
        File downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File outputPath = new File(downloadsPath + File.separator + "SCREENSHOT");
        if (!outputPath.exists())
            outputPath.mkdir();
        File file = new File(outputPath + File.separator + "SCREENSHOT_" + System.currentTimeMillis() + ".PNG");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Snackbar.make(appCompatActivity.findViewById(R.id.mapFragmentView), "Screenshot: " + file.getAbsolutePath(), Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

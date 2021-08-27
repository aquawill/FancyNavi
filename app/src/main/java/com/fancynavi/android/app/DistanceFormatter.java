package com.fancynavi.android.app;

import android.annotation.SuppressLint;

import com.here.msdkui.common.measurements.Converter;

class DistanceFormatter {

    @SuppressLint("DefaultLocale")
    static String formatLengthMetersToMileYard(double meters, int decimalPlaces) {
        double inches = (39.370078 * meters);
        double miles = Converter.round(inches / 63360, decimalPlaces);
        int yard = (int) inches / 36;
        if (miles > 1) {
            return String.format("%.2f %s", miles, DataHolder.getActivity().getResources().getString(R.string.msdkui_unit_mile));
        } else {
            return String.format("%d %s", yard, DataHolder.getActivity().getResources().getString(R.string.msdkui_unit_yard));
        }
    }

    @SuppressLint("DefaultLocale")
    static String formatLengthMetersToKilometers(double meters, int decimalPlaces) {
        if ((meters / 1000) > 1) {
            return String.format("%.2f %s", meters / 1000, DataHolder.getActivity().getResources().getString(R.string.msdkui_unit_kilometer));
        } else {
            return String.format("%d %s", (int) meters, DataHolder.getActivity().getResources().getString(R.string.msdkui_unit_meter));
        }
    }
}

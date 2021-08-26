package com.fancynavi.android.app;

import java.util.Locale;

class UnitLocale {
    static UnitLocale Imperial = new UnitLocale();
    static UnitLocale ImperialUs = new UnitLocale();
    static UnitLocale Metric = new UnitLocale();

    static UnitLocale getDefault() {
        return getFrom(Locale.getDefault());
    }

    static UnitLocale getFrom(Locale locale) {
        String countryCode = locale.getCountry();
        if ("US".equals(countryCode)) {
            return ImperialUs; // USA
        }
        if ("LR".equals(countryCode)) {
            return Imperial; // Liberia
        }
        if ("MM".equals(countryCode)) {
            return Imperial; // Myanmar
        }
        return Metric;
    }
}

// https://stackoverflow.com/questions/4898237/using-locale-settings-to-detect-wheter-to-use-imperial-units
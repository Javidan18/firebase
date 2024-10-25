package com.example.sagliktakipandroid;

import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import java.util.Locale;

public class YourApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setLocale();
    }

    private void setLocale() {
        Locale locale = Locale.getDefault();
        Locale.setDefault(locale);
        Configuration config = new Configuration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(new LocaleList(locale));
        } else {
            config.locale = locale;
        }

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}

package com.orchidatech.askandanswer.Application;

import android.content.SharedPreferences;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orchidatech.askandanswer.Constant.Constants;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        ActiveAndroid.initialize(this);


    }
}

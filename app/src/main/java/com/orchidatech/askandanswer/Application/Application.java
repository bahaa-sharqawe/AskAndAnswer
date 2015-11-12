package com.orchidatech.askandanswer.Application;

import android.content.SharedPreferences;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orchidatech.askandanswer.Constant.Constants;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class Application extends android.app.Application {
    private static final String APP_ID = "";
    private static final String APP_NAMESPACE = "";
    private static final Permission[] permissions = new Permission[] {
            Permission.USER_PHOTOS,
            Permission.EMAIL,
            Permission.PUBLISH_ACTION
    };;
    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        ActiveAndroid.initialize(this);

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(APP_ID)
                .setNamespace(APP_NAMESPACE)
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
    }
}

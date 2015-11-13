package com.orchidatech.askandanswer.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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

        Configuration.Builder builder = new Configuration.Builder(getBaseContext());
//        builder.setCacheSize(1024*1024*4);
        builder.setDatabaseName("AskAndAnswer.db");
        builder.setDatabaseVersion(1);
        ActiveAndroid.initialize(builder.create(), true);
//        ActiveAndroid.initialize(this);

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(APP_ID)
                .setNamespace(APP_NAMESPACE)
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}

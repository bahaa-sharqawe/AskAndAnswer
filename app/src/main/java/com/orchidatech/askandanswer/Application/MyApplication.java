package com.orchidatech.askandanswer.Application;

import android.app.ActivityManager;

import com.activeandroid.ActiveAndroid;
import com.alexbbb.uploadservice.UploadService;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Logic.LollipopBitmapMemoryCacheParamsSupplier;


/**
 * Created by Bahaa on 7/11/2015.
 */
public class MyApplication extends com.activeandroid.app.Application {
    private static final String APP_ID = "446821295503912";
    private static final String APP_NAMESPACE = "orchaskandanswer";
//    private static final Permission[] permissions = new Permission[]{
//            Permission.USER_PHOTOS,
//            Permission.EMAIL,
//            Permission.PUBLISH_ACTION,
//            Permission.USER_ABOUT_ME
//    };
//    ;

    @Override
    public void onCreate() {
        super.onCreate();
        initFresco();
        ActiveAndroid.initialize(this);

//        Fresco.initialize(getApplicationContext());
        ImageLoaderConfiguration  config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LruMemoryCache(GNLConstants.MAX_IMAGE_LOADER_CACH_SIZE)).build();
                ImageLoader.getInstance().init(config);

//        Configuration.Builder builder = new Configuration.Builder(getBaseContext());
////        builder.setCacheSize(1024*1024*4);
//        builder.setDatabaseName("AskAndAnswer.db");
//        builder.setDatabaseVersion(1);
//        ActiveAndroid.initialize(builder.create());
//
//        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
//                .setAppId(APP_ID)
//                .setNamespace(APP_NAMESPACE)
//                .setPermissions(permissions)
//                .build();
//        SimpleFacebook.setConfiguration(configuration);
        UploadService.NAMESPACE = "com.orchidatech.askandanswer";

        AjaxCallback.setNetworkLimit(8);
        BitmapAjaxCallback.setIconCacheLimit(50);
        BitmapAjaxCallback.setCacheLimit(50);
    }
//
//
//    @Override
//    public void onTerminate() {
//        super.onTerminate();
//        ActiveAndroid.dispose();
//    }
private void initFresco() {
    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
            .newBuilder(getApplicationContext())
            .setBitmapMemoryCacheParamsSupplier(new LollipopBitmapMemoryCacheParamsSupplier(activityManager))
            .build();

    Fresco.initialize(getApplicationContext(), imagePipelineConfig);
}
}

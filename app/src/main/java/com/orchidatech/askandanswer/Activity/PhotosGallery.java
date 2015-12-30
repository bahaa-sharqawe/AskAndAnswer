package com.orchidatech.askandanswer.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orchidatech.askandanswer.Logic.HackyViewPager;
import com.orchidatech.askandanswer.Logic.LollipopBitmapMemoryCacheParamsSupplier;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Adapter.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Bahaa on 25/12/2015.
 */
public class PhotosGallery extends Activity {
    public static final String IMAGE_URL = "image_url";
    private String image_url;
    private AlertDialog dialog;
    private HackyViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(getApplicationContext());
        initFresco();
        setContentView(R.layout.gallery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        image_url = getIntent().getStringExtra(IMAGE_URL);
        viewPager = (HackyViewPager) this.findViewById(R.id.view_pager);
        ArrayList<String> imagesUrl = new ArrayList<>();
        imagesUrl.add(image_url);
        viewPager.setAdapter(new ViewPagerAdapter(this, imagesUrl));
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        dialog = builder.create();
////        dialog.getWindow().setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.loading_fragment_backgnd));
//        dialog.setView(getCustomView(), 0, 0, 0, 0);
//        dialog.setCanceledOnTouchOutside(true);
//        return dialog;
//    }
//    private View getCustomView() {
//        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.gallery, null, false);
//        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
//        ArrayList<String> imagesUrl = new ArrayList<>();
//        imagesUrl.add(image_url);
//        viewPager.setAdapter(new ViewPagerAdapter(getActivity(), imagesUrl));
//        return view;
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

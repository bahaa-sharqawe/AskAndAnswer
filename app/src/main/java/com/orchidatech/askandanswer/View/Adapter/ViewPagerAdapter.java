package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Bahaa on 25/12/2015.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private final AQuery aq;
    ArrayList<String> images;
    Context context;

    public ViewPagerAdapter(Context context, ArrayList<String> images) {
        this.images = images;
        this.context = context;
        aq = new AQuery(context);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);
        Bitmap preset = aq.getCachedImage(images.get(position));
        aq.id(photoView).image(images.get(position), true, true, 0, 0, preset, AQuery.FADE_IN);
//        Picasso.with(context).load(images.get(position)).into(photoView);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

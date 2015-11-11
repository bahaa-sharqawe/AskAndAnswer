package com.orchidatech.askandanswer.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.orchidatech.askandanswer.R;

public class TermsFragment extends Fragment {
    ImageView iv_logo;
    ImageView iv_drawer;
    OnDrawerIconClickListener listener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null && activity instanceof OnDrawerIconClickListener)
            listener = (OnDrawerIconClickListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_terms, null , false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iv_logo = (ImageView) getActivity().findViewById(R.id.iv_logo);
        resizeLogo();
        iv_drawer = (ImageView) getActivity().findViewById(R.id.iv_drawer);
        iv_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        iv_logo.getLayoutParams().height = (int)(screenSize.y*0.25);
        iv_logo.getLayoutParams().width = (int)(screenSize.y*0.25);
    }
    public interface OnDrawerIconClickListener{
        public void onClick();
    }

}

package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.orchidatech.askandanswer.R;

public class TermsFragment extends Fragment {
    ImageView iv_logo;

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

    }

    private void resizeLogo() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getSize(screenSize); // store size in screenSize
        iv_logo.getLayoutParams().height = (int)(screenSize.y*0.25);
        iv_logo.getLayoutParams().width = (int)(screenSize.y*0.25);
    }

}

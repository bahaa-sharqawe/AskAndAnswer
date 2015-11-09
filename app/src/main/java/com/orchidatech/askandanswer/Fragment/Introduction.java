package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Utils.FontManager;

/**
 * Created by Bahaa on 5/11/2015.
 */
public class Introduction extends Fragment {
        TextView tv_intro;
        TextView tv_intro_content;

    FontManager mFontManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_introduction, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_intro = (TextView) getActivity().findViewById(R.id.tv_intro);
        tv_intro_content = (TextView) getActivity().findViewById(R.id.tv_intro_content);
        mFontManager = FontManager.getInstance(getActivity().getAssets());
        tv_intro.setTypeface(mFontManager.getFont("roboto_light"));
        tv_intro_content.setTypeface(mFontManager.getFont("roboto_light"));

    }
}

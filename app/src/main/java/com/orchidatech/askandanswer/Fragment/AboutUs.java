package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.R;

/**
 * Created by Bahaa on 5/11/2015.
 */
public class AboutUs extends Fragment {
    TextView tv_intro;
    TextView tv_contact;
    RelativeLayout rl_contact;
    RelativeLayout rl_intro;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aboutus, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rl_contact = (RelativeLayout) getActivity().findViewById(R.id.rl_contact);
        rl_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContactUs().show(getFragmentManager(), getActivity().getResources().getString(R.string.contactus_small));

            }
        });
        rl_intro = (RelativeLayout) getActivity().findViewById(R.id.rl_intro);
        rl_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainScreen.oldPosition = -1;
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new Introduction()).commit();
            }
        });
    }
}

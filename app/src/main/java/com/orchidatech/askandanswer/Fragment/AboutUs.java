package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orchidatech.askandanswer.Activity.MainScreen;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Utils.FontManager;

/**
 * Created by Bahaa on 5/11/2015.
 */
public class AboutUs extends Fragment {
    TextView tv_intro;
    TextView tv_contact;
    RelativeLayout rl_contact;
    RelativeLayout rl_intro;
    private FontManager fontManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aboutus, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar();
        fontManager = FontManager.getInstance(getActivity().getAssets());
        tv_intro = (TextView) getActivity().findViewById(R.id.tv_intro);
        tv_intro.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        tv_contact = (TextView) getActivity().findViewById(R.id.tv_contact);
        tv_contact.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

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
                getFragmentManager().beginTransaction().replace(R.id.fragment_host, new Introduction()).addToBackStack("").commit();
                getFragmentManager().executePendingTransactions();
            }
        });
    }
    private void setActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("About");
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ( getActivity().findViewById(R.id.ed_search)).setVisibility(View.GONE);
        (getActivity(). findViewById(R.id.rl_num_notifications)).setVisibility(View.GONE);
    }
}

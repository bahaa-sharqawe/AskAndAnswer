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
 * Created by Bahaa on 28/12/2015.
 */
public class Feedback extends Fragment{
    TextView tv_feedback;
    RelativeLayout rl_feed;
    private FontManager fontManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar();
        fontManager = FontManager.getInstance(getActivity().getAssets());

        tv_feedback = (TextView) getActivity().findViewById(R.id.tv_feedback);
        tv_feedback.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        rl_feed = (RelativeLayout) getActivity().findViewById(R.id.rl_feed);

        rl_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUs contactUs = new ContactUs();
                Bundle args = new Bundle();
                args.putString(ContactUs.DIALOG_TITLE, getActivity().getResources().getString(R.string.suggest_us));
                contactUs.setArguments(args);
                contactUs.show(getFragmentManager(), getActivity().getResources().getString(R.string.contactus_small));

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

package com.orchidatech.askandanswer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.orchidatech.askandanswer.R;

/**
 * Created by Bahaa on 6/11/2015.
 */
public class Settings extends Fragment {
    Switch sw_notification;
    ImageView iv_checkbox;
    ImageView iv_checked;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar();
        sw_notification = (Switch) getActivity().findViewById(R.id.sw_notification);
        sw_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //update in server then
                //   SettingsDAO.updateSettings(1, GNLConstants.Settings_Keys.NOTIFICATIONS_SOUND, isChecked?1:0);
            }
        });
        iv_checkbox = (ImageView) getActivity().findViewById(R.id.iv_checkbox);
        iv_checked = (ImageView) getActivity().findViewById(R.id.iv_checked);
        iv_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                iv_checked.setVisibility(View.VISIBLE);
            }
        });
        iv_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                iv_checkbox.setVisibility(View.VISIBLE);
            }
        });
    }
    private void setActionBar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Settings");
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ( getActivity().findViewById(R.id.ed_search)).setVisibility(View.GONE);
        (getActivity(). findViewById(R.id.rl_num_notifications)).setVisibility(View.GONE);
    }
}

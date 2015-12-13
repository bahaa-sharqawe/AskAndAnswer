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
import android.widget.TextView;

import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Utils.FontManager;

import org.w3c.dom.Text;

/**
 * Created by Bahaa on 6/11/2015.
 */
public class Settings extends Fragment {
    Switch sw_notification;
    TextView tv_notify_setting_title;
   TextView tv_notify_setting_desc;
    private FontManager fontManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar();
        fontManager = FontManager.getInstance(getActivity().getAssets());
        tv_notify_setting_desc = (TextView) getActivity().findViewById(R.id.tv_notify_setting_desc);
        tv_notify_setting_desc.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        tv_notify_setting_title = (TextView) getActivity().findViewById(R.id.tv_notify_setting_title);
        tv_notify_setting_title.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

        sw_notification = (Switch) getActivity().findViewById(R.id.sw_notification);
        sw_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //update in server then
                //   SettingsDAO.updateSettings(1, GNLConstants.Settings_Keys.NOTIFICATIONS_SOUND, isChecked?1:0);
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

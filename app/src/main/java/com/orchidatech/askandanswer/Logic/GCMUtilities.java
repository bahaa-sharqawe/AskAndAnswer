package com.orchidatech.askandanswer.Logic;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orchidatech.askandanswer.View.Interface.OnGCMRegisterListener;
import com.orchidatech.askandanswer.View.Interface.OnRegisterListener;

import java.io.IOException;

/**
 * Created by Bahaa on 9/12/2015.
 */
public class GCMUtilities {
    private static final int MAX_ATTEMPTS = 5;
    OnGCMRegisterListener listener;
    String sender_id;
    Context context;
    private GoogleCloudMessaging gcm;

    public GCMUtilities(Context context, String sender_id, OnGCMRegisterListener listener) {
        this.context = context;
        this.listener = listener;
        this.sender_id = sender_id;
    }

    public void register() {
                new RegisterWithGCM().execute();
    }

    private class RegisterWithGCM extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                if(gcm == null)
                gcm = GoogleCloudMessaging.getInstance(context);
                try {
                   String regid = gcm.register(sender_id);
//                receiver_reg_id = regid;
//                pubnub.enablePushNotificationsOnChannel("Channel-qy6nf256d", regid);
                    return regid;


                } catch (IOException e) {
                    if(i == MAX_ATTEMPTS)
                        break;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

    if(!TextUtils.isEmpty(s))
        listener.OnRegistered(s);
    else
        listener.onFail();
    //            Toast.makeText(getApplicationContext(), regid, Toast.LENGTH_LONG).show();
        }
    }
}

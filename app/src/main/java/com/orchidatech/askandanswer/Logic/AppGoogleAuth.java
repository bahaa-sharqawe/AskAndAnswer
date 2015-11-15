package com.orchidatech.askandanswer.Logic;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.orchidatech.askandanswer.Entity.SocialUser;
import com.orchidatech.askandanswer.View.Adapter.OnSocialLoggedListener;

/**
 * Created by Bahaa on 15/11/2015.
 */
public class AppGoogleAuth  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public GoogleApiClient mGoogleApiClient;
    public boolean mIntentInProgress;
    public final int RC_SIGN_IN = 0;
    public boolean signedInUser;
    public Activity context;
    OnSocialLoggedListener listener;
    public ConnectionResult mConnectionResult;
    public AppGoogleAuth(Activity context) {
        this.context=context;

        mGoogleApiClient = new GoogleApiClient.Builder(context).
                addConnectionCallbacks(this).addOnConnectionFailedListener(this).
                addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        signedInUser = false;
        getProfileInformation();
        //Toast.makeText(context, "Connected !", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), context, 0).show();
            return;
        }
        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = connectionResult;
            if (signedInUser) {
                resolveSignInError();
            }
        }

    }
    public void resolveSignInError() {
        //  Toast.makeText(context, "Failed"+"\n", Toast.LENGTH_LONG).show();
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(context, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    public String getProfileInformation() {
        Log.d("jaffer","tst info");
        String result="";
        try {
            Log.d("jaffer", "tst info");
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Log.d("jaffer", "tst info");
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                // new LoadProfileImage(image).execute(personPhotoUrl);
                result = personName+" \n"+personPhotoUrl+" \n"+email;
                final SocialUser socialUser = new SocialUser();
                socialUser.name = personName;
                socialUser.avatarURL = personPhotoUrl;
                socialUser.network = SocialUser.NetworkType.GOOGLEPLUS;
                socialUser.email = email;
                Log.d("jaffer email", email);

                listener.onSuccess(socialUser);
            }
            else
                Log.d("jaffer", "else");
        } catch (Exception e) {
            Log.d("jaffer","exc");
            e.printStackTrace();
        }
        return result;
    }
    public void googlePlusLogin(OnSocialLoggedListener listener)
    {      this.listener=listener;

        if (!mGoogleApiClient.isConnected()) {
            signedInUser = true;
            resolveSignInError();
        }
    }
    public void googlePlusLogout() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            // mGoogleApiClient.connect();
            // Toast.makeText(context, "DisConnect GOOGLE +", Toast.LENGTH_SHORT).show();

        }
    }


}


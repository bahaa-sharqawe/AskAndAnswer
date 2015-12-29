package com.orchidatech.askandanswer.Logic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.orchidatech.askandanswer.Entity.SocialUser;
import com.orchidatech.askandanswer.View.Interface.OnSocialLoggedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Bahaa on 26/12/2015.
 */
public class AppFacebookAuth {
    private CallbackManager mCallbackManager;
    Activity activity;
    ArrayList<String> permissions = new ArrayList<>(Arrays.asList("public_profile", "email"));
    OnSocialLoggedListener listener;

    public AppFacebookAuth(Activity activity, OnSocialLoggedListener listener) {
        this.activity = activity;
        this.listener = listener;
        FacebookSdk.sdkInitialize(activity);
        mCallbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().setLoginBehavior( LoginBehavior.SUPPRESS_SSO );
        loginWithFacebook();

    }

    private void loginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestData();
            }

            @Override
            public void onCancel() {
            Toast.makeText(activity.getApplicationContext(), "CANCELLED", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(activity.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void requestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                try {
                    if(json != null){
                        String text = "Name : "+json.getString("name")+"\n Email : "+json.optString("email");
                        String str_firstname = json.getString("first_name");
                        String str_lastname = json.getString("last_name");
           //             Toast.makeText(activity, text + "\n" + str_firstname + " " + str_lastname, Toast.LENGTH_LONG).show();
                        Log.i("responsevcvcxvxc",  response.toString());
                        final SocialUser socialUser = new SocialUser();
                        socialUser.name = json.getString("name");
                        socialUser.avatarURL = "https://graph.facebook.com/" + json.optString("id") + "/picture?width=300&height=300";
                        socialUser.network = SocialUser.NetworkType.FACEBOOK;
                        socialUser.email = json.optString("email");
                        socialUser.fname = json.getString("first_name");
                        socialUser.lname = json.getString("last_name");

                        listener.onSuccess(socialUser);
                    }

                } catch (JSONException e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }
    public CallbackManager getCM(){
        return mCallbackManager;
    }
}

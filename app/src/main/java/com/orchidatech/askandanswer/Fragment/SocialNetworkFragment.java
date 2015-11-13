package com.orchidatech.askandanswer.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.googleplus.GooglePlusSocialNetwork;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class SocialNetworkFragment extends Fragment {
    private static final String SOCIAL_NETWORK_TAG = "GooglePlusSocialNetwork";
    private SocialNetworkManager mSocialNetworkManager;
    private GooglePlusSocialNetwork gPlusNetwork;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
      if(mSocialNetworkManager == null){
          mSocialNetworkManager = new SocialNetworkManager();
          gPlusNetwork = new GooglePlusSocialNetwork(this);
          mSocialNetworkManager.addSocialNetwork(gPlusNetwork);
          getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
          mSocialNetworkManager.getSocialNetwork(gPlusNetwork.getID()).requestLogin(new OnLoginCompleteListener() {
              @Override
              public void onLoginSuccess(int socialNetworkID) {
                    getProfile(gPlusNetwork.getID());
              }

              @Override
              public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {

              }
          });

      }

}
    private void getProfile(int id){
        mSocialNetworkManager.getSocialNetwork(id).requestCurrentPerson(new OnRequestSocialPersonCompleteListener() {
            @Override
            public void onRequestSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson) {
            }

            @Override
            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {

            }
        });

    }
}

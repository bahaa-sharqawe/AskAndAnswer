package com.orchidatech.askandanswer.View.Utils;

import android.content.Context;

import com.orchidatech.askandanswer.Constant.URL;
import com.orchidatech.askandanswer.View.Interface.OnCategoriesFetchedListener;
import com.orchidatech.askandanswer.View.Interface.OnLoadFinished;
import com.orchidatech.askandanswer.View.Interface.OnLoginListener;
import com.orchidatech.askandanswer.View.Interface.OnRegisterListener;
import com.orchidatech.askandanswer.WebService.Operations;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class WebServiceFunctions {

    public static void login(Context context, String username, String password, final OnLoginListener listener){
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.USERNAME, username);
        params.put(URL.URLParameters.PASSWORD, password);

        Operations.getInstance(context).login(params, new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                ///Check o content then decide success or fail..
                //if user not found then listener.onFail(context.getString(R.string.BR_LOGIN_004))
                //if username or password incorrect then listener.onFail(context.getString(R.string.BR_LOGIN_003))
                //else listener.onSuccess()
                listener.onSuccess();
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });
    }
    public static void register(Context context, String fname, String lname, String email, String password, final OnRegisterListener listener){
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.FNAME, fname);
        params.put(URL.URLParameters.LNAME, lname);
        params.put(URL.URLParameters.EMAIL, email);
        params.put(URL.URLParameters.PASSWORD, password);

        Operations.getInstance(context).register(params, new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                ///Check o content then decide success or fail..
                //if user already exists then listener.onFail(context.getString(R.string.BR_SIGN_004))
                //else listener.onSuccess()
                listener.onSuccess();
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });
    }
    public static void getPosts(Context context) {
        Operations.getInstance(context).getPosts(new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                ////store in localDB
            }

            @Override
            public void onFail(String error) {

            }
        });
    }
    public static void getCategories(Context contxt, final OnCategoriesFetchedListener listener){
        Operations.getInstance(contxt).getCategories(new OnLoadFinished(){

            @Override
            public void onSuccess(JSONObject o) {
                //store in localDB
                ///parsing o to fetch all categories
                listener.onSuccess(null);
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });

    }
}

package com.orchidatech.askandanswer.WebService;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orchidatech.askandanswer.Constant.URL;
import com.orchidatech.askandanswer.Constant.URLParameters;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnLoadFinished;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by Bahaa on 13/11/2015.
 */
public class Operations {

    private static Operations instance;
    private Context context;

    private Operations(Context context) {
        this.context = context;
    }

    public static synchronized Operations getInstance(Context context) {
        if (instance == null)
            instance = new Operations(context);
        return instance;
    }

    public void login(Map<String, String> params, final OnLoadFinished onLoadFinished) {
        String url = URL.LOGIN + "?" + URLParameters.USERNAME + "="+ encode(params.get(URLParameters.USERNAME)) + "&" + URLParameters.PASSWORD + "=" + encode(params.get(URLParameters.PASSWORD));
        sendRequest(url, onLoadFinished);

    }
    public void getPosts(final OnLoadFinished listener){
        sendRequest(URL.GET_POSTS, listener);
    }


    public void getCategories(OnLoadFinished listener) {
        sendRequest(URL.GET_CATEGORIES, listener);
    }

    private String encode(String s) {
        return Uri.encode(s, "utf-8");
    }

    private void sendRequest(final String url, final OnLoadFinished listener) {
        OperationsManager.getInstance(context).sendRequest(url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject o) {
                        listener.onSuccess(o);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        listener.onFail(context.getString(R.string.BR_GNL_001));
                    }
                });
    }

    public void register(Map<String, String> params, final OnLoadFinished listener) {
        OperationsManager.getInstance(context).sendRequest(URL.REGISTER, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject o) {
                        listener.onSuccess(o);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        listener.onFail(context.getString(R.string.BR_GNL_001));
                    }
                });

    }
}

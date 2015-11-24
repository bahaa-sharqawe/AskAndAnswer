package com.orchidatech.askandanswer.WebService;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class OperationsManager {

    private static OperationsManager instance;
    private Context context;

    private OperationsManager(Context context) {
        this.context = context;
    }

    public static synchronized OperationsManager getInstance(Context context) {
        if (instance == null) {
            instance = new OperationsManager(context);
        }
        return instance;
    }

    public void sendRequest(int method, String url, Map<String, String> params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        MyRequest mRequest = new MyRequest(method, url, listener, errorListener, params);
        RequestQueueHandler.getInstance(context).addToRequestQueue(mRequest);
//
//        JsonObjectRequest mRequest = new JsonObjectRequest(url, null, listener, errorListener);
//        RequestQueueHandler.getInstance(context).addToRequestQueue(mRequest);
    }
}

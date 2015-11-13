package com.orchidatech.askandanswer.WebService;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class RequestQueueHandler {

    private static RequestQueueHandler instance;
    private RequestQueue requestQueue;
    private static Context mContext;

    private RequestQueueHandler() {
        requestQueue = Volley.newRequestQueue(mContext);
    }

    public static synchronized RequestQueueHandler getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new RequestQueueHandler();
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        requestQueue.add(req);
    }
//    public void addToRequestQueue(MyRequest req) {
//        requestQueue.add(req);
//    }
}

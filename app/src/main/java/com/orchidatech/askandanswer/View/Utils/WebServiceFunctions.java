package com.orchidatech.askandanswer.View.Utils;

import android.content.Context;

import com.orchidatech.askandanswer.View.Interface.OnLoadFinished;
import com.orchidatech.askandanswer.WebService.Operations;

import org.json.JSONObject;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class WebServiceFunctions {

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
    public static void getCategories(Context contxt){
        Operations.getInstance(contxt).getCategories(new OnLoadFinished(){

            @Override
            public void onSuccess(JSONObject o) {
                //store in localDB
            }

            @Override
            public void onFail(String error) {

            }
        });

    }
}

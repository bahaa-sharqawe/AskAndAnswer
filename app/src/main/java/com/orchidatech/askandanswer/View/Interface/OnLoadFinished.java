package com.orchidatech.askandanswer.View.Interface;

import org.json.JSONObject;

/**
 * Created by Bahaa on 13/11/2015.
 */
public interface OnLoadFinished {
    void onSuccess(String response);
    void onFail(String error);
}

package com.orchidatech.askandanswer.View.Interface;

/**
 * Created by Bahaa on 22/11/2015.
 */
public interface OnUploadImageListener {
    void onSuccess(String serverResponseMessage);
    void onFail(String error);
}

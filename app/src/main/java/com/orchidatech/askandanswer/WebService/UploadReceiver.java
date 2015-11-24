package com.orchidatech.askandanswer.WebService;

import android.content.Context;

import com.alexbbb.uploadservice.AbstractUploadServiceReceiver;
import com.orchidatech.askandanswer.View.Interface.OnUploadImageListener;

/**
 * Created by Bahaa on 22/11/2015.
 */
public class UploadReceiver extends AbstractUploadServiceReceiver {

    private Context context;
    private OnUploadImageListener listener;

    UploadReceiver(Context context, OnUploadImageListener listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onProgress(String uploadId, int progress) {

//            Log.i(TAG, "The progress of the upload with ID " + uploadId + " is: " + progress);
    }

    @Override
    public void onError(String uploadId, Exception exception) {
        listener.onFail(exception.getLocalizedMessage());
//            Log.e(TAG, "Error in upload with ID: " + uploadId + ". "
//                    + exception.getLocalizedMessage(), exception);
    }

    @Override
    public void onCompleted(String uploadId, int serverResponseCode, String serverResponseMessage) {
        this.unregister(context);
        listener.onSuccess(serverResponseMessage);
//            progressBar.setProgress(0);
//
//            Log.i(TAG, "Upload with ID " + uploadId + " is completed: " + serverResponseCode + ", "
//                    + serverResponseMessage);
    }
};
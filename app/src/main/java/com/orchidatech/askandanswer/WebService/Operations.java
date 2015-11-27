package com.orchidatech.askandanswer.WebService;

import android.content.Context;
import android.util.Log;

import com.alexbbb.uploadservice.MultipartUploadRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orchidatech.askandanswer.Constant.URL;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnLoadFinished;
import com.orchidatech.askandanswer.View.Interface.OnUploadImageListener;

import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;


/**
 * Created by Bahaa on 13/11/2015.
 */
public class Operations {
    //0 get  1 post
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

    public void register(Map<String, String> params, final OnLoadFinished listener) {
        Log.i("gfgfdgfdgfdgfdgfdgfdg", URL.REGISTER);
        sendRequest(Request.Method.POST, URL.REGISTER, params, listener);
    }

    public void login(Map<String, String> params, final OnLoadFinished listener) {
        sendRequest(Request.Method.POST, URL.LOGIN, params, listener);

    }

    public void getUserInfo(OnLoadFinished listener, String url) {
        sendRequest(Request.Method.GET, url, null, listener);
    }

    public void getUserPosts(final OnLoadFinished listener, String url) {
        sendRequest(Request.Method.GET, url, null, listener);
    }

    public void getTimeLine(OnLoadFinished listener, String url) {
        sendRequest(Request.Method.GET, url, null, listener);
    }

    public void getUserComments(OnLoadFinished listener, String url) {
        sendRequest(Request.Method.GET, url, null, listener);
    }

    public void getPostComments(OnLoadFinished listener, String url) {
        sendRequest(Request.Method.GET, url, null, listener);

    }
    public void getUserFavPosts(OnLoadFinished listener, String url) {
        sendRequest(Request.Method.GET, url, null, listener);
    }

    public void getCategories(OnLoadFinished listener) {
        Log.i("gfgfdgfdgfdgfdgfdgfdg", URL.GET_CATEGORIES);
        sendRequest(Request.Method.GET, URL.GET_CATEGORIES, null, listener);
    }

    public void addComment(OnLoadFinished listener, String url) {
        sendRequest(Request.Method.GET, url, null, listener);
    }

    public void getUserCategories(String url, OnLoadFinished listener) {
        sendRequest(Request.Method.GET, url, null, listener);
    }
    public void sendUserCategories(Map<String, String> params, OnLoadFinished listener) {
        sendRequest(Request.Method.POST, URL.SEND_USER_CATEGORIES, params, listener);
    }

    public void deletePost(Map<String, String> params, OnLoadFinished listener) {
        sendRequest(Request.Method.POST, URL.DELETE_POST, params, listener);
    }

    public void search(OnLoadFinished listener, String url) {
        sendRequest(Request.Method.GET, url, null, listener);
    }
    public void addPost(Context context, long user_id, long category_id, String text, String picturePath, long date, int is_hidden, OnUploadImageListener listener) {

        UploadReceiver uploadReceiver = new UploadReceiver(context, listener);
        uploadReceiver.register(context);
        MultipartUploadRequest request = new MultipartUploadRequest(context, UUID.randomUUID().toString(), URL.ADD_POST);
        request.addFileToUpload(picturePath, URL.URLParameters.IMAGE, new File(picturePath).getName(), null);
        request.addParameter(URL.URLParameters.USER_ID, user_id + "");
        request.addParameter(URL.URLParameters.CATEGORIES_ID, category_id + "");
        request.addParameter(URL.URLParameters.TEXT, text);
        request.addParameter(URL.URLParameters.DATE, date + "");
        request.addParameter(URL.URLParameters.IS_HIDDEN, is_hidden + "");
        request.setMethod("POST");
        try {
            request.startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void editPost(Context context, long post_id, long user_id, long category_id, String text, String picturePath, long date, int isHidden, OnUploadImageListener listener) {
        UploadReceiver uploadReceiver = new UploadReceiver(context, listener);
        uploadReceiver.register(context);
        MultipartUploadRequest request = new MultipartUploadRequest(context, UUID.randomUUID().toString(), URL.EDIT_POST);
        if (picturePath != null)
            request.addFileToUpload(picturePath, URL.URLParameters.IMAGE, new File(picturePath).getName(), null);
        request.addParameter(URL.URLParameters.POST_ID, post_id + "");
        request.addParameter(URL.URLParameters.USER_ID, user_id + "");
        request.addParameter(URL.URLParameters.CATEGORIES_ID, category_id + "");
        request.addParameter(URL.URLParameters.TEXT, text);
        request.addParameter(URL.URLParameters.DATE, date + "");
        request.addParameter(URL.URLParameters.IS_HIDDEN, isHidden + "");
        request.setMethod("POST");
        try {
            request.startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public void updateProfile(long id, String fname, String lname, String password, String picturePath, ArrayList<Category> selectedCategories, OnUploadImageListener listener) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedCategories.size(); i++)
            sb.append(selectedCategories.get(i).getServerID()).append(i != selectedCategories.size() - 1 ? "," : "");
        UploadReceiver uploadReceiver = new UploadReceiver(context, listener);
        uploadReceiver.register(context);
        MultipartUploadRequest request = new MultipartUploadRequest(context, UUID.randomUUID().toString(), URL.UPDATE_PROFILE);
        if (picturePath != null)
            request.addFileToUpload(picturePath, URL.URLParameters.IMAGE, new File(picturePath).getName(), null);
        request.addParameter(URL.URLParameters.ID, id + "");
        request.addParameter(URL.URLParameters.FNAME, fname);
        request.addParameter(URL.URLParameters.LNAME, lname);
        request.addParameter(URL.URLParameters.PASSWORD, password);
        request.addParameter(URL.URLParameters.CATEGORIES_ID, sb.toString());

        request.setMethod("POST");
        try {
            request.startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void addPostFavorite(Map<String, String> params, OnLoadFinished listener) {
        sendRequest(Request.Method.POST, URL.ADD_POST_FAVORITE, params, listener);
    }

    public void removePostFavorite(Map<String, String> params, OnLoadFinished listener) {
        sendRequest(Request.Method.POST, URL.REMOVE_POST_FAVORITE, params, listener);
    }

    private void sendRequest(int method, final String url, Map<String, String> params, final OnLoadFinished listener) {
        OperationsManager.getInstance(context).sendRequest(method, url, params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String o) {
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

package com.orchidatech.askandanswer.WebService;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.orchidatech.askandanswer.View.Interface.OnLoadFinished;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by Bahaa on 20/11/2015.
 */
public class UploadImage {
    private Context context;
    private OnLoadFinished listener;
    private AndroidMultiPartEntity entity;
    String url;
    private UploadToServer uploadToServer;

    public UploadImage(Context context, String url, OnLoadFinished listener) {
        this.context = context;
        this.url = url;
        this.listener = listener;
        uploadToServer = new UploadToServer();
        entity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
            @Override
            public void transferred(long num) {

            }
        });
    }

    public void sendRequest() {
        uploadToServer.execute();

    }

    public void addStringProperty(String key, String value) {
        try {
            entity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void addFileProperty(String key, String filePath) {
        entity.addPart(key, new FileBody(new File(filePath)));
    }

    private class UploadToServer extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... param) {
            String responseString = null;
            HttpClient httpClient = new DefaultHttpClient();
            Log.i("statd", url);

            HttpPost httpPost = new HttpPost(url.trim());
            try {
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity r_entity = response.getEntity();
//Log.i("responsadd",  EntityUtils.toString(r_entity));
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred!";
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("responsadd",  e.getMessage());
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(TextUtils.isEmpty(s))
                listener.onFail("Please Check your internet connection");
            else
            listener.onSuccess(s);
        }
    }
}

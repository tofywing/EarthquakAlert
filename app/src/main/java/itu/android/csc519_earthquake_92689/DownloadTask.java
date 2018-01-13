package itu.android.csc519_earthquake_92689;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import itu.android.csc519_earthquake_92689.callback.TaskCallback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Yee on 8/19/17.
 */

public class DownloadTask extends AsyncTask<URL, Void, String> {

    private TaskCallback mCallback;

    public DownloadTask(TaskCallback callback) {
        mCallback = callback;
    }

    @Override
    protected String doInBackground(URL... params) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(params[0]).build();
            Response response = client.newCall(request).execute();
            byte[] bytes = response.body().bytes();
            if(response.code() == HttpURLConnection.HTTP_OK){
             return new String(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unavailable";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.equals("Unavailable")) {
            mCallback.onFailed(s);
        } else {
            mCallback.onSuccess(s);
        }
    }
}

package itu.android.csc519_earthquake_92689;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.os.AsyncTaskCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import itu.android.csc519_earthquake_92689.Util.AlgoUtil;
import itu.android.csc519_earthquake_92689.Util.TimeUtil;
import itu.android.csc519_earthquake_92689.callback.TaskCallback;
import itu.android.csc519_earthquake_92689.model.Earthquake;

/**
 * Created by Yee on 8/18/17.
 */

public class DownloadTaskLoader extends AsyncTaskLoader<ArrayList<Earthquake>> implements TaskCallback {
    private static final String UNAVAILABLE = "unavailable";
    static final String KEY_MESSAGE = "message";
    static final String KEY_CURRENT_LOAD_DATA = "currentlyDownloadData";
    static final int LOADING_SUCCEED = 1;
    static final int LOADING_FAILED = 2;

    private URL[] mUrls;
    private DownloadTask mTask;
    private int index;
    private Context mContext;
    private ArrayList<Earthquake> mEarthquakes;
    private boolean block;
    private Handler mHandler;

    DownloadTaskLoader(Context context, URL[] urls, Handler handler) {
        super(context);
        mContext = context;
        mUrls = urls;
        mHandler = handler;
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        mEarthquakes = new ArrayList<>();
        mTask = new DownloadTask(this);
        index = 0;
        mTask.execute(mUrls[index]);
        block = true;
        while (block) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mEarthquakes;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public void onSuccess(String data) {
        try {
            Earthquake earthquake;
            JSONObject object = new JSONObject(data);
            JSONObject properties = object.optJSONObject("properties");
            double magVal = properties.optDouble("mag");
            String mag = String.format(Locale.US, "%.01f", magVal);
            String placeTemp = properties.optString("place");
            String[] requiredStr = AlgoUtil.getRequiredPlaceString(placeTemp);
            String place = requiredStr[0];
            String placeFull = requiredStr[1];
            JSONObject products = properties.optJSONObject("products");
            JSONArray origin = products.optJSONArray("origin");
            JSONObject originObject = origin.getJSONObject(0);
            JSONObject originProperties = originObject.optJSONObject("properties");
            float depthKm = Float.valueOf(originProperties.optString("depth"));
            String depth = depthKm < 1 ? UNAVAILABLE : String.valueOf((int) AlgoUtil.kmToMiles(depthKm)) + " Mi";
            String detailURL = mUrls[index].toString();
            String webURL = properties.optString("url");
            int distanceInKm = (int) (originProperties.optDouble("minimum-distance") * 1000);
            String distance = distanceInKm == 0 ? UNAVAILABLE : (int) AlgoUtil.kmToMiles(distanceInKm) + " Mi";
            long timeTemp = properties.optLong("time");
            String time = TimeUtil.convertTimeInFormat(timeTemp);
            long updateTemp = properties.optLong("updated");
            String update = TimeUtil.convertTimeInFormat(updateTemp);
            String magType = properties.optString("magType");
            String significant = properties.optString("sig");
            double longitude = originProperties.optDouble("longitude");
            double latitude = originProperties.optDouble("latitude");
            earthquake = new Earthquake(
                    mag,
                    place,
                    placeFull,
                    depth,
                    detailURL,
                    webURL,
                    distance,
                    time,
                    update,
                    magType,
                    significant,
                    longitude,
                    latitude
            );
            mEarthquakes.add(earthquake);
            Bundle bundle = new Bundle();
            bundle.putString(KEY_MESSAGE, requiredStr[1] + "\n");
            Message msg = Message.obtain();
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            mTask = new DownloadTask(this);
            if (index + 1 < mUrls.length) {
                mTask.execute(mUrls[++index]);
            } else {
                Message msg1 = Message.obtain();
                msg1.arg1 = LOADING_SUCCEED;
                mHandler.sendMessage(msg1);
                block = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(String data) {
        Message msg = Message.obtain();
        msg.arg1 = LOADING_FAILED;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_CURRENT_LOAD_DATA, mEarthquakes);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void stopLoading() {
        super.stopLoading();
        if (mTask != null) {
            mTask.cancel(true);
        }
        onFailed(mContext.getString(R.string.task_cancel));
    }
}

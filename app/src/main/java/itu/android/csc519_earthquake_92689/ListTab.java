package itu.android.csc519_earthquake_92689;


import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import itu.android.csc519_earthquake_92689.Util.AlgoUtil;
import itu.android.csc519_earthquake_92689.Util.TimeUtil;
import itu.android.csc519_earthquake_92689.adapter.ListAdapter;
import itu.android.csc519_earthquake_92689.callback.DataPrepareCallback;
import itu.android.csc519_earthquake_92689.callback.TaskCallback;
import itu.android.csc519_earthquake_92689.model.Earthquake;

import static android.content.Intent.ACTION_ATTACH_DATA;
import static itu.android.csc519_earthquake_92689.DownloadTaskLoader.KEY_CURRENT_LOAD_DATA;
import static itu.android.csc519_earthquake_92689.DownloadTaskLoader.KEY_MESSAGE;
import static itu.android.csc519_earthquake_92689.DownloadTaskLoader.LOADING_FAILED;
import static itu.android.csc519_earthquake_92689.DownloadTaskLoader.LOADING_SUCCEED;
import static itu.android.csc519_earthquake_92689.MainActivity.KEY_EARTHQUAKES;
import static itu.android.csc519_earthquake_92689.SettingsActivity.SETTINGS_CHANGED;

/**
 * Created by Yee on 8/16/17.
 */

public class ListTab extends Fragment implements TaskCallback {
    public static final String TAG = "ListTab";
    public static final String URL_FORMAT = "https://earthquake.usgs" +
            ".gov/fdsnws/event/1/query?format=geojson&starttime=%s&endtime=%s";
    public static int INITIAL_LOADER_ID = 1;
    public static final String KEY_EARTHQUAKES = "ListTabEarthQuakesSet";

    ListAdapter mListAdapter;
    RecyclerView mList;
    LoaderManager mLoaderManager;
    DownloadTaskLoader mTaskLoader;
    ArrayList<Earthquake> mEarthquakes;
    Dialog mDialog;
    TextView mDialogContent;
    String mDataMsg;
    TextView mBarProgress;
    ProgressBar mProgressBar;
    TextView mUpdateInfo;
    float mProgress = 0;
    float mDownloadInterval = 0;
    long mSearchRange = 0;
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.arg1) {
                case LOADING_SUCCEED:
                    mDialogContent.setText(R.string.laoding_complete);
                    updateFinishAction();
                    break;
                case LOADING_FAILED:
                    mDialogContent.setText(R.string.loading_failed);
                    updateFinishAction();
                    mEarthquakes = msg.getData().getParcelableArrayList(KEY_CURRENT_LOAD_DATA);
                    mPrepareCallback.onDataPrepared(mEarthquakes);
                    setupAdapter();
                    break;
                default:
                    break;
            }
            mDataMsg += msg.getData().getString(KEY_MESSAGE);
            mDialogContent.setText(mDataMsg);
            if ((mProgress += mDownloadInterval) > 100) {
                mProgress = 100;
            }
            mBarProgress.setText(String.format(Locale.US, "%.01f%%", mProgress));
            return false;
        }
    });
    DataPrepareCallback mPrepareCallback;
    BroadcastReceiver mReceiver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPrepareCallback = (DataPrepareCallback) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mList = (RecyclerView) view.findViewById(R.id.earthquake_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mList.setLayoutManager(layoutManager);
        mList.setHasFixedSize(true);
        mEarthquakes = new ArrayList<>();
        if (savedInstanceState != null) {
            mEarthquakes = savedInstanceState.getParcelableArrayList(KEY_EARTHQUAKES);
            setupAdapter();
        } else {
            reload();
        }
        mUpdateInfo = (TextView) view.findViewById(R.id.update_info);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ACTION_ATTACH_DATA:
                        reload();
                        break;
                    default:
                }
            }
        };
        return view;
    }

    private void setupAdapter() {
        if (mListAdapter == null) {
            mListAdapter = new ListAdapter(mEarthquakes);
            mList.setAdapter(mListAdapter);
        } else {
            mListAdapter.setEarthquakes(mEarthquakes);
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccess(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray jsonArray = object.getJSONArray("features");
            //initially archive top 50 (@depends on the data size) largest earth quake in a week
            final URL[] urls = AlgoUtil.getSortedEarthquake(jsonArray);
            mDownloadInterval = 100 / (float) urls.length;
            //Loader call backs
            mLoaderManager = getActivity().getSupportLoaderManager();
            //restart: start new or recreate if the loader id is already exsited.
            mLoaderManager.restartLoader(INITIAL_LOADER_ID, null, new LoaderManager
                    .LoaderCallbacks<ArrayList<Earthquake>>() {
                @Override
                public Loader<ArrayList<Earthquake>> onCreateLoader(int id, Bundle args) {
                    lockOrientation();
                    mDataMsg = getString(R.string.dialog_loading) + "\n";
                    mDialogContent.setText(mDataMsg);
                    return mTaskLoader = new DownloadTaskLoader(getContext(), urls, mHandler);
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> data) {
                    mEarthquakes = data;
                    mPrepareCallback.onDataPrepared(mEarthquakes);
                    setupAdapter();
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(String data) {

    }

    private void showDialog() {
        mDialog = new Dialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog, null);
        mDialogContent = (TextView) view.findViewById(R.id.dialog_content);
        mDialogContent.setText(R.string.dialog_loading);
        mDialogContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mBarProgress = (TextView) view.findViewById(R.id.bar_progress);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mProgressBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTaskLoader != null) {
                    mTaskLoader.stopLoading();
                }
                updateFinishAction();
            }
        });
        mDialog.setContentView(view);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ATTACH_DATA);
        Activity activity = getActivity();
        if (activity != null) {
            activity.registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        if (activity != null) {
            activity.unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void lockOrientation() {
        Activity activity = getActivity();
        if (activity != null) {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        }
    }

    private void unlockOrientation() {
        Activity activity = getActivity();
        if (activity != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    private void downloadDataInitialize() {
        mProgress = 0;
        mDataMsg = "";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_EARTHQUAKES, mEarthquakes);
    }

    private void updateUpdatingTime() {
        long current = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("'Updated:' hh:mm a", Locale.US);
        mUpdateInfo.setText(format.format(new Date(current)));
    }

    private void updateFinishAction() {
        unlockOrientation();
        downloadDataInitialize();
        updateUpdatingTime();
        mDialog.dismiss();
    }

    private void reload() {
        showDialog();
        openPreferredLocationInMap();
        String[] weeklyBond = TimeUtil.getWeeklyBound(System.currentTimeMillis(), mSearchRange);
        String urlTemp = String.format(Locale.US, URL_FORMAT, weeklyBond[0], weeklyBond[1]);
        try {
            URL url = new URL(urlTemp);
            DownloadTask task = new DownloadTask(this);
            task.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void openPreferredLocationInMap() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mSearchRange = Long.valueOf(sharedPrefs.getString(getString(R.string.setting_key), "0"));
    }
}

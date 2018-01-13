package itu.android.csc519_earthquake_92689;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import itu.android.csc519_earthquake_92689.model.Earthquake;

/**
 * Created by Yee on 8/22/17.
 */

public class PopDialog extends Dialog {
    private static final String TAG_DETAIL_DIALOG = "detailDialogFragment";
    private static final String TAG_MAP_DIALOG = "mapDialogFragment";
    private Context mContext;
    private DialogFragment mFragment;
    private MapDialog mMapDialog;
    private Earthquake mEarthquake;


    public PopDialog(@NonNull Context context, Earthquake earthquake) {
        super(context);
        mContext = context;
        mEarthquake = earthquake;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog, null);
        setContentView(view);
        ImageView info = (ImageView) view.findViewById(R.id.dialog_info);
        ImageView share = (ImageView) view.findViewById(R.id.dialog_share);
        ImageView map = (ImageView) view.findViewById(R.id.dialog_map);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateFragment(mEarthquake);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performShareAction(mEarthquake);
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapDialog != null && mMapDialog.isAdded())
                    mMapDialog.dismiss();
                mMapDialog = MapDialog.newInstance(mEarthquake);
                AppCompatActivity activity = (AppCompatActivity) mContext;
                FragmentManager manager = activity.getSupportFragmentManager();
                mMapDialog.setCancelable(true);
                mMapDialog.show(manager, TAG_DETAIL_DIALOG);
            }
        });
    }

    private void onCreateFragment(Earthquake earthquake) {
        Activity activity = (Activity) mContext;
        android.app.FragmentManager manager = activity.getFragmentManager();
        if (mFragment != null && mFragment.isAdded())
            mFragment.dismiss();
        mFragment = DetailDialog.newInstance(earthquake);
        mFragment.show(manager, TAG_MAP_DIALOG);
    }

    private void performShareAction(Earthquake earthquake) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, earthquake.getPlaceFull() + "\n\n" + earthquake.getMag());
        shareIntent.setType("text/plain");
        mContext.startActivity(shareIntent);
    }
}



package itu.android.csc519_earthquake_92689;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

import itu.android.csc519_earthquake_92689.Util.TimeUtil;
import itu.android.csc519_earthquake_92689.model.Earthquake;

/**
 * Created by Yee on 8/21/17.
 */

public class DetailDialog extends DialogFragment {
    public static final String TAG = "DetailDialog";
    public static final String KEY_EARTHQUAKE = "transferredEarthQuake";

    TextView mPlace;
    TextView mData;
    TextView mLocation;
    TextView mDepth;
    TextView mSignificance;
    TextView mUrl;

    Earthquake mEarthquake;

    public static DetailDialog newInstance(Earthquake earthquake) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_EARTHQUAKE, earthquake);
        DetailDialog fragment = new DetailDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.detail_dialog);
        mEarthquake = getArguments().getParcelable(KEY_EARTHQUAKE);
        mPlace = (TextView) dialog.findViewById(R.id.dialog_place);
        mPlace.setText(mEarthquake.getPlaceFull());
        mData = (TextView) dialog.findViewById(R.id.dialog_data);
        String data = getString(R.string.dialog_data, mEarthquake.getMag(), mEarthquake.getTime());
        mData.setText(data);
        mLocation = (TextView) dialog.findViewById(R.id.dialog_location);
        mLocation.setText(getString(
                R.string.dialog_location,
                String.valueOf(mEarthquake.getLongitude()),
                String.valueOf(mEarthquake.getLatitude())
        ));
        mDepth = (TextView) dialog.findViewById(R.id.dialog_depth);
        mDepth.setText(getString(R.string.dialog_depth, mEarthquake.getDepth()));
        mSignificance = (TextView) dialog.findViewById(R.id.dialog_significance);
        mSignificance.setText(getString(R.string.dialog_sig, mEarthquake.getSig()));
        mUrl = (TextView) dialog.findViewById(R.id.dialog_url);
        mUrl.setText(Html.fromHtml("<a href=" + mEarthquake.getWebURL() + "><u>View on USGS website</u> "));
        mUrl.setMovementMethod(LinkMovementMethod.getInstance());
        return dialog;
    }
}

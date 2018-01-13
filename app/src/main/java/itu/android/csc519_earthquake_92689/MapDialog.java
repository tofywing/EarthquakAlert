package itu.android.csc519_earthquake_92689;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.ColorUtils;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import itu.android.csc519_earthquake_92689.Util.AlgoUtil;
import itu.android.csc519_earthquake_92689.model.Earthquake;

/**
 * Created by Yee on 8/22/17.
 */

public class MapDialog extends DialogFragment {
    public static final String KEY_EARTHQUAKE = "MapDialogEarthquake";
    public static final float CLOSE_ZOOM = 9.5f;

    SupportMapFragment mMapFragment;
    Earthquake mEarthquake;

    public static MapDialog newInstance(Earthquake earthquake) {
        Bundle args = new Bundle();
        MapDialog fragment = new MapDialog();
        fragment.setArguments(args);
        args.putParcelable(KEY_EARTHQUAKE, earthquake);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.fragment_map_dialog);
        mEarthquake = getArguments().getParcelable(KEY_EARTHQUAKE);
        mMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id
                .mapDialogFragment);
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MarkerOptions options;
                float[] hue = new float[3];
                ColorUtils.RGBToHSL(AlgoUtil.calculateMagRedColor(mEarthquake.getMag(), 7), 0, 5, hue);
                options = new MarkerOptions().title(mEarthquake.getPlaceFull())
                        .snippet(getString(R.string.dialog_data, mEarthquake.getMag(), mEarthquake.getTime()))
                        .position(new LatLng(mEarthquake.getLatitude(), mEarthquake.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(hue[0]));
                googleMap.addMarker(options);
                CameraUpdate cameraUpdate = CameraUpdateFactory
                        .newLatLngZoom(new LatLng(mEarthquake.getLatitude(), mEarthquake.getLongitude()), CLOSE_ZOOM);
                googleMap.animateCamera(cameraUpdate);

            }
        });
        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        if (activity != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(mMapFragment).commitAllowingStateLoss();
        }
    }
}

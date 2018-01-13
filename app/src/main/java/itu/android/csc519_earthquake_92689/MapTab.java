package itu.android.csc519_earthquake_92689;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import itu.android.csc519_earthquake_92689.Util.AlgoUtil;
import itu.android.csc519_earthquake_92689.model.Earthquake;

/**
 * Created by Yee on 8/17/17.
 */

public class MapTab extends SupportMapFragment {
    public static final String TAG = "MapTab";
    public static final String KEY_EARTHQUAKES = "MapTabTransferredEarthquakes";

    ArrayList<Earthquake> mEarthquakes;
    List<Marker> mMarkers;
    GoogleMap mMap;
    PopDialog mPopDialog;

    public static MapTab newInstance(ArrayList<Earthquake> earthquakes) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_EARTHQUAKES, earthquakes);
        MapTab fragment = new MapTab();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEarthquakes = getArguments().getParcelableArrayList(KEY_EARTHQUAKES);
        reload(mEarthquakes);
    }

    void setMarker() {
        if (mMarkers != null) {
            for (Marker marker : mMarkers) {
                marker.remove();
            }
        }
        mMarkers = new LinkedList<>();
        Marker marker;
        MarkerOptions options;
        int i = 0;
        if (mEarthquakes != null) {
            for (Earthquake earthquake : mEarthquakes) {
                float[] hue = new float[3];
                ColorUtils.RGBToHSL(AlgoUtil.calculateMagRedColor(earthquake.getMag(), 7), 0, 5, hue);
                options = new MarkerOptions().title(earthquake.getPlaceFull())
                        .snippet(getString(R.string.dialog_data, earthquake.getMag(), earthquake.getTime()))
                        .position(new LatLng(earthquake.getLatitude(), earthquake.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(hue[0]));
                marker = mMap.addMarker(options);
                marker.setTag(i++);
                mMarkers.add(marker);
            }
        }
    }

    public void reload(ArrayList<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        mPopDialog = new PopDialog(getContext(), mEarthquakes.get((int) marker.getTag()));
                        mPopDialog.setCancelable(true);
                        mPopDialog.show();
                    }
                });
                setMarker();
            }
        });
    }
}

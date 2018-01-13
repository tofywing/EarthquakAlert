package itu.android.csc519_earthquake_92689;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Yee on 8/18/17.
 */

public class StatusTab extends Fragment {
    //TODO:

    public static StatusTab newInstance() {

        Bundle args = new Bundle();

        StatusTab fragment = new StatusTab();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        return view;
    }
}

package itu.android.csc519_earthquake_92689.callback;

import java.util.ArrayList;

import itu.android.csc519_earthquake_92689.model.Earthquake;

/**
 * Created by Yee on 8/22/17.
 */

public interface DataPrepareCallback{
    void onDataPrepared(ArrayList<Earthquake> earthquakes);
}

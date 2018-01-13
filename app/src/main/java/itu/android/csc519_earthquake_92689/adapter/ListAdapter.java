package itu.android.csc519_earthquake_92689.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import itu.android.csc519_earthquake_92689.PopDialog;
import itu.android.csc519_earthquake_92689.R;
import itu.android.csc519_earthquake_92689.Util.AlgoUtil;
import itu.android.csc519_earthquake_92689.model.Earthquake;

/**
 * Created by Yee on 8/17/17.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {
    private ArrayList<Earthquake> mEarthquakes;
    private Context mContext;
    private PopDialog mPopDialog;

    public ListAdapter(@NonNull ArrayList<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
    }

    @Override
    public ListAdapter.ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        mContext = parent.getContext();
        return new ListHolder(view);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ListHolder holder, int position) {
        final Earthquake earthquake = mEarthquakes.get(position);
        holder.mContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPopDialog = new PopDialog(mContext, earthquake);
                mPopDialog.setCancelable(true);
                mPopDialog.show();
                return false;
            }
        });
        holder.mag.setText(earthquake.getMag());
        holder.mag.setTextColor(Color.argb(255, AlgoUtil.calculateMagRedColor(earthquake.getMag(), 7), 0, 5));
        holder.place.setText(earthquake.getPlace());
        holder.depth.setText(mContext.getString(R.string.item_depth, earthquake.getDepth()));
        holder.distance.setText(mContext.getString(R.string.item_distance, earthquake.getDistance()));
        holder.time.setText(earthquake.getTime());
        holder.time.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
    }

    @Override
    public int getItemCount() {
        return mEarthquakes != null ? mEarthquakes.size() : 0;
    }

    class ListHolder extends RecyclerView.ViewHolder {
        RelativeLayout mContainer;
        TextView mag;
        TextView place;
        TextView depth;
        TextView distance;
        TextView time;

        ListHolder(View itemView) {
            super(itemView);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.item_container);
            mag = (TextView) itemView.findViewById(R.id.mag);
            place = (TextView) itemView.findViewById(R.id.place);
            depth = (TextView) itemView.findViewById(R.id.depth);
            distance = (TextView) itemView.findViewById(R.id.distance);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }

    public void setEarthquakes(ArrayList<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
    }
}

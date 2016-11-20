package com.perfect_apps.tawsili.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.models.SchedualObject;
import com.perfect_apps.tawsili.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mostafa_anter on 10/14/16.
 */

public class MyScheduleRidesItemsAdapter extends RecyclerView.Adapter<MyScheduleRidesItemsAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<SchedualObject> mDataSet;
    private static Context mContext;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timeStamp)TextView timeStamp;
        @BindView(R.id.current_location_text)TextView currentLocation;
        @BindView(R.id.drop_off_location_text)TextView dropOffLocation;
        @BindView(R.id.dropOffView)LinearLayout dropOffView;

        public TextView getTimeStamp() {
            return timeStamp;
        }

        public TextView getCurrentLocation() {
            return currentLocation;
        }

        public TextView getDropOffLocation() {
            return dropOffLocation;
        }

        public LinearLayout getDropOffView() {
            return dropOffView;
        }

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });

        }


    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public MyScheduleRidesItemsAdapter(Context mContext, List<SchedualObject> dataSet) {
        this.mContext = mContext;
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.my_rides_row, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        if (!mDataSet.get(position).getTo_details().trim().isEmpty() &&
                !mDataSet.get(position).getTo_details().equalsIgnoreCase("null")){
            viewHolder.getDropOffView().setVisibility(View.VISIBLE);
            viewHolder.getDropOffLocation().setText(mDataSet.get(position)
                    .getTo_details());
        }else {
            viewHolder.getDropOffView().setVisibility(View.GONE);
        }

        viewHolder.getCurrentLocation().setText(mDataSet.get(position)
                .getFrom_details());

        viewHolder.getTimeStamp().setText(Utils.manipulateDateFormat(mDataSet.get(position)
                .getSchedual_create_time()));
        // Get element from your dataset at this position and replace the contents of the view
        // with that element

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
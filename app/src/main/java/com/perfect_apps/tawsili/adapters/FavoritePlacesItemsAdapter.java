package com.perfect_apps.tawsili.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.models.FavoritePlaceItem;
import com.perfect_apps.tawsili.models.MyRidesItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mostafa_anter on 10/14/16.
 */

public class FavoritePlacesItemsAdapter extends RecyclerView.Adapter<FavoritePlacesItemsAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<FavoritePlaceItem> mDataSet;
    private Context mContext;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)TextView title;
        @BindView(R.id.favImage)ImageView favImage;
        @BindView(R.id.address)TextView subtitle;

        public TextView getTitle() {
            return title;
        }

        public ImageView getFavImage() {
            return favImage;
        }

        public TextView getSubtitle() {
            return subtitle;
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
    public FavoritePlacesItemsAdapter(Context mContext, List<FavoritePlaceItem> dataSet) {
        this.mContext = mContext;
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.favorite_places_item_row, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        viewHolder.getTitle().setText(mDataSet.get(position).getName());
        viewHolder.getSubtitle().setText(mDataSet.get(position).getVicinity());

        // Get element from your dataset at this position and replace the contents of the view
        // with that element

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
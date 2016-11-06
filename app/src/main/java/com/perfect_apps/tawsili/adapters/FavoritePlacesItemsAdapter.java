package com.perfect_apps.tawsili.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.models.FavoritePlaceItem;
import com.perfect_apps.tawsili.models.MyRidesItem;
import com.perfect_apps.tawsili.store.FavoritePlacesStore;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mostafa_anter on 10/14/16.
 */

public class FavoritePlacesItemsAdapter extends RecyclerView.Adapter<FavoritePlacesItemsAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<FavoritePlaceItem> mDataSet;
    private Context mContext;

    // belong like button animations
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();
    // put control on one item selected
    private int lastCheckedPosition = -1;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView title;
        @BindView(R.id.favImage)
        ImageView favImage;
        @BindView(R.id.address)
        TextView subtitle;

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
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        viewHolder.getTitle().setText(mDataSet.get(position).getName());
        viewHolder.getSubtitle().setText(mDataSet.get(position).getVicinity());
        viewHolder.getFavImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0 && position != 1) {
                    if (!new FavoritePlacesStore(mContext).isFavoritItem(mDataSet.get(position).getLat()
                            + "," + mDataSet.get(position).getLng())) {
                        lastCheckedPosition = position;
                        notifyItemRangeChanged(0, mDataSet.size());
                        if (!likedPositions.contains(position)) {
                            likedPositions.add(position);
                            updateHeartButton(viewHolder, true);
                        }
                        addItem(position);
                    } else {

                        if (likedPositions.contains(position)) {
                            likedPositions.remove(position);
                            lastCheckedPosition = -1;
                        }
                        viewHolder.getFavImage().setImageResource(R.drawable.ic_grade_gray);
                        removeItem(position);
                    }
                }
            }
        });
        if (position == lastCheckedPosition) {
            viewHolder.getFavImage().setImageResource(R.drawable.ic_grade_black_24dp);
        } else if (new FavoritePlacesStore(mContext).isFavoritItem(mDataSet.get(position).getLat()
                + "," + mDataSet.get(position).getLng())) {
            // this item is in my database
            viewHolder.getFavImage().setImageResource(R.drawable.ic_grade_black_24dp);
        } else {
            viewHolder.getFavImage().setImageResource(R.drawable.ic_grade_gray);
        }

        //for handel first two items
        if (mDataSet.get(position).getLat().equalsIgnoreCase("8") ||
                mDataSet.get(position).getLat().equalsIgnoreCase("9")) {
            if (mDataSet.get(position).getLat().equalsIgnoreCase("8")) {
                viewHolder.getFavImage().setImageResource(R.drawable.car2);
            } else if (mDataSet.get(position).getLat().equalsIgnoreCase("9")) {
                viewHolder.getFavImage().setImageResource(R.drawable.location);
            }

        }

    }

    private void addItem(int position) {
        //add item to favorite
        FavoritePlaceItem item = mDataSet.get(position);
        new FavoritePlacesStore(mContext).addItem(item);
    }

    private void removeItem(int position) {
        //add item to favorite
        FavoritePlaceItem item = mDataSet.get(position);
        new FavoritePlacesStore(mContext).removeItem(item);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // like button effect
    private void updateHeartButton(final ViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.getFavImage(), "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.getFavImage(), "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.getFavImage(), "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.getFavImage().setImageResource(R.drawable.ic_grade_black_24dp);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // do something
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.getFavImage().setImageResource(R.drawable.ic_grade_black_24dp);
            } else {
                holder.getFavImage().setImageResource(R.drawable.ic_grade_gray);
            }
        }
    }
}
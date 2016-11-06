package com.perfect_apps.tawsili.activities;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.adapters.FavoritePlacesItemsAdapter;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.models.FavoritePlaceItem;
import com.perfect_apps.tawsili.parser.JsonParser;
import com.perfect_apps.tawsili.store.FavoritePlacesStore;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;
import com.perfect_apps.tawsili.utils.Constants;
import com.perfect_apps.tawsili.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritePlacesActivity extends LocalizationActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private static final String TAG = "ProviderChatsFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;


    protected LayoutManagerType mCurrentLayoutManagerType;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected FavoritePlacesItemsAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<FavoritePlaceItem> mDataset;

    public static int FLAG = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_places);
        FLAG = getIntent().getIntExtra(Constants.comingFrom, 0);
        ButterKnife.bind(this);
        setToolbar();

        setmRecyclerView(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favorite_places, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setmRecyclerView(Bundle savedInstanceState) {
        // initiate mDataSet
        mDataset = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new FavoritePlacesItemsAdapter(this, mDataset);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);


        //noinspection ResourceAsColor
        mSwipeRefresh.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        mSwipeRefresh.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        24,
                        getResources().getDisplayMetrics()));


        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                initiateRefresh();
            }
        });

        initiateRefresh();
        if (mSwipeRefresh != null && !mSwipeRefresh.isRefreshing())
            mSwipeRefresh.setRefreshing(true);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        * hide title
        * */
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        TextView tv = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv.setTypeface(font);

    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private void initiateRefresh() {
        if (!mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.setRefreshing(true);
        }
        clearDataSet();
        getNearPlaces();
    }

    private void onRefreshComplete() {
        if (mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void getNearPlaces() {
        String lat = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLat);
        String lng = new TawsiliPrefStore(this).getPreferenceValue(Constants.userLastLocationLng);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + lat + "," + lng +
                "&radius=1000&type=establishment&rankby=prominence&key=AIzaSyAiT5rjXpzq0N7-ibpjq-QW5_pDfFPElRw";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                List<FavoritePlaceItem> favoritePlaceItems = JsonParser.parseFavoritePlaces(response);

                if (favoritePlaceItems != null) {
                    for (FavoritePlaceItem item :
                            favoritePlaceItems) {
                        mDataset.add(item);
                        mAdapter.notifyDataSetChanged();

                    }
                }

                // add item inside favorite at top
                mDataset.addAll(0, new FavoritePlacesStore(FavoritePlacesActivity.this).findAll());
                mAdapter.notifyDataSetChanged();


                if (FLAG != 100) {
                    // add two item at top
                    FavoritePlaceItem favoritePlaceItem1 = new FavoritePlaceItem(getString(R.string.iwill_guid),
                            "", "8", null, false);
                    FavoritePlaceItem favoritePlaceItem2 = new FavoritePlaceItem(getString(R.string.select_location_from_map),
                            "", "9", null, false);
                    mDataset.add(0, favoritePlaceItem1);
                    mDataset.add(1, favoritePlaceItem2);
                    mAdapter.notifyDataSetChanged();
                }

                onRefreshComplete();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                onRefreshComplete();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    // remove all item from RecyclerView
    private void clearDataSet() {
        if (mDataset != null) {
            mDataset.clear();
            mAdapter.notifyItemRangeRemoved(0, mDataset.size());
        }
    }

}

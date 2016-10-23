package com.perfect_apps.tawsili.utils;

import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by mostafa_anter on 10/20/16.
 */

public class MapHelper {

    public static void setUpMarker(GoogleMap mMap, LatLng latLng, int resID) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        //   options.title();
        options.icon(BitmapDescriptorFactory.fromResource(resID));
        Marker marker = mMap.addMarker(options);

        marker.showInfoWindow();
    }

    /*
     * Zooms the map to show the area of interest based on the search radius
     */
    public static void updateZoom(GoogleMap mMap, LatLng myLatLng) {
        // Zoom to the given bounds
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
    }

    public static void drawCircle(GoogleMap mMap, LatLng ll) {
        CircleOptions options = new CircleOptions()
                .center(ll)
                .radius(400) //meters surround us
                .fillColor(0x330000FF)
                .strokeColor(Color.GRAY)
                .strokeWidth(3);
        mMap.addCircle(options);
    }

    // Animation handler for old APIs without animation support
    public static void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


}

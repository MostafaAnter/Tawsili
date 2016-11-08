package com.perfect_apps.tawsili.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.perfect_apps.tawsili.models.ReceiveSMSEvent;
import com.perfect_apps.tawsili.models.TouchMapEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mostafa_anter on 11/9/16.
 */

public class CustomFrameLayout extends FrameLayout {
    private static final String TAG = "CustomFrameLayout";

    public CustomFrameLayout(Context context) {
        super(context);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onInterceptTouchEvent.ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onInterceptTouchEvent.ACTION_MOVE");
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onInterceptTouchEvent.ACTION_UP");
                // post message
                EventBus.getDefault().post(new TouchMapEvent());
                break;
        }

        return super.onInterceptTouchEvent(ev);


    }
}

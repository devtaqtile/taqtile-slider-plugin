package com.taqtile.dierbergs.slider;


import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.FrameLayout;

import com.ionicframework.myapp398042.R;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by dmitry on 9/5/16.
 */
public class SliderPlugin extends CordovaPlugin {

    private final String SLIDER_PLUGIN = "SliderPlugin";
    public final static int PAGES = 5;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;

    public MyPagerAdapter adapter;
    public ViewPager pager;
    public Activity activity;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(SLIDER_PLUGIN, "SliderPlugin called with options: " + args);
//        activity = cordova.getActivity();
//
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1420);
//        params.topMargin  = 280;
//
//
//        final View pagerLayout = LayoutInflater.from(activity).inflate(activity.getResources().getIdentifier("viewpager","layout",activity.getPackageName()),null);
//
//        pagerLayout.setLayoutParams(params);
//        pager = (ViewPager) pagerLayout.findViewById(R.id.myviewpager);
//
//
//
//        adapter = new MyPagerAdapter(activity);
//        pager.setPageTransformer(false, adapter);
//
//        pager.setAdapter(adapter);
//
//        pager.setClipChildren(false);
//
//        // Set current item to the middle page so we can fling to both
//        // directions left and right
//        pager.setCurrentItem(FIRST_PAGE);
//
//        // Necessary or the pager will only have one extra page to show
//        // make this at least however many pages you can see
//        pager.setOffscreenPageLimit(3);
//
//        // Set margin for pages as a negative number, so a part of next and
//        // previous pages will be showed
//        pager.setPageMargin(100);
//
//        final ViewGroup root = (ViewGroup) webView.getView().getParent();

        activity = cordova.getActivity();

//        pager = new ViewPager(cordova.getActivity());
//        pager.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1420);
        params.topMargin = 280;
//        pager.setLayoutParams(params);


        final View pagerLayout = LayoutInflater.from(activity).inflate(activity.getResources().getIdentifier("viewpager", "layout", activity.getPackageName()), null);

        pagerLayout.setLayoutParams(params);
        PagerContainer pagerContainer = (PagerContainer) pagerLayout.findViewById(R.id.pager_container);


        pager = pagerContainer.getViewPager();
        adapter = new MyPagerAdapter(activity);

        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setPageTransformer(false, adapter);

        pager.setAdapter(adapter);

        pager.setClipChildren(false);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);

        // Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(3);

        // Set margin for pages as a negative number, so a part of next and
        // previous pages will be showed
        pager.setPageMargin(10);

        final ViewGroup root = (ViewGroup) webView.getView().getParent();


        if ("close".equals(action)) {

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                root.addView(pager);
                    root.removeView(pagerLayout);
                }
            });

            return true;
        } else if ("show".equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                root.addView(pager);
                    root.addView(pagerLayout);
                }
            });

            return true;
        } else if ("echo".equals(action)) {
            callbackContext.success();
            return true;
        }

        return false;
    }

    private void show(JSONArray data, CallbackContext callbackContext) {

    }
}

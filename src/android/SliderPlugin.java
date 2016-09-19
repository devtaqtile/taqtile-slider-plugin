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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by dmitry on 9/5/16.
 */
public class SliderPlugin extends CordovaPlugin {

    public final String SLIDER_PLUGIN = "SliderPlugin";
    public final static int PAGES = 5;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1000;
    public final static int FIRST_PAGE = 0;

    public MyPagerAdapter adapter;
    public ViewPager pager;
    public Activity activity;
    private View pagerLayout;
    private ViewGroup root;
    private boolean isShow;
    private FrameLayout.LayoutParams params;

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
        if (activity == null) {
            activity = cordova.getActivity();

            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity));

            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1420);
            params.topMargin = 280;

            pagerLayout = LayoutInflater.from(activity).inflate(activity.getResources().getIdentifier("viewpager", "layout", activity.getPackageName()), null);
            pagerLayout.setLayoutParams(params);
            PagerContainer pagerContainer = (PagerContainer) pagerLayout.findViewById(R.id.pager_container);

            pager = pagerContainer.getViewPager();

            pager.setClipChildren(false);

            // Set current item to the middle page so we can fling to both
            // directions left and right
            pager.setCurrentItem(FIRST_PAGE);

            // Necessary or the pager will only have one extra page to show
            // make this at least however many pages you can see
            pager.setOffscreenPageLimit(3);

            // Set margin for pages as a negative number, so a part of next and
            // previous pages will be showed
            pager.setPageMargin(30);

            root = (ViewGroup) webView.getView().getParent();
        }

        ArrayList<String> listData = new ArrayList<String>();

        if (args != null) {
            for (int i = 0; i < args.length(); i++) {
                listData.add(args.get(i).toString());
            }
        }

        adapter = new MyPagerAdapter(activity, listData);

        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setPageTransformer(false, adapter);

        if ("close".equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isShow) return;

                    root.removeView(pagerLayout);
                    isShow = false;
                }
            });

            return true;
        } else if ("show".equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isShow) return;

                    pager.setAdapter(adapter);
                    root.addView(pagerLayout);
                    isShow = true;
                }
            });

            return true;
        }

        return false;
    }

    private void show(JSONArray data, CallbackContext callbackContext) {

    }
}

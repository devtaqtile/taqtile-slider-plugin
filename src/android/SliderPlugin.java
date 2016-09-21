package com.taqtile.dierbergs.slider;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
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
    private boolean isClicable = true;

    @Override
    public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(SLIDER_PLUGIN, "SliderPlugin called with options: " + args);

        FrameLayout.LayoutParams params;
        float density;

        if (activity == null && "show".equals(action)) {
            activity = cordova.getActivity();

            density = activity.getResources().getDisplayMetrics().density;

            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity));

            JSONObject elementParams = args.getJSONObject(0);

            final int width = (int) (density * elementParams.getInt("width"));
            final int height = (int) (density * elementParams.getInt("height"));
            final int top = (int) (density * elementParams.getInt("top"));

            params = new FrameLayout.LayoutParams(width, height);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = (int) (density * elementParams.getInt("top"));

            pagerLayout = LayoutInflater.from(activity).inflate(activity.getResources().getIdentifier("viewpager", "layout", activity.getPackageName()), null);

            pagerLayout.setLayoutParams(params);
//            PagerContainer pagerContainer = (PagerContainer) pagerLayout.findViewById(R.id.pager_container);
            final PagerContainer pagerContainer = (PagerContainer) pagerLayout.findViewById(activity.getResources().getIdentifier("pager_container", "id", activity.getPackageName()));


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

            webView.getView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!isClicable || !isShow || event.getY() > height + top || event.getY() < top){
                        return false;
                    } else {
                        return pagerContainer.onTouchEvent(event);
                    }
                }
            });

        }

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
            adapter = new MyPagerAdapter(activity, args.getJSONArray(1), callbackContext);

            pager.setOffscreenPageLimit(adapter.getCount());
            pager.setPageTransformer(false, adapter);

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isShow) return;

                    if (Build.VERSION.SDK_INT >= 21 || "org.xwalk.core.XWalkView".equals(webView.getView().getClass().getName())) {
                        webView.getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }

                    root.setBackgroundColor(Color.WHITE);
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    }
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        webView.getView().setBackgroundColor(0);
                    }

                    webView.getView().setBackgroundColor(Color.TRANSPARENT);

                    pager.setAdapter(adapter);

//                    root.removeView(webView.getView());

//                    webView.getView().setBackgroundColor(Color.TRANSPARENT);

                    root.addView(pagerLayout, 0);


                    isShow = true;
                }
            });

            return true;
        } else if ("destroy".equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isShow) {
                        root.removeView(pagerLayout);
                        isShow = false;
                    }

                    activity = null;
                }
            });

            return true;
        } else if ("setClickable".equals(action)) {
            isClicable = args.getBoolean(0);

            return true;
        }

        return false;
    }


}

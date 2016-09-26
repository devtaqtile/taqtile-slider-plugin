package com.taqtile.dierbergs.slider;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by dmitry on 9/5/16.
 */
public class SliderPlugin extends CordovaPlugin {

    public final String SLIDER_PLUGIN = "SliderPlugin";
    public final static int FIRST_PAGE = 0;

    public MyPagerAdapter adapter;
    public ViewPager pager;
    public RadioGroup radioGroup;
    public Activity activity;
    private View pagerLayout;
    private ViewGroup root;
    private boolean isShow;
    private boolean isClickable = true;

    @Override
    public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(SLIDER_PLUGIN, "SliderPlugin called with options: " + args);

        FrameLayout.LayoutParams params;
        float density;

        //TODO: need refactoring
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
            final PagerContainer pagerContainer = (PagerContainer) pagerLayout.findViewById(activity.getResources().getIdentifier("pager_container", "id", activity.getPackageName()));

            radioGroup = (RadioGroup) pagerLayout.findViewById(activity.getResources().getIdentifier("radioGroup", "id", activity.getPackageName()));
            radioGroup.check(radioGroup.getChildAt(0).getId());

            params = new FrameLayout.LayoutParams(width, height-(int)(25 * density));
            pagerContainer.setLayoutParams(params);
            pagerContainer.setRadioGroupView(radioGroup);

            pager = pagerContainer.getViewPager();
            pager.setClipChildren(false);
            pager.setCurrentItem(FIRST_PAGE);
            pager.setOffscreenPageLimit(3);
            pager.setPageMargin(30);


            root = (ViewGroup) webView.getView().getParent();

            webView.getView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!isClickable || !isShow || event.getY() > height + top || event.getY() < top) {
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
            isClickable = args.getBoolean(0);

            return true;
        }

        return false;
    }


}

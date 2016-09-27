package com.taqtile.dierbergs.slider;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.RadioButton;
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
    private boolean isInit = false;
    private boolean isClickable = true;
    private Resources resources;
    private float density;

    @Override
    public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) {
        Log.d(SLIDER_PLUGIN, "SliderPlugin called with options: " + args);

        if ("init".equals(action)) {
            try {
                if (!isInit) {
                    activity = cordova.getActivity();
                    resources = activity.getResources();
                    density = resources.getDisplayMetrics().density;

                    initPlugin(args);
                    isInit = true;
                    callbackContext.success("init success");
                }
            } catch (JSONException e) {
                callbackContext.error("Args should be JSON");
            }

            return true;
        } else if ("close".equals(action)) {
            closePluginActivity();
            return true;
        } else if ("show".equals(action)) {
            try {
                showSlider(args, callbackContext);
            } catch (JSONException e) {
                callbackContext.error("Args should be JSON");
            }
            return true;
        } else if ("destroy".equals(action)) {

            destroySlider();
            return true;
        } else if ("setClickable".equals(action)) {
            try {
                isClickable = args.getBoolean(0);
            } catch (JSONException e) {
                callbackContext.error("Args should be JSON");
            }

            return true;
        }

        return false;
    }

    private void destroySlider() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    root.removeView(pagerLayout);
                    isShow = false;
                }

                activity = null;
                adapter = null;
                pager = null;
                radioGroup = null;
                pagerLayout = null;
                root = null;
                isClickable = true;
                isInit = false;
            }
        });
    }

    private void showSlider(JSONArray args, CallbackContext callbackContext) throws JSONException {
        adapter = new MyPagerAdapter(activity, args.getJSONArray(0), callbackContext);

        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setPageTransformer(false, adapter);

        for (int i = 0; i < args.getJSONArray(0).length(); i++) {
            RadioButton radioButton = new RadioButton(activity);
            radioButton.setId(i + 42);
            radioButton.setWidth((int) (10 * density));
            radioButton.setHeight((int) (10 * density));
            radioButton.setGravity(Gravity.CENTER);

            RadioGroup.LayoutParams buttonParams
                    = new RadioGroup.LayoutParams(activity, null);
            buttonParams.setMargins(10, 0, 10, 0);

            radioButton.setLayoutParams(buttonParams);
            radioButton.setButtonDrawable(null);

            final int resourceId = resources.getIdentifier("button_selector", "drawable",
                    activity.getPackageName());
            Drawable drawable = resources.getDrawable(resourceId);

            radioButton.setBackground(drawable);

            radioGroup.addView(radioButton);
            if (i == 0) radioGroup.check(i + 42);
        }

        pager.setAdapter(adapter);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isShow) return;
                root.addView(pagerLayout, 0);
                isShow = true;
            }
        });
    }

    private void closePluginActivity() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isShow) return;

                root.removeView(pagerLayout);
                isShow = false;
                radioGroup.removeAllViews();
            }
        });
    }

    private void initPlugin(JSONArray args) throws JSONException {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity));

        JSONObject elementParams = args.getJSONObject(0);

        final int width = (int) (density * elementParams.getInt("width"));
        final int height = (int) (density * elementParams.getInt("height"));
        final int top = (int) (density * elementParams.getInt("top"));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = (int) (density * elementParams.getInt("top"));

        pagerLayout = LayoutInflater.from(activity).inflate(resources.getIdentifier("viewpager", "layout", activity.getPackageName()), null);

        pagerLayout.setLayoutParams(params);
        final PagerContainer pagerContainer = (PagerContainer) pagerLayout.findViewById(resources.getIdentifier("pager_container", "id", activity.getPackageName()));

        radioGroup = (RadioGroup) pagerLayout.findViewById(resources.getIdentifier("radioGroup", "id", activity.getPackageName()));

        params = new FrameLayout.LayoutParams(width, height - (int) (25 * density));
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

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                root.setBackgroundColor(Color.WHITE);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                }

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    webView.getView().setBackgroundColor(0);
                }

                if (Build.VERSION.SDK_INT >= 21 || "org.xwalk.core.XWalkView".equals(webView.getView().getClass().getName())) {
                    webView.getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
                }

                webView.getView().setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }
}

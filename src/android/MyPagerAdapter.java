package com.taqtile.dierbergs.slider;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyPagerAdapter extends PagerAdapter implements ViewPager.PageTransformer {
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private final CallbackContext callbackContext;
    private Activity context;
    private float scale;
    private JSONArray items;
    private ImageLoader imageLoader;

    public MyPagerAdapter(Activity context, JSONArray items, CallbackContext callbackContext) {
        this.context = context;
        this.items = items;
        this.callbackContext = callbackContext;
    }


    public View getItem(final int position) {

        JSONObject item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View page = inflater.inflate(context.getResources().getIdentifier("page", "layout", context.getPackageName()), null);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    callbackContext.success(items.getJSONObject(position).get("id").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            item = items.getJSONObject(position);

            TextView textView = (TextView) page.findViewById(context.getResources().getIdentifier("textView", "id", context.getPackageName()));
            TextView textViewDesc = (TextView) page.findViewById(context.getResources().getIdentifier("textViewDesc", "id", context.getPackageName()));
            textView.setText(item.getString("name"));
            textViewDesc.setText(item.getString("description"));

            ImageView imageView = (ImageView) page.findViewById(context.getResources().getIdentifier("imageView", "id", context.getPackageName()));
            imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(item.getString("url"), imageView);

            page.setOnClickListener(onClickListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (position == SliderPlugin.FIRST_PAGE)
            scale = BIG_SCALE;
        else
            scale = SMALL_SCALE;

        page.setScaleX(scale);
        page.setScaleY(scale);

        return page;
    }


    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View page = getItem(position);
        ((ViewPager) collection).addView(page, 0);

        return page;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return "test";
    }


    @Override
    public int getCount() {
        return items.length();
    }


    @Override
    public void transformPage(View page, float position) {
        float scale = BIG_SCALE;
        if (position > 0) {
            scale = scale - position * DIFF_SCALE;
        } else {
            scale = scale + position * DIFF_SCALE;
        }
        if (scale < 0) scale = 0;
        page.setScaleX(scale);
        page.setScaleY(scale);
    }
}

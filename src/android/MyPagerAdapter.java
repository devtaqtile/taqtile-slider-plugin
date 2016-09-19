package com.taqtile.dierbergs.slider;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MyPagerAdapter extends PagerAdapter implements ViewPager.PageTransformer {
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private Activity context;
    private float scale;
    private List<String> urls;
    private ImageLoader imageLoader;


    public MyPagerAdapter(Activity context, ArrayList<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    public View getItem(int position) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View page = inflater.inflate(context.getResources().getIdentifier("page", "layout", context.getPackageName()), null);
//        TextView textView = (TextView) page.findViewById(context.getResources().getIdentifier("text_view", "id", context.getPackageName()));
        ImageView imageView = (ImageView) page.findViewById(context.getResources().getIdentifier("imageView", "id", context.getPackageName()));
//        textView.setText("page " + position);

//        imageView.setImageResource(android.R.drawable.btn_star_big_on);


        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(urls.get(position), imageView);

        // make the first pager bigger than others
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
        return urls.size();
    }


    @Override
    public void transformPage(View page, float position) {
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 100);
//        params.topMargin = 280;
//
//        MyLinearLayout myLinearLayout = new MyLinearLayout(context);
//        myLinearLayout.setLayoutParams(params);
//        myLinearLayout.setBackgroundColor(Color.BLACK);

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

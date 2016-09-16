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
import android.widget.TextView;

public class MyPagerAdapter extends PagerAdapter implements ViewPager.PageTransformer {
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private MyLinearLayout cur = null;
    private MyLinearLayout next = null;
    private Activity context;
    private FragmentManager fm;
    private float scale;
    private int realCount;

    public MyPagerAdapter(Activity context) {
        this.context = context;
    }

    public View getItem(int position) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View page = inflater.inflate(context.getResources().getIdentifier("page", "layout", context.getPackageName()), null);
        TextView textView = (TextView) page.findViewById(context.getResources().getIdentifier("text_view", "id", context.getPackageName()));
        textView.setText("page " + position);


        // make the first pager bigger than others
        if (position == SliderPlugin.FIRST_PAGE)
            scale = BIG_SCALE;
        else
            scale = SMALL_SCALE;

        position = position % SliderPlugin.PAGES;

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
        return SliderPlugin.PAGES * SliderPlugin.LOOPS;
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

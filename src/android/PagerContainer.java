package com.taqtile.dierbergs.slider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

/**
 * Created by dmitry on 9/16/16.
 */
public class PagerContainer extends FrameLayout implements ViewPager.OnPageChangeListener {
    private ViewPager mPager;
    boolean mNeedsRedraw = false;
    private RadioGroup radioGroupView;

    public PagerContainer(Context context) {
        super(context);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setClipChildren(false);

        //Child clipping doesn't work with hardware acceleration in Android 3.x/4.x
        //You need to set this value here if using hardware acceleration in an
        // application targeted at these releases.
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFinishInflate() {
        try {
            mPager = (ViewPager) getChildAt(0);
            mPager.setOnPageChangeListener(this);
        } catch (Exception e) {
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }

    public ViewPager getViewPager() {
        return mPager;
    }

    private Point mCenter = new Point();
    private Point mInitialTouch = new Point();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenter.x = w / 2;
        mCenter.y = h / 2;
    }

    private final float MOVE_THRESHOLD_DP = 20 * getResources().getDisplayMetrics().density;

    private boolean mMoveOccured;
    private float mDownPosX;
    private float mDownPosY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //We capture any touches not already handled by the ViewPager
        // to implement scrolling from a touch outside the pager bounds.
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouch.x = (int) ev.getX();
                mInitialTouch.y = (int) ev.getY();

                mMoveOccured = false;
                mDownPosX = ev.getX();
                mDownPosY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (!mMoveOccured && ev.getX() < mPager.getRight() && ev.getX() > mPager.getLeft()) {

                    int currentPageId = mPager.getCurrentItem();

                    View page = ((MyPagerAdapter) mPager.getAdapter()).getItem(currentPageId);

                    page.callOnClick();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - mDownPosX) > MOVE_THRESHOLD_DP || Math.abs(ev.getY() - mDownPosY) > MOVE_THRESHOLD_DP) {
                    mMoveOccured = true;
                }
                break;
            default:
                break;
        }
        ev.offsetLocation(mCenter.x - mInitialTouch.x, mCenter.y - mInitialTouch.y);

        return mPager.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mNeedsRedraw) invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        radioGroupView.check(radioGroupView.getChildAt(position).getId());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
    }

    public void setRadioGroupView(RadioGroup radioGroupView) {
        this.radioGroupView = radioGroupView;
    }
}

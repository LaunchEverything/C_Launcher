package com.example.carlauncher.entity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.carlauncher.R;

/**
 * Created by Administrator on 2017/4/16.
 */

public class CarView extends LinearLayout {

    private View mCarView;
    private TextView mTitleView;
    private ImageView mIconView;
    private ReflectView mReflectView;
    private int mReflectWidth;
    private int mReflectHeight;

    public CarView(Context context) {
        this(context, null, 0);
    }

    public CarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        mCarView = findViewById(R.id.item);
        mTitleView = (TextView) findViewById(R.id.item_title);
        mIconView = (ImageView) findViewById(R.id.item_icon);
        mReflectView = (ReflectView) findViewById(R.id.reflect_view);
        mReflectView.setCarView(mCarView);
        super.onFinishInflate();
    }

    public void setReflectViewSize (int width, int height) {
        mReflectWidth = width;
        mReflectHeight = height;
        mReflectView.init(mReflectWidth, mReflectHeight);
    }

    public View getTitleView() {
        return mTitleView;
    }

    public View getIconView() {
        return mIconView;
    }

    public View getMainView () {
        return mCarView;
    }

    public View getReflectView () {
        return mReflectView;
    }
}

package com.example.carlauncher;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carlauncher.entity.BenchItem;

/**
 * Created by Administrator on 2017/4/21.
 */

public class BenchItemView extends LinearLayout{
    private ImageView icon;
    private TextView title;

    public BenchItemView(Context context) {
        super(context);
    }

    public BenchItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BenchItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        icon = (ImageView)findViewById(R.id.img);
        title = (TextView)findViewById(R.id.title);
    }
    public void setItem (BenchItem item) {
        icon.setImageResource(item.iconId);
        title.setText(item.titleId);
    }
}

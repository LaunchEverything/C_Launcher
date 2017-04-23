package com.example.carlauncher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/4/22.
 */

public class CarRecyclerView extends RecyclerView {

    public CarRecyclerView(Context context) {
        this(context, null, 0);
    }

    public CarRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}

package com.example.carlauncher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import rouchuan.customlayoutmanager.CustomLayoutManager;

/**
 * Created by zixintechno on 12/7/16.
 */

public class ScrollZoomLayoutManager extends CustomLayoutManager {

    private static final float SCALE_RATE = 1.1f;
    private static final float MAX_RATION = 9f;
    private int itemSpace = 0;
    private RecyclerView recyclerView;
    private final static String TAG = "ScrollZoomLayoutManager";

    public ScrollZoomLayoutManager(Context context, int startLeft, int itemSpace) {
        super(context);
        this.itemSpace = itemSpace;
        this.startLeft = startLeft;
    }

    public ScrollZoomLayoutManager(Context context, int startLeft, int itemSpace, boolean isClockWise) {
        super(context,isClockWise);
        this.itemSpace = itemSpace;
        this.startLeft = startLeft;
    }

    public void setRecycleView(RecyclerView view){
        recyclerView = view;
    }

    @Override
    protected float setInterval() {
        return mDecoratedChildWidth + itemSpace;
    }

    @Override
    protected void setUp() {

    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        float scale = calculateScale((int) targetOffset + startLeft);
        float rotation = calculateRotation((int) targetOffset + startLeft);
        itemView.setPivotY(mDecoratedChildHeight / 4);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
        itemView.setRotationY(rotation);
        boolean hideView =  ((targetOffset + startLeft + mDecoratedChildWidth > getHorizontalSpace())
                || (targetOffset + startLeft <= 0)) && scrollStop;
        if (hideView){
            itemView.setAlpha(0);
        }
   }

    /**
     *
     * @param x start positon of the view you want scale
     * @return the scale rate of current scroll offset
     */
    public float calculateScale(int x){
        Log.d(TAG, "calculateScale,x = " + x);
        float maxScale = SCALE_RATE - 1;
        //a = center,b = maxScale
        float center = (getHorizontalSpace() - mDecoratedChildWidth) / 2;
        float a = maxScale / (center * center);
        float scale = 1 + a * (x - center) * (x - center);
        return scale;
    }

    public float calculateRotation(int x){
        Log.d(TAG, "calculateRotation,x = " + x);
        int center = (getHorizontalSpace() - mDecoratedChildWidth) / 2;
        float maxRation = MAX_RATION;
        float ration = maxRation;
        float a = maxRation / (center * center);
        if (x > center) {
            return -a * (x - center) * (x - center);
        } else {
            return a * (x - center) * (x - center);
        }
    }
}

package com.example.carlauncher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.carlauncher.entity.Item;
import com.example.carlauncher.helper.MyItemTouchCallback;
import com.example.carlauncher.helper.OnRecyclerItemClickListener;
import com.example.carlauncher.utils.EaseCubicInterpolator;
import com.example.carlauncher.utils.VibratorUtil;

import java.util.ArrayList;
import java.util.List;

import rouchuan.customlayoutmanager.CenterScrollListener;

public class MainActivity extends Activity {

    private List<Item> mResults = new ArrayList<Item>();
    RecyclerView mRecyclerView;
    private ScrollZoomLayoutManager scrollZoomLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_main);
        int space = getResources().getDimensionPixelSize(R.dimen.item_space);
        scrollZoomLayoutManager = new ScrollZoomLayoutManager(this, space);
        mRecyclerView.addOnScrollListener(new CenterScrollListener());
        mRecyclerView.setLayoutManager(scrollZoomLayoutManager);
        RecyclerAdapter adapter = new RecyclerAdapter(R.layout.item_grid,mResults);
        mRecyclerView.setAdapter(adapter);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(adapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                Toast.makeText(MainActivity.this, "onItemClick,item = " + vh.getAdapterPosition(), Toast.LENGTH_SHORT);
                if (vh.getLayoutPosition()!=mResults.size()-1) {
                    itemTouchHelper.startDrag(vh);
//                    VibratorUtil.Vibrate(MainActivity.this, 70);   //震动70ms
                }
            }

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                //TODO.this is test
                try{
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),TestActivity.class);
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initData() {
        for (int i=0;i<3;i++){
            mResults.add(new Item(i*6+0,"Item01", R.mipmap.item1));
            mResults.add(new Item(i*6+1,"Item02", R.mipmap.item2));
            mResults.add(new Item(i*6+2,"Item03", R.mipmap.item3));
            mResults.add(new Item(i*6+3,"Item04", R.mipmap.item4));
            mResults.add(new Item(i*6+4,"Item05", R.mipmap.item5));
            mResults.add(new Item(i*6+5,"Item06", R.mipmap.item6));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startResumeAnimation();
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void startResumeAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<ValueAnimator> animators = new ArrayList<>();
        final int width = mRecyclerView.getWidth();
        for (int i = 0; i < mRecyclerView.getChildCount(); i++){
            final View child = mRecyclerView.getChildAt(i);
            final ValueAnimator va = ValueAnimator.ofFloat(0, 1.0f);
            final int j = i;
            va.setDuration(1000);
            child.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            child.setTranslationX(width + child.getLeft());
            child.setScaleX(1);
            child.setScaleY(1);
            child.setRotationY(0);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float)animation.getAnimatedValue();
                    int trans = width + child.getLeft();
                    child.setTranslationX(trans * (1 - value) );
                    float scale = scrollZoomLayoutManager.calculateScale((int)(trans * (1 - value)) + child.getLeft());
                    float rotation = scrollZoomLayoutManager.calculateRotation((int)(trans * (1 - value)) + child.getLeft());
                    child.setScaleX(scale);
                    child.setScaleY(scale);
                    child.setRotationY(rotation);
                    mRecyclerView.setAlpha(value * value);
                }
            });
            va.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    child.setLayerType(View.LAYER_TYPE_NONE, null);
                }

            });
            animators.add(va);
        }
        EaseCubicInterpolator interpolator = new EaseCubicInterpolator(0,0,.58f,1);
        for (int i = 0; i < animators.size(); i++) {
            animatorSet.playTogether(animators.get(i));
        }
        animatorSet.setInterpolator(interpolator);
        animatorSet.start();
    }

}

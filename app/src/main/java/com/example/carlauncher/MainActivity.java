package com.example.carlauncher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.example.carlauncher.entity.CarItem;
import com.example.carlauncher.helper.MyItemTouchCallback;
import com.example.carlauncher.helper.OnRecyclerItemClickListener;
import com.example.carlauncher.utils.EaseCubicInterpolator;
import java.util.ArrayList;
import java.util.List;
import rouchuan.customlayoutmanager.CenterScrollListener;

public class MainActivity extends Activity {

    private SparseArray<CarItem> mCarItems = new SparseArray<CarItem>();
    RecyclerView mCarRecyclerView;
    private ScrollZoomLayoutManager scrollZoomLayoutManager;
    LauncherModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mModel = new LauncherModel();
        initData();
        mCarRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        int space = getResources().getDimensionPixelSize(R.dimen.item_space);
        scrollZoomLayoutManager = new ScrollZoomLayoutManager(this, space);
        mCarRecyclerView.addOnScrollListener(new CenterScrollListener());
        mCarRecyclerView.setLayoutManager(scrollZoomLayoutManager);
        RecyclerAdapter adapter = new RecyclerAdapter(R.layout.item_car, mCarItems);
        mCarRecyclerView.setAdapter(adapter);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(adapter));
        itemTouchHelper.attachToRecyclerView(mCarRecyclerView);

        mCarRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mCarRecyclerView) {
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                Toast.makeText(MainActivity.this, "onItemClick,item = " + vh.getAdapterPosition(), Toast.LENGTH_SHORT);
                if (vh.getLayoutPosition() != mCarItems.size()-1) {
                    itemTouchHelper.startDrag(vh);
//                    VibratorUtil.Vibrate(MainActivity.this, 70);   //震动70ms
                }
            }

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                int postion = vh.getAdapterPosition();
                Intent intent = mCarItems.get(postion).getIntent();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initData() {
        List<ResolveInfo> apps = mModel.getAllApps(this);
        int maxCount = apps.size() > 30 ? 30 : apps.size();
        PackageManager pm = getPackageManager();
        for (int i = 0;i < maxCount; i++){
            ResolveInfo info = apps.get(i);
            if (info.activityInfo.packageName.equals(getPackageName())) continue;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
            CarItem item = new CarItem(info.loadLabel(pm).toString(), intent);
            item.setIconRes(R.mipmap.main_set_icon_n);
            mCarItems.put(i, item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startResumeAnimation();
        mCarRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCarRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void startResumeAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<ValueAnimator> animators = new ArrayList<>();
        final int width = mCarRecyclerView.getWidth();
        for (int i = 0; i < mCarRecyclerView.getChildCount(); i++){
            final View child = mCarRecyclerView.getChildAt(i);
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
                    mCarRecyclerView.setAlpha(value * value);
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

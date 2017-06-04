package com.example.carlauncher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.util.SparseArray;
import com.example.carlauncher.entity.BenchItem;
import com.example.carlauncher.entity.CarItem;
import com.example.carlauncher.helper.MyItemTouchCallback;
import com.example.carlauncher.helper.OnRecyclerItemClickListener;
import com.example.carlauncher.utils.EaseCubicInterpolator;
import com.example.carlauncher.utils.VibratorUtil;

import java.util.ArrayList;
import java.util.List;
import rouchuan.customlayoutmanager.CenterScrollListener;

public class MainActivity extends Activity implements View.OnClickListener, Callback{

    private SparseArray<CarItem> mCarItems = new SparseArray<CarItem>();
    RecyclerView mCarRecyclerView;
    private LinearLayout mBenchView;
    private ImageView mMenuView;
    private ImageView mHomeView;
    private ScrollZoomLayoutManager scrollZoomLayoutManager;
    private RecyclerAdapter mAdapter;
    private LauncherModel mModel;
    private int mBenchHeight;
    private boolean mBenchMenuVisible;
    private boolean mIsAnimating = false;
    private final int ITEM_COUNT_IN_PAGE = 6;
    private int mItemSpace;
    private int mStartLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        mModel = new LauncherModel();
        bindData(this);
        mCarRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mItemSpace = getResources().getDimensionPixelSize(R.dimen.item_space);
        mStartLeft = getResources().getDimensionPixelSize(R.dimen.recycleview_start_left);
        scrollZoomLayoutManager = new ScrollZoomLayoutManager(this, mStartLeft, mItemSpace);
        scrollZoomLayoutManager.setRecycleView(mCarRecyclerView);
        final CenterScrollListener scrollListener = new CenterScrollListener(scrollZoomLayoutManager);
        mCarRecyclerView.addOnScrollListener(scrollListener);
        mCarRecyclerView.setLayoutManager(scrollZoomLayoutManager);
        mAdapter = new RecyclerAdapter(R.layout.item_car, mCarItems);
        mAdapter.setCallback(this);
        mCarRecyclerView.setAdapter(mAdapter);
        layout();
        MyItemTouchCallback touchCallback = new MyItemTouchCallback(mAdapter);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        touchCallback.setOnDragListener(new MyItemTouchCallback.OnDragListener() {
            @Override
            public void onFinishDrag() {
                scrollListener.resetPosition(scrollZoomLayoutManager, mCarRecyclerView);
            }
        });
        itemTouchHelper.attachToRecyclerView(mCarRecyclerView);

        mCarRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mCarRecyclerView) {
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                Toast.makeText(MainActivity.this, "onItemClick,item = " + vh.getAdapterPosition(), Toast.LENGTH_SHORT);
                if (vh.getLayoutPosition() != mCarItems.size()-1) {
                    itemTouchHelper.startDrag(vh);
                    VibratorUtil.Vibrate(MainActivity.this, 70);   //震动70ms
                }
            }
        });
    }

    private void layout (){
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        Point smallestSize = new Point();
        Point largestSize = new Point();
        display.getCurrentSizeRange(smallestSize, largestSize);
        Point realSize = new Point();
        display.getRealSize(realSize);
        // The real size never changes. smallSide and largeSide will remain the
        // same in any orientation.
        int smallSide = Math.min(realSize.x, realSize.y);
        int largeSide = Math.max(realSize.x, realSize.y);
        int itemWidth = (largeSide - 2 * mStartLeft - (ITEM_COUNT_IN_PAGE - 1) * mItemSpace) / ITEM_COUNT_IN_PAGE;
        int itemHeight = (int)(itemWidth * 1.2f);
        mAdapter.setItemSize(itemWidth, itemHeight);
    }

    private void setupViews () {
        mBenchView = (LinearLayout) findViewById(R.id.bench);
        mMenuView = (ImageView)findViewById(R.id.menu);
        mMenuView.setOnClickListener(this);
        mHomeView = (ImageView)findViewById(R.id.home);
        mHomeView.setOnClickListener(this);
        mBenchHeight = getResources().getDimensionPixelSize(R.dimen.bench_height);
        mBenchView.setTranslationY(mBenchHeight);
    }

    private void bindData(Context context){
        initData();
        initBench(context);
    }

    private void initData() {
        List<ResolveInfo> apps = mModel.getAllApps(this);
        int maxCount = apps.size();
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

    private void initBench (Context context) {
        BenchItem[] items = {
                new BenchItem(R.string.item_bt, R.drawable.list_bt_),
                new BenchItem(R.string.item_style, R.drawable.list_skin_),
                new BenchItem(R.string.item_favorite, R.drawable.list_love_),
                new BenchItem(R.string.item_music, R.drawable.list_music_),
                new BenchItem(R.string.item_help, R.drawable.list_help_)
        };
        for (int i = 0; i < items.length; i++) {
            BenchItemView view = (BenchItemView) LayoutInflater.from(context).inflate(R.layout.bench_item, mBenchView, false);
            view.setItem(items[i]);
            mBenchView.addView(view);
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
            va.setDuration(900);
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
                    child.setScaleX(1);
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
        animatorSet.setStartDelay(100);
        animatorSet.start();
    }

    private void showBenchMenu(){
        mBenchMenuVisible = true;
        mIsAnimating = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                mBenchView.setTranslationY(mBenchHeight * (1 - value));
                mCarRecyclerView.setScaleX(0.8f + 0.2f * (1 - value));
                mCarRecyclerView.setScaleY(0.8f + 0.2f * (1 - value));
                mMenuView.setAlpha(1 - value);
                mHomeView.setAlpha(value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mMenuView.setVisibility(View.VISIBLE);
                mHomeView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnimating = false;
                mMenuView.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private void hideBenchMenu(){
        mBenchMenuVisible = false;
        mIsAnimating = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                mBenchView.setTranslationY(mBenchHeight * value);
                mCarRecyclerView.setScaleX(0.8f + 0.2f * value);
                mCarRecyclerView.setScaleY(0.8f + 0.2f * value);
                mMenuView.setAlpha(value);
                mHomeView.setAlpha(1 - value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mMenuView.setVisibility(View.VISIBLE);
                mHomeView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnimating = false;
                mHomeView.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu){
            if (!mIsAnimating) {
                showBenchMenu();
            }
        } else if (v.getId() == R.id.home) {
            if (!mIsAnimating) {
                hideBenchMenu();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!mIsAnimating) {
            if (mBenchMenuVisible) {
                hideBenchMenu();
            }
        }
    }

    @Override
    public void onClickCallback(RecyclerView.ViewHolder holder, int position) {
//        int postion = vh.getAdapterPosition();
        Intent intent = mCarItems.get(position).getIntent();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}

interface Callback {
    public void onClickCallback(RecyclerView.ViewHolder holder, int position);
}
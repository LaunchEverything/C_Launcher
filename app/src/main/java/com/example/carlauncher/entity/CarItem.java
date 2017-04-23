package com.example.carlauncher.entity;

import android.content.Intent;

/**
 * Created by Administrator on 2017/4/20.
 */

public class CarItem {

    private String mTitle;
    private Intent mIntent;
    private int mIconRes;

    public CarItem (String title, Intent intent) {
        this.mTitle = title;
        this.mIntent = intent;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Intent getIntent () {
        return mIntent;
    }

    public void setIntent(Intent intent) {
        this.mIntent = intent;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public void setIconRes(int iconRes) {
        this.mIconRes = iconRes;
    }
}

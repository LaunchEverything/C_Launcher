package com.example.carlauncher.entity;

import android.content.Intent;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/12.
 */
public class Item implements Serializable{
    private int id;
    private String name;
    private int img;
    private Intent intent;

    public Item(int id, String name, int img) {
        this.id = id;
        this.name = name;
        this.img = img;
    }

    public Item(String name, int img) {
        this.name = name;
        this.img = img;
    }

    public Item (String name, Intent intent) {
        this.name = name;
        this.intent = intent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent () {
        return intent;
    }
}

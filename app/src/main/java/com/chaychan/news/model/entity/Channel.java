package com.chaychan.news.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class Channel implements MultiItemEntity,Serializable {
    public static final int TYPE_MY = 1;
    public static final int TYPE_OTHER = 2;
    public static final int TYPE_MY_CHANNEL = 3;
    public static final int TYPE_OTHER_CHANNEL = 4;
    public static final int TYPE_FIXED_MY_CHANNEL = 5;
    public String title;
    public String channelCode;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int itemType;

    public Channel(String title, String channelCode) {
        this(TYPE_MY_CHANNEL, title, channelCode);
    }

    public Channel(int type, String title, String channelCode) {
        this.title = title;
        this.channelCode = channelCode;
        setItemType(type);
    }


    @Override
    public int getItemType() {
        return itemType;
    }
}
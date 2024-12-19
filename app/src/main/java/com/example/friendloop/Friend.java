package com.example.friendloop;

import android.net.Uri;

public class Friend {
    private String mName;
    private Uri mPicture;

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public Uri getPicture()
    {
        return mPicture;
    }

    public void setPicture(Uri thumbnail)
    {
        this.mPicture = thumbnail;
    }
}

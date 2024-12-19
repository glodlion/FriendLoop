package com.example.friendloop;

import android.net.Uri;

public class Friend {
    private Uri mPicture;
    private String mName;
    private String mPhone;
    private String mBirthday;

    public Uri getPicture()
    {
        return mPicture;
    }

    public void setPicture(Uri thumbnail)
    {
        this.mPicture = thumbnail;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public String getPhone()
    {
        return mPhone;
    }

    public void setPhone(String phone)
    {
        this.mPhone = phone;
    }

    public String getBirthday()
    {
        return mBirthday;
    }

    public void setBirthday(String birthday)
    {
        this.mBirthday = birthday;
    }
}

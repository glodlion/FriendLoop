package com.example.friendloop;

import android.net.Uri;

public class Friend {
    private String mPicture;
    private String mName;
    private String mPhone;
    private String mBirthday;
    private String mPreferences;

    public Friend() {
        this.mPicture = "android.resource://com.example.friendloop/" + R.drawable.ic_android_;
        this.mName = "Default Name";
        this.mPhone = "000-000-0000";
        this.mBirthday = "2000-01-01";
        this.mPreferences = "Default Preferences";
    }

    public Friend( String name ,String picture, String phone, String birthday, String preferences) {
        this.mPicture = picture;
        this.mName = name;
        this.mPhone = phone;
        this.mBirthday = birthday;
        this.mPreferences = preferences;
    }
    public String getPicture()
    {
        return mPicture;
    }

    public void setPicture(String thumbnail)
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

    public String getPreferences()
    {
        return mPreferences;
    }

    public void setPreferences(String birthday)
    {
        this.mPreferences = birthday;
    }

}

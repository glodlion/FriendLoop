package com.example.friendloop;

public class Friend {
    private String mPicture;
    private String mName;
    private String mPhone;
    private String mBirthday;
    private String mPreferences;
    private String mIntimacy;

    public Friend() {
        this.mPicture = "android.resource://com.example.friendloop/" + R.drawable.ic_android_;
        this.mName = "Default Name";
        this.mPhone = "000-000-0000";
        this.mBirthday = "2000-01-01";
        this.mPreferences = "Default Preferences";
        this.mIntimacy = "100";
    }

    public Friend( String name ,String picture, String phone, String birthday, String preferences, String intimacy) {
        this.mPicture = picture;
        this.mName = name;
        this.mPhone = phone;
        this.mBirthday = birthday;
        this.mPreferences = preferences;
        this.mIntimacy = intimacy;
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

    public void setPreferences(String preferences)
    {
        this.mPreferences = preferences;
    }

    public String getIntimacy()
    {
        return mIntimacy;
    }

    public void setIntimacy(String intimacy)
    {
        this.mIntimacy = intimacy;
    }
}

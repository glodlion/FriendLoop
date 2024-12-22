package com.example.friendloop;

public class FriendItem {
    public String name;
    public String photo;
    public String phone;
    public String birthday;
    public String preferences;

    // 建構方法：完整參數
    public FriendItem(String name, String photo, String phone, String birthday, String preferences) {
        this.name = name;
        this.photo = photo;
        this.phone = phone;
        this.birthday = birthday;
        this.preferences = preferences;
    }


    // 建構方法：使用預設值
    public FriendItem() {
        this("Unknown", "None", "000-000-0000", "1970-01-01", "None");
    }

    public void DebugLog() {
        System.out.println("FriendItem DebugLog:");
        System.out.println("Name: " + name);
        System.out.println("Photo: " + photo);
        System.out.println("Phone: " + phone);
        System.out.println("Birthday: " + birthday);
        System.out.println("Preferences: " + preferences);
    }
}

package com.example.friendloop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SqlDataBaseHelper extends SQLiteOpenHelper {

    // 資料庫名稱與版本
    private static final String DATABASE_NAME = "FriendLoop.db"; // 資料庫名稱
    private static final int DATABASE_VERSION = 1; // 資料庫版本

    // 資料表名稱與欄位
    public static final String TABLE_CONTACTS = "friends";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IMAGE_URI = "imageUri";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_BIRTHDAY = "birthday";
    public static final String COLUMN_PREFERENCES = "preferences";

    // 建立資料表的 SQL 語句
    public static final String CREATE_TABLE_CONTACTS =
            "CREATE TABLE " + TABLE_CONTACTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_IMAGE_URI + " TEXT, " +  // 新增此欄位
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_BIRTHDAY + " TEXT, " +
                    COLUMN_PREFERENCES + " TEXT);";

    public SqlDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    // 1. 取出所有詳細資料
    public Cursor getAllContacts() {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    // 2. 取出所有人的部分資訊（只取 Name 和 Phone , 還有）
    public Cursor getContactsSummary() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + COLUMN_NAME + ", " + COLUMN_PHONE + ", " + COLUMN_IMAGE_URI +
                        " FROM " + TABLE_CONTACTS, null);
    }

    // 3. 單筆取出資料
    public Cursor getContactById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // 4. 單筆修改
    public int updateContact(int id, Friend friend) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, friend.getName());
        values.put(COLUMN_PHONE, friend.getPhone());
        values.put(COLUMN_BIRTHDAY, friend.getBirthday());
        values.put(COLUMN_PREFERENCES, friend.getPreferences());
        values.put(COLUMN_IMAGE_URI, friend.getPicture());

        return db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 5. 單筆新增
    public long addContact(Friend friend) {
        long result = -1;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, friend.getName());
            values.put(COLUMN_PHONE, friend.getPhone());
            values.put(COLUMN_BIRTHDAY, friend.getBirthday());
            values.put(COLUMN_IMAGE_URI , friend.getPicture());
            values.put(COLUMN_PREFERENCES, friend.getPreferences());
            result = db.insert(TABLE_CONTACTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return result;
    }


    // 6. 單筆刪除
    public void deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }



}


package com.example.friendloop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import android.Manifest;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {
    RecyclerView mFriendRecyclerView;
    protected HippoCustomRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Friend> mDataset = new ArrayList<>();
    protected LayoutManagerType mCurrentLayoutManagerType;
    protected SqlDataBaseHelper mSqlDataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 檢查權限
        if (!checkPermissions()) {
            requestPermissions();
        }

        mSqlDataBaseHelper = new SqlDataBaseHelper(this);
//        mSqlDataBaseHelper.resetTable(); //當資料表變動時執行一次
        initRecyclerView();
        initRecycleView(savedInstanceState);
        initSharedPreferences();
        initFloatingActionButton();
    }

    private enum LayoutManagerType
    {
        LINEAR_LAYOUT_MANAGER
    }

    private boolean checkPermissions() {
        // 檢查相機和儲存權限
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        // 請求相機和儲存權限
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 300);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 300) {
            // 處理使用者的授權回應
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 權限已授予
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                // 權限被拒絕
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 初始化SharedPreferences，用來存個人資訊及編輯狀態(個人資訊、朋友資訊)
    private void initSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.contains("state")){
            editor.putString("name", "姓名");
            editor.putString("phone", "電話");
            editor.putString("birthday", "生日");
            editor.putString("preference", "備註");
            editor.putString("picture", "android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground);
        }
        editor.putInt("state", 0); // 狀態值： 1 表示個人資訊頁面，2 表示朋友資訊頁面
        editor.apply();
    }

    private void initRecyclerView() {
        mDataset = new ArrayList<>();
        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(this);
        Cursor cursor = dbHelper.getAllContacts();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Friend friend = new Friend();
                int nameIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_NAME);
                int pictureIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_IMAGE_URI);
                String name = nameIndex < 0 ? "Default Value" : cursor.getString(nameIndex);
                String picture = pictureIndex < 0 ? "Default Value" : cursor.getString(pictureIndex);
                friend.setName(name);
                friend.setPicture(picture);

                //friend.setPhone(cursor.getString(cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_PHONE)));
                //friend.setBirthday(cursor.getString(cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_BIRTHDAY)));
                // 假設每個好友有一個固定圖片
                //friend.setPicture(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_android_));
                mDataset.add(friend);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void addData(String uri, String name, String phone, String birthday, String preference){
        Friend friend = new Friend();
        friend.setPicture(uri);
        friend.setName(name);  //命名
        friend.setPhone(phone);
        friend.setBirthday(birthday);
        friend.setPreferences(preference);
        Log.d("DEBUG", "1 Dataset size: " + mDataset.size());
        mDataset.add(friend);
        Log.d("DEBUG", "2 Dataset size: " + mDataset.size());
        // 通知 RecyclerView 更新新增的項目
        mAdapter.notifyDataSetChanged();
        mFriendRecyclerView.scrollToPosition(mDataset.size() - 1);
    }

    private void initRecycleView(Bundle savedInstanceState)
    {
        mFriendRecyclerView = (RecyclerView) findViewById(R.id.friendRecyclerView);
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null)
        {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState.getSerializable("EXTRA_KEY_LAYOUT_MANAGER");
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        //  TO DO 動態載入自定義之 HippoCustomRecyclerViewAdapter 物件mAdapter，包含自訂UI friend_list_item.xml。
        mAdapter = new HippoCustomRecyclerViewAdapter(MainActivity.this, mDataset);
        mFriendRecyclerView.setAdapter(mAdapter);
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType)
    {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mFriendRecyclerView.getLayoutManager() != null)
        {
            scrollPosition = ((LinearLayoutManager) mFriendRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }


        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mFriendRecyclerView.setLayoutManager(mLayoutManager);
        mFriendRecyclerView.scrollToPosition(scrollPosition);
    }

    private void initFloatingActionButton(){
        FloatingActionButton mAddFriendFab = findViewById(R.id.addFriendFab);
        mAddFriendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "add friend", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    }

    // 接收回傳資料
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == 100) {
            // 取得資料

            String uri = data.getStringExtra("picture");
            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            String birthdayString = data.getStringExtra("birthday");
            String preference = data.getStringExtra("preference");

            Friend friend = new Friend(name, uri , phone , birthdayString , "hehe");

            mSqlDataBaseHelper.addContact(friend);
          
            addData(uri, name, phone, birthdayString, preference);

        }
    }

    public void onPersonalClick(View view) {
        // 修改 SharedPreferences 中的 state 值
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("state", 1); // 修改狀態為 1，表示進入個人頁面
        editor.apply(); // 提交變更（異步儲存）

        Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
        startActivity(intent);
    }

    public void onQrClick(View view) {
        Intent intent = new Intent(MainActivity.this, QrActivity.class);
        startActivity(intent);
    }

}
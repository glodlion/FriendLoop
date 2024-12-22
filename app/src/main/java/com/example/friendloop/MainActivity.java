package com.example.friendloop;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

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

        mSqlDataBaseHelper = new SqlDataBaseHelper(this);
//        mSqlDataBaseHelper.resetTable(); //當資料表變動時執行一次
        initRecyclerView();
        initRecycleView(savedInstanceState);

        initFloatingActionButton();

    }

    private enum LayoutManagerType
    {
        LINEAR_LAYOUT_MANAGER
    }

    private void initRecyclerView() {
        mDataset = new ArrayList<>();
        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(this);
        Cursor cursor = dbHelper.getAllContacts();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Friend friend = new Friend();
                int columnIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_NAME);
                String name = columnIndex < 0 ? "Default Value" : cursor.getString(columnIndex);
                friend.setName(name);

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
        Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
        startActivity(intent);
    }

    public void onQrClick(View view) {
        Intent intent = new Intent(MainActivity.this, QrActivity.class);
        startActivity(intent);
    }

}
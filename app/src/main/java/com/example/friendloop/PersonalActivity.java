package com.example.friendloop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class PersonalActivity extends AppCompatActivity {

    ImageView mPersonalPicture, mImageIntimacy;
    TextView mPersonalName, mPersonalPhone, mPersonalBirthday, mPersonalPreferences, mPersonalIntimacy, mTitle;
    String name, phone, birthday, preference, picture, intimacy;
    int state, pos;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        LoadStateData();
    }

    private void init(){
        mImageIntimacy = findViewById(R.id.imageIntimacy);
        mPersonalPicture = findViewById(R.id.personalPicture);
        mPersonalName = findViewById(R.id.personalName);
        mPersonalPhone = findViewById(R.id.personalPhone);
        mPersonalBirthday = findViewById(R.id.personalBirthday);
        mPersonalPreferences = findViewById(R.id.personalPreferences);
        mPersonalIntimacy = findViewById(R.id.friendIntimacy);
        mTitle = findViewById(R.id.textView2);
    }

    private void LoadStateData(){
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        state = sharedPreferences.getInt("state", 0);
        Log.d("Debug", "State: "+state);
        if(state == 1){
            State1();
        } else if(state == 2){
            State2();
        }
    }

    private void State1(){
        name = sharedPreferences.getString("name", "");
        phone = sharedPreferences.getString("phone", "");
        birthday = sharedPreferences.getString("birthday", "");
        preference = sharedPreferences.getString("preference", "");
        String path = "android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground;
        picture = sharedPreferences.getString("picture", path);
        Log.d("Debug", "image: "+picture);

        mTitle.setText("個人資訊");
        try{
            Glide.with(this)
                    .load(Uri.parse(picture))
                    .override(500, 500)
                    .into(mPersonalPicture);
        } catch (Exception e){
            mPersonalPicture.setImageURI(Uri.parse(path));
        }
        mPersonalName.setText(name);
        mPersonalPhone.setText(phone);
        mPersonalBirthday.setText(birthday);
        mPersonalPreferences.setText(preference);
        mImageIntimacy.setVisibility(View.INVISIBLE);
    }

    private void State2(){
        mTitle.setText("朋友資訊");
        // 從 Intent 中取得傳遞的資料
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // 確保 pos 值不為空
            pos = bundle.getInt("pos")+1;
            SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(this);
            Cursor cursor = dbHelper.getContactById(pos);

            // 確保 Cursor 不為 null 且有資料
            if (cursor != null && cursor.moveToFirst()) {
                // 取出欄位的索引
                int nameIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_NAME);
                int pictureIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_IMAGE_URI);
                int phoneIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_PHONE);
                int birthdayIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_BIRTHDAY);
                int preferenceIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_PREFERENCES);
                int intimacyIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_INTIMACY);

                // 根據索引取得資料
                name = (nameIndex >= 0) ? cursor.getString(nameIndex) : "Default Name";
                picture = (pictureIndex >= 0) ? cursor.getString(pictureIndex) : "Default Picture";
                phone = (phoneIndex >= 0) ? cursor.getString(phoneIndex) : "Default Phone";
                birthday = (birthdayIndex >= 0) ? cursor.getString(birthdayIndex) : "Default Birthday";
                preference = (preferenceIndex >= 0) ? cursor.getString(preferenceIndex) : "Default Preference";
                intimacy = (intimacyIndex >= 0) ? cursor.getString(intimacyIndex) : "0";
                // 關閉 Cursor
                cursor.close();
                String path = "android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground;
                try{
                    Glide.with(this)
                            .load(Uri.parse(picture))
                            .override(500, 500)
                            .into(mPersonalPicture);
                } catch (Exception e){
                    mPersonalPicture.setImageURI(Uri.parse(path));
                }
                mPersonalName.setText(name);
                mPersonalPhone.setText(phone);
                mPersonalBirthday.setText(birthday);
                mPersonalPreferences.setText(preference);
                mPersonalIntimacy.setText(intimacy);
                mImageIntimacy.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onCancelClick(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("state", 0); // 狀態值： 0表示回主頁面，1 表示個人資訊頁面，2 表示朋友資訊頁面
        editor.apply();
        finish();
    }

    public void onChangeInfoClick(View view) {
        Intent intent = new Intent(PersonalActivity.this, EditInfomation.class);
        Bundle bundle = new Bundle();
        bundle.putInt("pos", pos);
        bundle.putString("name", name);
        bundle.putString("picture", picture);
        bundle.putString("phone", phone);
        bundle.putString("birthday", birthday);
        bundle.putString("preference", preference);
        bundle.putString("intimacy", intimacy);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(state == 1){
            State1();
        } else if (state == 2){
            State2();
        }
    }
}
package com.example.friendloop;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.util.Calendar;

public class EditInfomation extends AppCompatActivity {
    Button mChooseBirthday;
    EditText mChangeInfoName, mChangeInfoPhone, mChangeInfoPreferences;
    TextView mChangeInfoBirthday, mTitle;
    ImageView mChangeInfoPicture;
    Uri uri;
    SharedPreferences sharedPreferences;
    String name, phone, birthday, preference, picture;
    int state, pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_infomation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        state = sharedPreferences.getInt("state", 0);

        init();
        LoadStateData();
        mChooseBirthday.setOnClickListener(view -> showDatePickerDialog());
    }

    private void init(){
        mChooseBirthday = findViewById(R.id.chooseBirthday);
        mChangeInfoName = findViewById(R.id.changeInfoName);
        mChangeInfoPhone = findViewById(R.id.changeInfoPhone);
        mChangeInfoBirthday = findViewById(R.id.changeInfoBirthday);
        mChangeInfoPreferences = findViewById(R.id.changeInfoPreferences);
        mChangeInfoPicture = findViewById(R.id.changeInfoPicture);
        mTitle = findViewById(R.id.textView2);
    }

    private void LoadStateData(){
        Log.d("Debug", "TEST: "+state);
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
        uri = Uri.parse(picture);

        mTitle.setText("編輯個人資訊");
        try{
            Glide.with(this)
                    .load(Uri.parse(picture))
                    .override(500, 500)
                    .into(mChangeInfoPicture);
        } catch (Exception e){
            mChangeInfoPicture.setImageURI(Uri.parse(path));
        }
        mChangeInfoName.setText(name);
        mChangeInfoPhone.setText(phone);
        mChangeInfoBirthday.setText(birthday);
        mChangeInfoPreferences.setText(preference);
    }

    private void State2(){
        mTitle.setText("編輯朋友資訊");

        // 從 Intent 中取得傳遞的資料
        Bundle bundle = getIntent().getExtras();

        pos = bundle.getInt("pos");
        name = bundle.getString("name");
        phone = bundle.getString("phone");
        birthday = bundle.getString("birthday");
        picture = bundle.getString("picture");
        preference = bundle.getString("preference");
        uri = Uri.parse(picture);

        String path = "android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground;
        try{
            Glide.with(this)
                    .load(Uri.parse(picture))
                    .override(500, 500)
                    .into(mChangeInfoPicture);
        } catch (Exception e){
            mChangeInfoPicture.setImageURI(Uri.parse(path));
        }
        mChangeInfoName.setText(name);
        mChangeInfoPhone.setText(phone);
        mChangeInfoBirthday.setText(birthday);
        mChangeInfoPreferences.setText(preference);
    }

    public void onPictureClick(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 使用 DatePickerDialog 讓用戶選擇日期
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // 顯示選擇的日期
            String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            mChangeInfoBirthday.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void savePersonalInfo(){
        if(uri == null){
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground);
        }
        String name = mChangeInfoName.getText().toString();
        String phone = mChangeInfoPhone.getText().toString();
        String birthdayString = mChangeInfoBirthday.getText().toString();
        String preference = mChangeInfoPreferences.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("birthday", birthdayString);
        editor.putString("preference", preference);
        editor.putString("picture", uri.toString());
        editor.apply();
    }

    private void saveFriendInfo() {
        if(uri == null){
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground);
        }
        String nameNew = mChangeInfoName.getText().toString();
        String phoneNew = mChangeInfoPhone.getText().toString();
        String birthdayStringNew = mChangeInfoBirthday.getText().toString();
        String preferenceNew = mChangeInfoPreferences.getText().toString();

        Friend friend = new Friend();
        friend.setName(nameNew);
        friend.setPicture(uri.toString());
        friend.setBirthday(birthdayStringNew);
        friend.setPreferences(preferenceNew);
        friend.setPhone(phoneNew);

        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(this);
        // 執行更新操作
        dbHelper.updateContact(pos, friend);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 確保是圖片選擇結果
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 獲取選擇的圖片 URI
            if (data != null) {
                uri = data.getData();
                try {
                    // 使用 URI 來顯示選擇的圖片
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    mChangeInfoPicture.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onCancelClick(View view) {
        finish();
    }

    public void onChangeClick(View view) {
        if(state == 1){
            savePersonalInfo();
        } else if(state == 2){
            // 儲存進資料庫
            saveFriendInfo();
        }
        finish();
    }


}
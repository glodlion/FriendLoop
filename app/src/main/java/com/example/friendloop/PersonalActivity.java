package com.example.friendloop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class PersonalActivity extends AppCompatActivity {

    ImageView mPersonalPicture;
    TextView mPersonalName, mPersonalPhone, mPersonalBirthday, mPersonalPreferences, mTitle;
    String name, phone, birthday, preference, picture;
    int state;
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
        mPersonalPicture = findViewById(R.id.personalPicture);
        mPersonalName = findViewById(R.id.personalName);
        mPersonalPhone = findViewById(R.id.personalPhone);
        mPersonalBirthday = findViewById(R.id.personalBirthday);
        mPersonalPreferences = findViewById(R.id.personalPreferences);
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
    }

    private void State2(){
        mTitle.setText("朋友資訊");





    }

    public void onCancelClick(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("state", 0); // 狀態值： 0表示回主頁面，1 表示個人資訊頁面，2 表示朋友資訊頁面
        editor.apply();
        finish();
    }

    public void onChangeInfoClick(View view) {
        Intent intent = new Intent(PersonalActivity.this, EditInfomation.class);
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
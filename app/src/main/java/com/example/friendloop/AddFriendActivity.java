package com.example.friendloop;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddFriendActivity extends AppCompatActivity {
    Button mConfirm, mCancel, mChooseBirthday;
    EditText mAddActivityFriendName, mAddActivityFriendPhone;
    TextView mAddActivityFriendBirthday;
    ImageView mFriendPicture;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_friend);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        changeFriendPicture();
        buttonEvent();

        mChooseBirthday.setOnClickListener(view -> showDatePickerDialog());
    }

    private void init(){
        mConfirm = findViewById(R.id.confirm);
        mCancel = findViewById(R.id.cancel);
        mChooseBirthday = findViewById(R.id.chooseBirthday);
        mAddActivityFriendName = findViewById(R.id.addActivityFriendName);
        mAddActivityFriendPhone = findViewById(R.id.addActivityFriendPhone);
        mAddActivityFriendBirthday = findViewById(R.id.addActivityFriendBirthday);
        mFriendPicture = findViewById(R.id.addActivityFriendPicture);
        mFriendPicture.setImageResource(R.drawable.ic_launcher_foreground);
    }

    private void changeFriendPicture(){
        mFriendPicture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (ContextCompat.checkSelfPermission(AddFriendActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddFriendActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, 1);
                }
                return true;
            }
        });
    }

    private void buttonEvent(){
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Uri uri, String name, String phone, Date birthday
                saveFriendInfo();
                finish();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
            mAddActivityFriendBirthday.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void saveFriendInfo() {
        if(uri == null){
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground);
        }
        String name = mAddActivityFriendName.getText().toString();
        String phone = mAddActivityFriendPhone.getText().toString();
        String birthdayString = mAddActivityFriendBirthday.getText().toString();

        // 將生日字符串轉換為 Date
        Date birthday = null;
        try {
            if (!birthdayString.isEmpty()) {
                // 假設日期格式為 "yyyy-MM-dd"
                birthday = new SimpleDateFormat("yyyy-MM-dd").parse(birthdayString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
//            Toast.makeText(this, "日期格式錯誤", Toast.LENGTH_SHORT).show();
        }
        Log.d("Debug", uri.toString());
        Log.d("Debug", name);
        Log.d("Debug", phone);
        if (birthday != null){
            Log.d("Debug", new SimpleDateFormat("yyyy-MM-dd").format(birthday));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // 用戶拒絕了權限
                Toast.makeText(this, "需要授權才能選擇圖片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if(resultCode == RESULT_OK){
            uri = data.getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                mFriendPicture.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
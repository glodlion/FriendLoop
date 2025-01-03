package com.example.friendloop;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.util.Calendar;

public class AddFriendActivity extends AppCompatActivity {
    Button mChooseBirthday;
    EditText mAddActivityFriendName, mAddActivityFriendPhone, mAddActivityFriendPreferences;
    TextView mAddActivityFriendBirthday;
    ImageView mFriendPicture;
    Uri uri;
    TemData temData;
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

        if (!checkPermissions()) {
            requestPermissions();
        }
        init();
        GetIntentValue();//當網頁跳轉有亂碼時去雲端取資料並自動輸入

        mChooseBirthday.setOnClickListener(view -> showDatePickerDialog());
    }

    /* 以下為新增，為了讀取資料處理 */
    private void GetIntentValue(){

        String id ="";
        // 接收 Intent
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            String qrCodeContent = data.toString(); // 獲取完整的 URI
            id = extractIdFromUri(qrCodeContent); // 提取 ID
        }
        fetchDataFromFirebase(id);
    }

    private String extractIdFromUri(String qrCodeContent) {
        if (qrCodeContent.startsWith("friendloop://friend/add/")) {
            return qrCodeContent.substring("friendloop://friend/add/".length());
        }
        return ""; // 返回空字符串，表示提取失敗
    }

    private void fetchDataFromFirebase(String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Data").child(id);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 將資料轉換為 TemData 對象
                    temData = dataSnapshot.getValue(TemData.class);  //填入資料
                    mAddActivityFriendName.setText(temData.mName);
                    mAddActivityFriendBirthday.setText(temData.mBirthday);
                    mAddActivityFriendPhone.setText(temData.mPhone);
                    mAddActivityFriendPreferences.setText(temData.mPreferences);

                } else {
                    Log.e("Firebase", "資料不存在");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "資料獲取失敗", databaseError.toException());
            }
        });
    }

    /* 以上為新增 */

    private void init(){
        mChooseBirthday = findViewById(R.id.chooseBirthday);
        mAddActivityFriendName = findViewById(R.id.addActivityFriendName);
        mAddActivityFriendPhone = findViewById(R.id.addActivityFriendPhone);
        mAddActivityFriendBirthday = findViewById(R.id.addActivityFriendBirthday);
        mAddActivityFriendPreferences = findViewById(R.id.addActivityFriendPreferences);
        mFriendPicture = findViewById(R.id.addActivityFriendPicture);
        mFriendPicture.setImageResource(R.drawable.ic_launcher_foreground);
    }

    public void onPictureClick(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*"); // 選擇圖片類型
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION); // 授予長期訪問權限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 授予讀取權限
        startActivityForResult(intent, 1); // 啟動選擇器
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

        // 設置最大日期為今天
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void saveFriendInfo() {
        if(uri == null){
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground);
        }
        String name = mAddActivityFriendName.getText().toString();
        String phone = mAddActivityFriendPhone.getText().toString();
        String birthdayString = mAddActivityFriendBirthday.getText().toString();
        String preference = mAddActivityFriendPreferences.getText().toString();

        Friend friend = new Friend(name, uri.toString() , phone , birthdayString , preference, "100");

        Log.d("test1", uri.toString());

        SqlDataBaseHelper mSqlDataBaseHelper = new SqlDataBaseHelper(this);
        mSqlDataBaseHelper.addContact(friend);

        MainActivity mainActivity = MainActivity.getInstance();
        mainActivity.initRecyclerView();
        mainActivity.initRecycleView();

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 確保是圖片選擇結果
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                // 獲取選擇的圖片 URI
                uri = data.getData();

                if (uri != null) {
                    try {
                        // 使用 URI 顯示選擇的圖片
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        mFriendPicture.setImageBitmap(bitmap);

                        // 授予長期訪問權限
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void onCancelClick(View view) {
        finish();
    }

    public void onAddClick(View view) {
        saveFriendInfo();
        finish();
    }

    private boolean checkPermissions() {
        // 檢查相機和儲存權限
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        // 請求相機和儲存權限
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.POST_NOTIFICATIONS
                }, 300);
    }



}
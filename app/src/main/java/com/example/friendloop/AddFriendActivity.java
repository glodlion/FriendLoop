package com.example.friendloop;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

        SqlDataBaseHelper mSqlDataBaseHelper = new SqlDataBaseHelper(this);
        mSqlDataBaseHelper.addContact(friend);

        Intent intent = new Intent();
        setResult(100, intent);
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
                    mFriendPicture.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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
}
package com.example.friendloop;

import static com.example.friendloop.QRCodeGenerator.generateQRCode;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QrcodeGenrate extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String key = "";
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrcode_genrate);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 ImageView 和 SharedPreferences
        imageView = findViewById(R.id.imageView);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
    }

    // 上傳資料至 Firebase 並生成 QR Code
    private void uploadDataToFirebase() {
        String name = sharedPreferences.getString("name", "");
        String phone = sharedPreferences.getString("phone", "");
        String birthday = sharedPreferences.getString("birthday", "");
        String preference = sharedPreferences.getString("preference", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Data");
        DatabaseReference newRef = myRef.push();

        TemData temData = new TemData(name, phone, birthday, preference);
        newRef.setValue(temData)
                .addOnSuccessListener(aVoid -> {
                    key = newRef.getKey();
                    Log.d("Firebase", "資料儲存成功，ID: " + key);

                    // 生成 QR Code 並顯示
                    String qrContent = "friendloop://friend/add/" + key;
                    Bitmap bitmap = generateQRCode(qrContent, 100, 100);
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                    imageView.setImageDrawable(drawable);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "資料儲存失敗", e);
                });
    }

    // 刪除資料
    private void deleteDataFromFirebase() {
        if (!key.isEmpty()) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Data");

            myRef.child(key).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "資料刪除成功，Key: " + key);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "資料刪除失敗", e);
                    });
        } else {
            Log.e("Firebase", "無法刪除資料，Key 為空");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 當 Activity 停止時刪除資料
        deleteDataFromFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 當 Activity 恢復時上傳資料
        uploadDataToFirebase();
    }
}

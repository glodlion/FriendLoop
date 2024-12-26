package com.example.friendloop;

import static com.example.friendloop.QRCodeGenerator.generateQRCode;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Debug;
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
    String key = "" ;
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
        //讀取個人資料
        imageView = findViewById(R.id.imageView);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String phone = sharedPreferences.getString("phone", "");
        String birthday = sharedPreferences.getString("birthday", "");
        String preference = sharedPreferences.getString("preference", "");





        //上傳至Firebase
        Log.i("test", "test");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Data");


        DatabaseReference newRef = myRef.push();


        TemData temData = new TemData(name, phone, birthday, preference);


        newRef.setValue(temData)
                .addOnSuccessListener(aVoid -> {
                    key = newRef.getKey();
                    Log.d("Firebase", "資料儲存成功，ID: " +key);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "資料儲存失敗", e);
                });


        Bitmap bitmap =  generateQRCode("friendloop://test" ,100 , 100);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        imageView.setImageDrawable(drawable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 獲取 Firebase Realtime Database 的引用
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Data");

        // 指定要刪除的節點 key

        // 刪除該節點
        myRef.child(key).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "資料刪除成功，Key: " + key);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "資料刪除失敗", e);
                });
    }
}
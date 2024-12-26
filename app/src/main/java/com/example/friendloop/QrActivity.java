package com.example.friendloop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrActivity extends AppCompatActivity {


    Button mButtonScanner;
    Button mButtonGenerate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr);






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mButtonScanner = findViewById(R.id.button3);
        mButtonGenerate = findViewById(R.id.button4);



        mButtonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(QrActivity.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                            .setOrientationLocked(true)
                            .setPrompt("請將 QR Code 對準掃描框")
                            .setCameraId(0)
                            .setBeepEnabled(true)
                            .initiateScan(); // 啟動掃描

            }
        });
        mButtonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QrActivity.this , QrcodeGenrate.class);
                startActivity(intent);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // 顯示掃描結果
                String qrCodeContent = result.getContents();

                if (qrCodeContent.startsWith("friendloop://add")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrCodeContent));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "掃描成功: " + qrCodeContent, Toast.LENGTH_LONG).show();
                }

            } else {
                // 用戶取消掃描
                Toast.makeText(this, "掃描取消", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
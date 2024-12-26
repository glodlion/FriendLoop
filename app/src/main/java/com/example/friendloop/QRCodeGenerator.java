package com.example.friendloop;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {

    // 生成 QR Code 的方法
    public static Bitmap generateQRCode(String text, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            // 生成 QR Code 的 BitMatrix
            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
            );

            // 創建空白的 Bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // 填充 Bitmap 資料
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK :Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}

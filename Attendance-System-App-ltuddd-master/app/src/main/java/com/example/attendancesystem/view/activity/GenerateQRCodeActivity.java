package com.example.attendancesystem.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.attendancesystem.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class GenerateQRCodeActivity extends AppCompatActivity2 {

    private String studentName;

    private String studentId;

    private String courseStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr_code);

        ImageView imageView = findViewById(R.id.imageView);


        Intent intent = getIntent();
        studentName = intent.getStringExtra("name");
        studentId = intent.getStringExtra("mssv");
        courseStudent = intent.getStringExtra("course");
        String withoutDiacritics = removeDiacritics(studentName);

        Bitmap qrCode = generateQRCode(withoutDiacritics,studentId, removeDiacritics(courseStudent));

        imageView.setImageBitmap(qrCode);

    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private Bitmap generateQRCode(String name, String mssv, String course) {
        String currentTime = getCurrentTime();
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        String combinedData = name + "|" + mssv + "|" + course + "|" + currentTime;
        try {
            BitMatrix bitMatrix = barcodeEncoder.encode(combinedData, BarcodeFormat.QR_CODE, 300, 300);

            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
    }

}

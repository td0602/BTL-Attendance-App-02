package com.example.attendancesystem.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancesystem.R;
import com.example.attendancesystem.storage.SaveUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.Normalizer;
import java.util.regex.Pattern;


public class ProcessQRDataActivity extends AppCompatActivity {

    private TextView name,id,desig,depat,tShift;

    private Button button;

    private String studentName;
    private String mssv;

    private String course;

    private String timeString;

    private DatabaseReference presentRef, attendanceRef, absentRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_process_qr_data);

        name=this.findViewById(R.id.studentName);
        id=this.findViewById(R.id.studentId);
        desig=this.findViewById(R.id.studentInfo);
        button = findViewById(R.id.btnConfirm);
        depat = findViewById(R.id.depatCourse);

        String qrData = getIntent().getStringExtra("qrData");
        Log.e("Bello","qrData:" + qrData);

        String[] parts = qrData.split("\\|");


        if (parts.length == 4) {
            studentName = parts[0];
            mssv = parts[1];
            course = parts[2];
            timeString = parts[3];
            Log.e("Bello","name: " + studentName);

            name.setText(studentName);
            id.setText(mssv);
            desig.setText(timeString);
            depat.setText(course);
        } else {
            System.out.println("Dữ liệu không hợp lệ");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attendanceRef=FirebaseDatabase.getInstance().getReference().child("Department").child(new SaveUser().teacher_DeptLoadData(getApplicationContext())).child("Attendance").child(new SaveUser().teacher_ShiftLoadData(getApplicationContext())).child(course).child(timeString);

                presentRef=attendanceRef.child("Present");
                presentRef.child(mssv).setValue(mssv);
                Toast.makeText(ProcessQRDataActivity.this, "Điểm danh sinh viên " + name + "thành công", Toast.LENGTH_SHORT).show();


            }
        });

    }

    private static String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
    }
}

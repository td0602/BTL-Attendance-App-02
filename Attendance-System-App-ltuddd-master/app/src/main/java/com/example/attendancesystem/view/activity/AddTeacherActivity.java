package com.example.attendancesystem.view.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.attendancesystem.R;
import com.example.attendancesystem.model.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import xyz.hasnat.sweettoast.SweetToast;

public class AddTeacherActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 22 ;

    private Toolbar addTeacherToolbar;
    private EditText teacherNameEt,teacherEmailEt,teacherPhoneEt,teacherAddressEt,teacherIdEt;
    private Spinner teacherDesignationSp;
    private String[] desigList;
    private String selectedDesig;
    private Button addTeacherBtn;
    private String intendedDept,intentedShift;
    private DatabaseReference teacherRef;
   private FirebaseStorage storage;
    private StorageReference storageReference;
    private String imageUrl;
    private Uri filePath;
    private ImageView imageAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        addTeacherToolbar=findViewById(R.id.addTeacherToolbar);
        setSupportActionBar(addTeacherToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
         Intent  intent=getIntent();
         intendedDept=intent.getStringExtra("TDEPT");
         intentedShift=intent.getStringExtra("TSHIFT");

        teacherNameEt=findViewById(R.id.addTeacherName);
        teacherEmailEt=findViewById(R.id.addTeacherEmail);
        teacherAddressEt=findViewById(R.id.addTeacherAddress);
        teacherPhoneEt=findViewById(R.id.addTeacherPhone);
        teacherIdEt=findViewById(R.id.addTeacherId);
        addTeacherBtn=findViewById(R.id.addTbtn);
        imageAccount=findViewById(R.id.img_teacher);

        teacherDesignationSp=findViewById(R.id.teacherDesignationSp);

        desigList=getResources().getStringArray(R.array.desig);

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(AddTeacherActivity.this,android.R.layout.simple_list_item_1,desigList);
        teacherDesignationSp.setAdapter(arrayAdapter);
        teacherDesignationSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDesig=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertImageToFireBaseStorage();
            }
        });

        imageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult( Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imageAccount.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void addTeacher(){
        String name=teacherNameEt.getText().toString();
        String email=teacherEmailEt.getText().toString();
        String address=teacherAddressEt.getText().toString();
        String phone=teacherPhoneEt.getText().toString();
        String ID=teacherIdEt.getText().toString();


            teacherRef= FirebaseDatabase.getInstance().getReference().child("Department").child(intendedDept).child("Teacher").child(intentedShift);
            String key=teacherRef.push().getKey();

            Teacher teacher=new Teacher(ID,name,intendedDept,selectedDesig,"",phone,email,"","",address,"","1234",intentedShift,imageUrl);
            teacherRef.child(key).setValue(teacher).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        SweetToast.success(getApplicationContext(),"Teacher data successfully");
                        finish();
                    }
                }
            });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertImageToFireBaseStorage() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(checkvalid() == false){
            return;
        }
        else {
            if (filePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

                // adding listeners on upload
                // or failure of image
                ref.putFile(filePath)
                        .addOnSuccessListener(
                                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                    {
                                        progressDialog.dismiss();
                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageUrl = uri.toString();
                                                addTeacher();
                                            }
                                        });
                                    }
                                })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {

                                progressDialog.dismiss();
                                SweetToast.success(getApplicationContext(),"Failed");
                            }
                        })
                        .addOnProgressListener(
                                new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onProgress(
                                            UploadTask.TaskSnapshot taskSnapshot)
                                    {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                    }
                                });
            }
            else{
                addTeacher();
            }
        }
    }


    private boolean checkvalid() {
        String name=teacherNameEt.getText().toString();
        String email=teacherEmailEt.getText().toString();
        String address=teacherAddressEt.getText().toString();
        String phone=teacherPhoneEt.getText().toString();
        String ID=teacherIdEt.getText().toString();

        if(name.isEmpty()){
            teacherNameEt.setError("Enter teacher name");
            teacherNameEt.requestFocus();
            return false;
        }else  if(email.isEmpty()){
            teacherEmailEt.setError("Enter Email");
            teacherEmailEt.requestFocus();
            return false;

        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            teacherEmailEt.setError("Enter Email");
            teacherEmailEt.requestFocus();
            return false;

        }else if(address.isEmpty()){
            teacherAddressEt.setError("Enter address");
            teacherAddressEt.requestFocus();
            return false;

        }else if(phone.isEmpty()){
            teacherPhoneEt.setError("Enter phone number");
            teacherPhoneEt.requestFocus();
            return false;

        }else if(selectedDesig.isEmpty() && selectedDesig.equals("Select Designation")){
            SweetToast.warning(getApplicationContext(),"Select Designation");
            return false;

        }else if(ID.isEmpty()){
            teacherIdEt.setError("Enter ID");
            teacherIdEt.requestFocus();
            return false;

        }
        return true;
    }
}

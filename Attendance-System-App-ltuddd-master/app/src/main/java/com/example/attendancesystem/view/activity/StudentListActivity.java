package com.example.attendancesystem.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.attendancesystem.ItemTouchHelperListener;
import com.example.attendancesystem.R;
import com.example.attendancesystem.RecyclerViewItemTouchHelper;
import com.example.attendancesystem.adapter.StudentListAdapter;
import com.example.attendancesystem.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import xyz.hasnat.sweettoast.SweetToast;

public class StudentListActivity extends AppCompatActivity implements ItemTouchHelperListener {
   private Toolbar studentListToolbar;
   private FloatingActionButton addStudentBtn;
   private DatabaseReference studentListRef;
    private DatabaseReference studentRef,attendanceRef,coureRef;
   private List<Student> studentList=new ArrayList<>();
   private RecyclerView studentListRV;
   private StudentListAdapter studentListAdapter;

   private RelativeLayout rootView;
    private String intentDept;
    private String intentBatch;
    private String intentShift;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        studentListToolbar=findViewById(R.id.studentListToolbar);
        addStudentBtn=findViewById(R.id.addStudentBtn);
        studentListRV=findViewById(R.id.StudentListRV);
        rootView=findViewById(R.id.root_view);
        Intent intent=getIntent();
        intentDept= intent.getStringExtra("DEPT");
        intentBatch=intent.getStringExtra("BATCH");
        intentShift=intent.getStringExtra("SHIFT");

        setSupportActionBar(studentListToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        studentListRef=FirebaseDatabase.getInstance().getReference().child("Department").child(intentDept).child("Student").child(intentBatch).child("allstudent").child(intentShift);
        studentListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentList.clear();
                if(dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.hasChildren()) {
                            Student student = dataSnapshot1.getValue(Student.class);
                            studentList.add(student);
                        }

                         studentListAdapter=new StudentListAdapter(StudentListActivity.this, studentList, new StudentListAdapter.IClickListener() {
                            @Override
                            public void onClickItem(Student student) {
                                openEditStudent(student);
                            }
                        });
                        studentListRV.setLayoutManager(new LinearLayoutManager(StudentListActivity.this));
                        studentListAdapter.notifyDataSetChanged();
                        studentListRV.setAdapter(studentListAdapter);



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(StudentListActivity.this,AddStudentActivity.class);

                intent1.putExtra("DEPT",intentDept);
                intent1.putExtra("BATCH",intentBatch);
                intent1.putExtra("SHIFT",intentShift);

                startActivity(intent1);
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(studentListRV);
    }

    private void openEditStudent(Student student) {
        Intent intent = new Intent(StudentListActivity.this,EditStudentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("student",student);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof StudentListAdapter.StudentListViewHolder){
            final String nameImageDelete = studentList.get(viewHolder.getAdapterPosition()).getName();

            final Student studentDelete = studentList.get(viewHolder.getAdapterPosition());
            final int indexDelte = viewHolder.getAdapterPosition();


            studentListAdapter.removeItem(indexDelte);
            Snackbar snackbar = Snackbar.make(rootView,nameImageDelete+"remover!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    studentListAdapter.undoItem(studentDelete,indexDelte);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);

            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            deleteStudentFireBase(studentDelete);
                    }
                }


            });
            snackbar.show();

        }
    }

    private void deleteStudentFireBase(final Student studentdl) {
        studentRef= FirebaseDatabase.getInstance().getReference().child("Department").child(intentDept).child("Student").
                child(intentBatch).child("allstudent").child(intentShift);

        studentListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.hasChildren()) {
                            Student studentFB = dataSnapshot1.getValue(Student.class);
                            if(studentFB.getId()== studentdl.getId()){
                                studentRef.child(dataSnapshot1.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        SweetToast.success(getApplicationContext(), " Xóa Thành Công");
                                    }
                                });
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}

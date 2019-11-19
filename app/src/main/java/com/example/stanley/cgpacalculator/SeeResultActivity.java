package com.example.stanley.cgpacalculator;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SeeResultActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    DatabaseReference databaseReference4;
    double tcl = 0, tgp = 0, noCO = 0;
    List<String> course;
    List<String> grade;
    List<String> unit;
    ArrayAdapter<String> adapter1, adapter2, adapter3;


    TextView textViewtcl, textViewtgp, textViewgpa, textViewNoCourse, textViewHonor;

    ListView txtViewCourses, textViewUnits, textViewGrades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_result);
        String sem = getIntent().getStringExtra("semester");
        String lev = getIntent().getStringExtra("level");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(lev + "level " + sem);


        txtViewCourses = findViewById(R.id.courses);
        textViewUnits = findViewById(R.id.units);
        textViewGrades = findViewById(R.id.grades);
        textViewNoCourse = findViewById(R.id.tot_courseR);
        textViewtcl = findViewById(R.id.tclR);
        textViewgpa = findViewById(R.id.gpaR);
        textViewHonor = findViewById(R.id.honorR);
        textViewtgp = findViewById(R.id.tgpR);

        unit = new ArrayList<>();
        course = new ArrayList<>();
        grade = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference2 = firebaseDatabase.getReference();
        databaseReference3 = firebaseDatabase.getReference();
        databaseReference4 = firebaseDatabase.getReference();

        // Create an ArrayAdapter using the string array and a default listview layout
        adapter1 = new ArrayAdapter<String>(this, R.layout.guc, course);
        adapter2 = new ArrayAdapter<String>(this, R.layout.guc, unit);
        adapter3 = new ArrayAdapter<String>(this, R.layout.guc, grade);
        // Apply the adapter to the listviews
        txtViewCourses.setAdapter(adapter1);
        textViewUnits.setAdapter(adapter2);
        textViewGrades.setAdapter(adapter3);


        try {

            databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                    .child("Results").child(lev).child(sem);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        Toast.makeText(SeeResultActivity.this, "No Result Yet ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    tcl = dataSnapshot.child("TCL").getValue(Integer.class);
                    tgp = dataSnapshot.child("TGP").getValue(Integer.class);
                    noCO = dataSnapshot.child("Tcourses").getValue(Integer.class);

                    double gpa = tgp / tcl;
                    DecimalFormat df = new DecimalFormat("#.##");
                    gpa = Double.valueOf(df.format(gpa));

                    System.out.println(course + "Jeez");
                    System.out.println(unit);
                    System.out.println(grade);
                    textViewgpa.setText(gpa + "");
                    textViewHonor.setText(hon(gpa));
                    textViewtcl.setText(tcl + "");
                    textViewNoCourse.setText("" + noCO);
                    textViewtgp.setText("" + tgp);
                    Toast.makeText(SeeResultActivity.this, "" + tcl, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference2 = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                    .child("Results").child(lev).child(sem).child("courses");
            databaseReference2.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String ash = dataSnapshot.getValue(String.class);
                    course.add(ash);
                    adapter1.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    adapter1.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference3 = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                    .child("Results").child(lev).child(sem).child("grades");
            databaseReference3.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String ash = dataSnapshot.getValue(String.class);
                    grade.add(ash);
                    adapter1.notifyDataSetChanged();


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    adapter1.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference4 = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                    .child("Results").child(lev).child(sem).child("units");
            databaseReference4.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    int ash = dataSnapshot.getValue(Integer.class);
                    unit.add(""+ash);
                    adapter1.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    adapter1.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            textViewUnits.setOnScrollListener(new AbsListView.OnScrollListener() {
                private int mLastFirstVisibleItem;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                        textViewUnits.setEnabled(true);
                        Log.i("a", "scrolling stopped...");
                    }

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    textViewUnits.setEnabled(false);


                }
            });
            txtViewCourses.setOnScrollListener(new AbsListView.OnScrollListener() {
                private int mLastFirstVisibleItem;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                        txtViewCourses.setEnabled(true);
                        Log.i("a", "scrolling stopped...");
                    }

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    txtViewCourses.setEnabled(false);


                }
            });
            textViewGrades.setOnScrollListener(new AbsListView.OnScrollListener() {
                private int mLastFirstVisibleItem;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        textViewGrades.setEnabled(true);
                        Log.i("a", "scrolling stopped...");
                    }

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    textViewGrades.setEnabled(false);


                }
            });





        } catch (Exception e) {
            Toast.makeText(this, "Semester is not Available", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    String hon(double gp) {
        String honor = "";
        if (gp >= 4.5) {
            honor = "First Class Honor";
        } else if (gp >= 3.5) {
            honor = "Second Class Upper Honor";

        } else if (gp >= 2.5) {
            honor = "Second Class Lower Honor";
        } else if (gp >= 1.5) {
            honor = "Third Class Honor";
        } else {
            honor = "Pass";

        }

        return honor;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

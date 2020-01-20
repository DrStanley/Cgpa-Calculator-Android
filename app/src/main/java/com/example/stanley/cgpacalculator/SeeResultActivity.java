package com.example.stanley.cgpacalculator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeeResultActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference1;
    DatabaseReference databaseReference2;
    DatabaseReference mdatabaseReference;

    DatabaseReference databaseReference3;
    DatabaseReference databaseReference4;
    DatabaseReference databaseReference5;
    double tcl = 0, tgp = 0, noCO = 0;
    double tcl2 = 0, tgp2 = 0, noCO2 = 0;
    List<String> course;
    List<String> grade;
    List<String> unit;
    ArrayAdapter<String> adapter1, adapter2, adapter3;
    String sem, lev, ash1, ash2, ash3;

    TextView textViewtcl, textViewtgp, textViewgpa, textViewNoCourse, textViewHonor;

    ListView txtViewCourses, textViewUnits, textViewGrades;


    View clickSource;
    View touchSource;

    int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_result);
        sem = getIntent().getStringExtra("semester");
        lev = getIntent().getStringExtra("level");
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

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference1 = firebaseDatabase.getReference();
        databaseReference2 = firebaseDatabase.getReference();
        databaseReference3 = firebaseDatabase.getReference();
        databaseReference4 = firebaseDatabase.getReference();
        mdatabaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                .child("Results").child("CGPA");

        // Create an ArrayAdapter using the string array and a default listview layout
        adapter1 = new ArrayAdapter<String>(this, R.layout.guc, course);
        adapter2 = new ArrayAdapter<String>(this, R.layout.guc, unit);
        adapter3 = new ArrayAdapter<String>(this, R.layout.guc, grade);
        // Apply the adapter to the listviews
        txtViewCourses.setAdapter(adapter1);
        textViewUnits.setAdapter(adapter2);
        textViewGrades.setAdapter(adapter3);


        try {
            mdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            tcl2 = dataSnapshot.child("TCL").getValue(Integer.class);
                            tgp2 = dataSnapshot.child("TGP").getValue(Integer.class);
                            noCO2 = dataSnapshot.child("Total_courses").getValue(Integer.class);
                        }

                    } catch (Exception f) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference1 = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                    .child("Results").child(lev).child(sem);
            databaseReference5 = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                    .child("Results").child(lev);

            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {
                        tcl = dataSnapshot.child("TCL").getValue(Integer.class);
                        tgp = dataSnapshot.child("TGP").getValue(Integer.class);
                        noCO = dataSnapshot.child("Tcourses").getValue(Integer.class);

                    } catch (NullPointerException e) {
                        Toast.makeText(SeeResultActivity.this, "No Result Yet For Selected Semester ", Toast.LENGTH_SHORT).show();
                        System.out.println("Testing 2 cause:" + e.getCause() + "error: " + e.getMessage());
                        finish();
                    }

                    double gpa = tgp / tcl;
                    DecimalFormat df = new DecimalFormat("#.##");
                    gpa = Double.valueOf(df.format(gpa));

                    textViewgpa.setText(gpa + "");
                    textViewHonor.setText(hon(gpa));
                    textViewtcl.setText(tcl + "");
                    textViewNoCourse.setText("" + noCO);
                    textViewtgp.setText("" + tgp);
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
                    ash1 = dataSnapshot.getValue(String.class);
                    course.add(ash1);
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
                    unit.add("" + ash);
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

            listViewsScroll();

        } catch (Exception e) {
            Toast.makeText(this, e.getCause() + " and " + "\t" + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu inflater
        getMenuInflater().inflate(R.menu.save_result, menu);
        return super.onCreateOptionsMenu(menu);
    }

    String hon(double gp) {
        String honor = "";
        if (gp >= 4.5) {
            honor = "First Class";
        } else if (gp >= 3.5) {
            honor = "Second Class Upper";

        } else if (gp >= 2.5) {
            honor = "Second Class Lower";
        } else if (gp >= 1.5) {
            honor = "Third Class";
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
        } else if (id == R.id.delete) {

            AlertDialog alertDialog = new AlertDialog.Builder(SeeResultActivity.this).create();
            alertDialog.setTitle("Delete Result");
            alertDialog.setIcon(R.drawable.ic_error_black_24dp);
            alertDialog.setMessage("Are you sure you want to Delete this Result.\nIf 'No' press back");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int which) {
                            tgp2 = tgp2 - tgp;
                            tcl2 = tcl2 - tcl;
                            noCO2 = noCO2 - noCO;

                            Map<String, Object> updates2 = new HashMap<String, Object>();
                            updates2.put("TCL", tcl2);
                            updates2.put("TGP", tgp2);
                            updates2.put("Total_courses", noCO2);
                            mdatabaseReference.updateChildren(updates2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        databaseReference1.removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                dialog.dismiss();
                                                Toast.makeText(SeeResultActivity.this,
                                                        "Result Deleted", Toast.LENGTH_LONG).show();

                                                databaseReference5.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue() == null && dataSnapshot.getChildrenCount() == 0) {

                                                            final DatabaseReference dataReference;
                                                            dataReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                                                                    .child("Results").child("Levels");
                                                            dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    System.out.println(dataSnapshot.getChildren());
                                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                        String xx = child.getKey();
                                                                        String yy = child.getValue(String.class);
                                                                        if (yy.equals(lev)) {
                                                                            dataSnapshot.child(xx).getRef().removeValue();
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

//                                                                    removeValue(new DatabaseReference.CompletionListener() {
//                                                                @Override
//                                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//
//                                                                    dataReference.setValue(null);
//                                                                    Toast.makeText(SeeResultActivity.this,
//                                                                            "Result Deleted Levels", Toast.LENGTH_LONG).show();
//                                                                }
//                                                            });

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
            alertDialog.show();
        }
//        else if (id == R.id.save) {
//            Toast.makeText(this, "Download not Implemented...", Toast.LENGTH_LONG).show();
//
//        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("ClickableViewAccessibility")
    void listViewsScroll() {

        txtViewCourses.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (touchSource == null)
                    touchSource = v;

                if (v == touchSource) {
                    textViewUnits.dispatchTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                }

                return false;
            }
        });
        txtViewCourses.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (touchSource == null)
                    touchSource = v;

                if (v == touchSource) {
                    textViewGrades.dispatchTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                }

                return false;
            }
        });

        textViewUnits.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (touchSource == null)
                    touchSource = v;

                if (v == touchSource) {
                    textViewGrades.dispatchTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                }

                return false;
            }
        });
        textViewUnits.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (touchSource == null)
                    touchSource = v;

                if (v == touchSource) {
                    txtViewCourses.dispatchTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                }

                return false;
            }
        });

        textViewGrades.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (touchSource == null)
                    touchSource = v;

                if (v == touchSource) {
                    txtViewCourses.dispatchTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                }

                return false;
            }
        });
        textViewGrades.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (touchSource == null)
                    touchSource = v;

                if (v == touchSource) {
                    textViewUnits.dispatchTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                }

                return false;
            }
        });


        txtViewCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent == clickSource) {
                    // Do something with the ListView was clicked
                }
            }
        });
        textViewUnits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent == clickSource) {
                    // Do something with the ListView was clicked
                }
            }
        });
        textViewGrades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent == clickSource) {
                    // Do something with the ListView was clicked
                }
            }
        });


        txtViewCourses.setOnScrollListener(new AbsListView.OnScrollListener() {
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

                if (view == clickSource)
                    textViewGrades.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop() + offset);

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

                if (view == clickSource)
                    txtViewCourses.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop() + offset);

            }
        });

        textViewGrades.setOnScrollListener(new AbsListView.OnScrollListener() {
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

                if (view == clickSource)
                    textViewUnits.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop() + offset);

            }
        });


        textViewGrades.setOnScrollListener(new AbsListView.OnScrollListener() {
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

                if (view == clickSource)
                    txtViewCourses.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop() + offset);
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

                if (view == clickSource)
                    textViewGrades.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop() + offset);

            }
        });

        txtViewCourses.setOnScrollListener(new AbsListView.OnScrollListener() {
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

                if (view == clickSource)
                    textViewUnits.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop() + offset);

            }
        });

    }
}

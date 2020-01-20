package com.example.stanley.cgpacalculator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

public class MiaActivity extends AppCompatActivity {
    ArrayList<String> list1 = new ArrayList<String>();
    ArrayList<String> list2 = new ArrayList<String>();
    ArrayList<String> list3 = new ArrayList<String>();
    List<String> course;
    List<String> grade;
    List<String> level;
    List<Integer> unit;
    List<Integer> point;
    private ProgressDialog npd;
    Spinner spinner1, spinner2;
    Spinner[] grades = new Spinner[20];
    TextView textViewName;
    TextView[] num = new TextView[20];
    LinearLayout[] linearLayouts = new LinearLayout[20];
    int a, x;
    long chi;
    int tcl = 0, tgp = 0, noCO = 0;

    double totalUnit, totatPoint;
    Button finish;
    Button sav;
    EditText[] cour = new EditText[20], uni = new EditText[20];
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;
    boolean ext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mia);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        x = 5;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (x < 20) {
                    cour[x].setVisibility(View.VISIBLE);
                    uni[x].setVisibility(View.VISIBLE);
                    grades[x].setVisibility(View.VISIBLE);
                    num[x].setVisibility(View.VISIBLE);
                    linearLayouts[x].setVisibility(View.VISIBLE);
                    x++;
                } else {
                    Snackbar.make(view, "Reached Limit", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        for (int i = 1; i <= 20; i++) {
            String courseID = "cousrse_code_" + i;
            String unitID = "unit_" + i;
            String gradeID = "grade_" + i;
            String numID = "tx_" + i;
            String linID = "lin_" + i;
            int resId = getResources().getIdentifier(courseID, "id", getPackageName());
            int resId2 = getResources().getIdentifier(unitID, "id", getPackageName());
            int resId3 = getResources().getIdentifier(gradeID, "id", getPackageName());
            int resId4 = getResources().getIdentifier(numID, "id", getPackageName());
            int resId5 = getResources().getIdentifier(linID, "id", getPackageName());
            i--;
            cour[i] = findViewById(resId);
            uni[i] = findViewById(resId2);
            grades[i] = findViewById(resId3);
            num[i] = findViewById(resId4);
            linearLayouts[i] = findViewById(resId5);
            i++;
        }
        spinner1 = findViewById(R.id.level_spinner);
        spinner2 = findViewById(R.id.semester_spinner);
        textViewName = findViewById(R.id.nam);
        finish = findViewById(R.id.check);
        sav = findViewById(R.id.save);
        unit = new ArrayList<>();
        point = new ArrayList<>();
        level = new ArrayList<>();
        course = new ArrayList<>();
        grade = new ArrayList<>();

        sav.setVisibility(View.GONE);
        npd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference2 = firebaseDatabase.getReference();
        databaseReference3 = firebaseDatabase.getReference();
        databaseReference2 = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                .child("Results").child("CGPA");
        adds();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Calculate GPA");
        String eamval = getIntent().getStringExtra("aha");
        textViewName.setText(eamval + " me");

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_layout, list1);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_layout, list2);
        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, R.layout.spinner_layout, list3);
        // Apply the adapter to the spinner
        for (int i = 0; i < 20; i++) {
            grades[i].setAdapter(adapter3);
        }
        enab();
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        tcl = dataSnapshot.child("TCL").getValue(Integer.class);
                        tgp = dataSnapshot.child("TGP").getValue(Integer.class);
                        noCO = dataSnapshot.child("Total_courses").getValue(Integer.class);
                    }
                } catch (Exception f) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference3 = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                .child("Results").child("Levels");

        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                chi = dataSnapshot.getChildrenCount();
                databaseReference3.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.getValue() == null) {
                            Toast.makeText(MiaActivity.this, "No Result Yet", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String as = dataSnapshot.getValue(String.class);
                        level.add(as);
                    }

                    @Override
                    public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        sav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResult();
            }
        });
    }

    private void calculate() {
        unit.clear();
        course.clear();
        grade.clear();
        point.clear();
        totatPoint = 0;
        totalUnit = 0;

        for (int i = 0; i < 20; i++) {
            if ((TextUtils.isEmpty(cour[i].getText().toString()) || TextUtils.isEmpty(uni[i].getText().toString())
                    || grades[i].getSelectedItem().toString().equals("Grade")) && (!TextUtils.isEmpty(cour[i].getText().toString())
                    || !TextUtils.isEmpty(uni[i].getText().toString())
                    || !grades[i].getSelectedItem().toString().equals("Grade"))) {
                i++;
                Toast.makeText(this, "Row " + i + " Incomplete ", Toast.LENGTH_SHORT).show();
                i--;
                cour[i].setError("This row");
                cour[i].requestFocus();
                return;
            }

            if (!TextUtils.isEmpty(cour[i].getText().toString()) && !TextUtils.isEmpty(uni[i].getText().toString())
                    && !grades[i].getSelectedItem().toString().equals("Grade")) {
                String cours = cour[i].getText().toString();
                int unix = Integer.parseInt(uni[i].getText().toString());
                String g = grades[i].getSelectedItem().toString();
                unit.add(unix);
                course.add(cours);
                grade.add(g);
            }

        }
        if (spinner1.getSelectedItem().toString().equals("Select Year")) {
            //email is empty
            Toast.makeText(this, "Please Select the Year", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spinner2.getSelectedItem().toString().equals("Select Semester")) {
            //email is empty
            Toast.makeText(this, "Please Select the Semester", Toast.LENGTH_SHORT).show();
            return;
        }
        if (unit.size() <= 0 || grade.size() <= 0) {
            Toast.makeText(this, "Nothing to calculate", Toast.LENGTH_SHORT).show();
        }
        else {

            for (int i = 0; i < grade.size(); i++) {
                int cl = unit.get(i);
                String car = grade.get(i);
                point.add(getPoint(cl, car.charAt(0)));
            }

            for (int t = 0; t < unit.size(); t++) {
                totalUnit += unit.get(t);
                totatPoint += point.get(t);
            }

            double gpa = totatPoint / totalUnit;
            DecimalFormat df = new DecimalFormat("#.##");
            gpa = Double.valueOf(df.format(gpa));

            AlertDialog alertDialog = new AlertDialog.Builder(MiaActivity.this).create();
            alertDialog.setTitle("Calculated");
            alertDialog.setMessage("Your GPA is \n" +
                    gpa + "\nPress save to store for later view");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            sav.setVisibility(View.VISIBLE);
            databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                    .child("Results").child(spinner1.getSelectedItem().toString() + "00")
                    .child(spinner2.getSelectedItem().toString());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("Level") || dataSnapshot.hasChild("TCL")) {
                        ext = true;
                    } else {
                        ext = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }

    private void saveResult() {
        if (unit.size() <= 0 || grade.size() <= 0) {
            Toast.makeText(this, "Nothing to Save", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ext) {
            AlertDialog alertDialog = new AlertDialog.Builder(MiaActivity.this).create();
            alertDialog.setTitle("Sorry!!");
            alertDialog.setIcon(R.drawable.ic_error_black_24dp);
            alertDialog.setMessage("\n" +
                    "The result you about to save already exist.\nPlease delete the previous to upload a new one.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }


        Map<String, Object> updates = new HashMap<String, Object>();
        npd.setMessage("Saving Result Please wait...");
        npd.show();
        npd.setCanceledOnTouchOutside(false);

        a = course.size();
        updates.put("courses", course);
        updates.put("units", unit);
        updates.put("grades", grade);
        updates.put("TCL", totalUnit);
        updates.put("Level", spinner1.getSelectedItem().toString() + "00");
        updates.put("TGP", totatPoint);
        updates.put("Tcourses", a);
        updates.put("grades", grade);
        databaseReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (level.contains(spinner1.getSelectedItem().toString() + "00")) {
                } else {
                    Map<String, Object> updates3 = new HashMap<String, Object>();
                    updates3.put(chi + "", spinner1.getSelectedItem().toString() + "00");
                    databaseReference3.updateChildren(updates3).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            npd.dismiss();
                            AlertDialog alertDialog = new AlertDialog.Builder(MiaActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setIcon(R.drawable.ic_error_black_24dp);
                            alertDialog.setMessage("Error Saving: " + e.getMessage() + "\nCause: " + e.getCause());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    });
                }
                tcl += totalUnit;
                tgp += totatPoint;
                noCO += a;
                Map<String, Object> updates2 = new HashMap<String, Object>();
                updates2.put("TCL", tcl);
                updates2.put("TGP", tgp);
                updates2.put("Total_courses", noCO);
                databaseReference2.updateChildren(updates2).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        npd.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(MiaActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setIcon(R.drawable.ic_error_black_24dp);
                        alertDialog.setMessage("Error Saving: " + e.getMessage() + "\nCause: " + e.getCause());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                });
                npd.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(MiaActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Results saved\nGo to 'Result' in the sub menu to view saved ones");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sav.setVisibility(View.GONE);
                            }
                        });
                alertDialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                npd.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(MiaActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setIcon(R.drawable.ic_error_black_24dp);
                alertDialog.setMessage("Error Saving: " + e.getMessage() + "\nCause: " + e.getCause());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    int getPoint(int cl, char grade) {
        int tcp = 0;
        switch (grade) {
            case 'A':
                tcp = cl * 5;
                break;
            case 'B':
                tcp = cl * 4;
                break;
            case 'C':
                tcp = cl * 3;
                break;
            case 'D':
                tcp = cl * 2;
                break;
            case 'E':
                tcp = cl * 1;
                break;
            case 'F':
                tcp = cl * 0;
                break;
            default:
                break;
        }
        return tcp;
    }

    void enab() {

        for (int i = 5; i < 20; i++) {
            cour[i].setVisibility(View.GONE);
            uni[i].setVisibility(View.GONE);
            grades[i].setVisibility(View.GONE);
            num[i].setVisibility(View.GONE);
            linearLayouts[i].setVisibility(View.GONE);
        }
    }

    private void adds() {
        list1.add("Select Year");
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("4");
        list1.add("5");
        list1.add("6");
        list1.add("7");
        list1.add("8");
        list1.add("9");
        list1.add("10");

        list2.add("Select Semester");
        list2.add("First Semester");
        list2.add("Second Semester");

        list3.add("Grade");
        list3.add("A");
        list3.add("B");
        list3.add("C");
        list3.add("D");
        list3.add("E");
        list3.add("F");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu inflater
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reset) {
            recreate();
            for (int i = 0; i < 20; i++) {
                cour[i].setText(null);
                uni[i].setText(null);
                grades[i].setSelection(0);
            }
            spinner2.setSelection(0);
            spinner1.setSelection(0);
            cour[0].requestFocus();
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

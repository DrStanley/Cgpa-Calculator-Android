package com.example.stanley.cgpacalculator;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class ViewGPAActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference2;
    private double tcl, tgp, noCO;

    TextView textViewName, textViewtcl, textViewtgp, textViewgpa, textViewCourse, textViewHonor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gpa);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CGPA");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference2 = firebaseDatabase.getReference();
        textViewCourse = findViewById(R.id.tot_course);
        textViewName = findViewById(R.id.namGPA);
        textViewtcl = findViewById(R.id.tcl);
        textViewgpa = findViewById(R.id.gpa);
        textViewHonor= findViewById(R.id.honor);
        textViewtgp = findViewById(R.id.tgp);
        final String eamval = getIntent().getStringExtra("aha");

        databaseReference2 = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                .child("Results").child("CGPA");
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        tcl = dataSnapshot.child("TCL").getValue(Integer.class);
                        tgp = dataSnapshot.child("TGP").getValue(Integer.class);
                        noCO = dataSnapshot.child("Total_courses").getValue(Integer.class);
                        textViewtcl.setText("" + tcl);
                        textViewtgp.setText("" + tgp);
                        textViewName.setText(eamval);
                        textViewCourse.setText(noCO + "");
                        double cgpa = tgp / tcl;
                        DecimalFormat df = new DecimalFormat("#.##");
                        cgpa = Double.valueOf(df.format(cgpa));
                        textViewgpa.setText(cgpa + "");
                        String d = hon(cgpa);
                        textViewHonor.setText(""+d);
                    }

                } catch (Exception f) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

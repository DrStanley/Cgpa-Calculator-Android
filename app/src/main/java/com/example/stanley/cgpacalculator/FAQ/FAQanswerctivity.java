package com.example.stanley.cgpacalculator.FAQ;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stanley.cgpacalculator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FAQanswerctivity extends AppCompatActivity {

    TextView textViewQ, textViewA;
    ImageButton imageButtonU, imageButtonD;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    int L = 0, D = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqanswerctivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Answer");
        textViewA = findViewById(R.id.answer);
        textViewQ = findViewById(R.id.quest);
        imageButtonU = findViewById(R.id.like);
        String question = getIntent().getStringExtra("question");
        textViewQ.setText(question);
        imageButtonD = findViewById(R.id.dislike);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("FAQ").child("Answers").child(question);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    finish();
                }
                String g = dataSnapshot.child("answer").getValue(String.class);
                L = dataSnapshot.child("upvote").getValue(Integer.class);
                D = dataSnapshot.child("downvote").getValue(Integer.class);

                textViewA.setText(g);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageButtonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> insert = new HashMap<String, Object>();
                D++;
                insert.put("downvote", L);
                databaseReference.updateChildren(insert).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_SHORT).show();

                        visi(false);
                    }
                });
            }
        });

        imageButtonU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> insert = new HashMap<String, Object>();
                L++;
                insert.put("upvote", L);
                databaseReference.updateChildren(insert).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "No", Toast.LENGTH_SHORT).show();
                        visi(false);
                    }
                });
            }
        });
    }

    void visi(boolean x) {
        imageButtonD.setEnabled(x);
        imageButtonU.setEnabled(x);
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

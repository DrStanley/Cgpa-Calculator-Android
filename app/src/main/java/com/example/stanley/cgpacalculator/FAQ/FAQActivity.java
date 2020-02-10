package com.example.stanley.cgpacalculator.FAQ;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stanley.cgpacalculator.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FAQActivity extends AppCompatActivity {

    ListView listViewQ;
    List<String> ques;
    ArrayAdapter<String> adapter1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Questions");

        listViewQ = findViewById(R.id.questions);
        ques = new ArrayList<>();
        ques.add("Does the Number of courses change My GP");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference = firebaseDatabase.getReference("FAQ").child("Questions");
        final View view = findViewById(android.R.id.content);
        adapter1 = new ArrayAdapter<String>(this, R.layout.listv_iew, ques);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Snackbar.make(view, "No Questions Yet", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String as = dataSnapshot.child("question").getValue(String.class);
                ques.add(as);
                adapter1.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                adapter1.setNotifyOnChange(true);
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                adapter1.setNotifyOnChange(true);
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Apply the adapter to the listviews
        listViewQ.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();


        listViewQ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final String val = (String) parent.getItemAtPosition(position);

                databaseReference2 = firebaseDatabase.getReference("FAQ").child("Answers").child(val);
                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            final View view = findViewById(android.R.id.content);
                            Snackbar.make(view, "No Answer Yet..", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();

                        } else {
                            startActivity(new Intent(FAQActivity.this, FAQanswerctivity.class).putExtra("question", val));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu inflater
        getMenuInflater().inflate(R.menu.ask, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.ask) {
            startActivity(new Intent(getApplicationContext(), AskQActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

}

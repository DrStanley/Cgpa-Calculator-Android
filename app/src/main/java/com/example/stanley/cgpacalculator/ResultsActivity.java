package com.example.stanley.cgpacalculator;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    ListView listView;
    ProgressDialog pd;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseAuth firebaseAuth;
    ArrayList<String> levels;
    ArrayAdapter<String> adapter1;
    String semester, lev;
    private ProgressDialog npd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        pd = new ProgressDialog(this);
        levels = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Results");
        npd = new ProgressDialog(this);

        listView = findViewById(R.id.listview);
        // send query to firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Users").child(firebaseAuth.getUid())
                .child("Results").child("Levels");
        npd.setMessage("Loading Result Please wait...");
        npd.show();
        npd.setCanceledOnTouchOutside(false);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter1 = new ArrayAdapter<String>(this, R.layout.listv_iew, levels);
        // Apply the adapter to the spinner
        listView.setAdapter(adapter1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vv = view.findViewById(R.id.tex);
                lev = vv.getText().toString();
                lev = lev.replace(" LEVEL", "");
                ShowSemesterDialog();
            }
        });

        final View view = findViewById(android.R.id.content);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Snackbar.make(view, "No Result Uploaded Yet", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    npd.dismiss();
                    return;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String as = dataSnapshot.getValue(String.class);
                levels.add(as + " LEVEL");
                adapter1.notifyDataSetChanged();
                npd.dismiss();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter1.setNotifyOnChange(true);
                adapter1.notifyDataSetChanged();
                npd.dismiss();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                levels.remove(dataSnapshot.getValue()+" LEVEL");
                adapter1.setNotifyOnChange(true);
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();

    }

    private void ShowSemesterDialog() {
        try {


            String[] sortOptions = {"First Semester", "Second Semester"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Semester").setIcon(R.drawable.ic_whatshot_black_24dp).setItems(sortOptions,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            // the which arg contains index position of the sortOptions
                            if (which == 0) {
                                //select by First Semester
                                semester = "First Semester";

                            } else if (which == 1) {
                                //select by Second Semester
                                semester = "Second Semester";

                            }

                            try {

                                startActivity(new Intent(getApplicationContext(), SeeResultActivity.class).putExtra("semester", semester)
                                        .putExtra("level", lev));
                            } catch (Exception e) {
                                Toast.makeText(ResultsActivity.this, "No Result Yet ", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
            builder.show();
        } catch (Exception e) {
            Toast.makeText(this, "Semester is not Available", Toast.LENGTH_LONG).show();

        }
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

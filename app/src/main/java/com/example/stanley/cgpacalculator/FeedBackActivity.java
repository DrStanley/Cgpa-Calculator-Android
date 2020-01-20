package com.example.stanley.cgpacalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stanley.cgpacalculator.FAQ.AskQActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedBackActivity extends AppCompatActivity {

    EditText editTextDetails;
    Spinner feedSpinner;
    TextView ids;
    ArrayList<String> list1 = new ArrayList<String>();
    private ProgressDialog npd;

    Button send;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FeedBack");

        ids = findViewById(R.id.feedID);
        editTextDetails = findViewById(R.id.feedDetails);
        feedSpinner = findViewById(R.id.feed_spinner);
        send = findViewById(R.id.sendFeed);
        npd = new ProgressDialog(this);

        list1.add("Select Type");
        list1.add("Complain");
        list1.add("Suggestion");
        list1.add("Compliment");
        ids.setText("Fd_" + System.currentTimeMillis());

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference2 = firebaseDatabase.getReference();
        databaseReference3 = firebaseDatabase.getReference();

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_layout, list1);
        // Apply the adapter to the spinner
        feedSpinner.setAdapter(adapter1);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextDetails.getText().toString())) {
                    //email is empty
                    Toast.makeText(getApplicationContext(), "Please Enter the text", Toast.LENGTH_SHORT).show();
                    editTextDetails.setError("*Required");
                    editTextDetails.requestFocus();
                    return;
                }
                if (feedSpinner.getSelectedItem().toString().equals("Select Type")) {
                    //email is empty
                    Toast.makeText(getApplicationContext(), "Please Select the course Grade", Toast.LENGTH_SHORT).show();
                    return;
                }
                databaseReference2 = firebaseDatabase.getReference("FeedBack").child(feedSpinner.getSelectedItem().toString())
                        .child(ids.getText().toString());

                Map<String, Object> insert = new HashMap<String, Object>();
                npd.setMessage("Sending Question...");
                npd.show();
                npd.setCanceledOnTouchOutside(false);
                insert.put("Id", ids.getText().toString());
                insert.put("details", editTextDetails.getText().toString());

                databaseReference2.updateChildren(insert).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        npd.dismiss();
                        Toast.makeText(FeedBackActivity.this, "Sent", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

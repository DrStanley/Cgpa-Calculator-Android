package com.example.stanley.cgpacalculator.FAQ;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stanley.cgpacalculator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AskQActivity extends AppCompatActivity {
    private ProgressDialog npd;

    EditText ques;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button ask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_q);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ask a Questions");
        npd = new ProgressDialog(this);

        ask = findViewById(R.id.sendQues);
        ques = findViewById(R.id.question);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        final String ids = "Fd_" + System.currentTimeMillis();
        databaseReference = firebaseDatabase.getReference("FAQ").child("Questions").child(ids);
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ques.getText().toString())) {
                    //email is empty
                    Toast.makeText(getApplicationContext(), "Please Enter the Question", Toast.LENGTH_SHORT).show();
                    ques.setError("*Required");
                    ques.requestFocus();
                    return;
                }
                Map<String, Object> insert = new HashMap<String, Object>();
                npd.setMessage("Sending Question...");
                npd.show();
                npd.setCanceledOnTouchOutside(false);
                insert.put("question", ques.getText().toString());
                insert.put("id", ids);
                databaseReference.updateChildren(insert).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        npd.dismiss();
                        Toast.makeText(getApplicationContext(), "Question Sent", Toast.LENGTH_SHORT).show();
                        finish();

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



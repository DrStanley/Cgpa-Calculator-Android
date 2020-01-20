package com.example.stanley.cgpacalculator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button loginbutton;
    TextView regtextV, restTextv;
    CheckBox remem;
    EditText editTextEm, editTextPa;
    FirebaseAuth firebaseAuth;
    private ProgressDialog pd;
    private String em;
    private String pa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        Shared preference to get the extra data stored;
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean check_log = sharedPref.getBoolean("Rember", false);
        String piss = sharedPref.getString("Username", "");
        editor.apply();

        Animation bounce = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.bounce);
        editTextEm = findViewById(R.id.usn);
        pd = new ProgressDialog(LoginActivity.this);
        editTextPa = findViewById(R.id.Upwd);
        firebaseAuth = FirebaseAuth.getInstance();
        if (check_log) {
            remem = findViewById(R.id.checkbox);
            remem.setChecked(true);
            editTextEm.setText(piss);
        }
        loginbutton = findViewById(R.id.login);
        regtextV = findViewById(R.id.regAct);
        restTextv = findViewById(R.id.f_pass);
        loginbutton.startAnimation(bounce);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        regtextV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
        restTextv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetPActivity.class));

            }
        });


    }


    private void loginUser() {

        em = editTextEm.getText().toString();
        pa = editTextPa.getText().toString();
        if (TextUtils.isEmpty(em)) {
            //email is empty
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
            editTextEm.setError("*Required");
            editTextEm.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pa)) {
            //password is empty
            Toast.makeText(this, "Please Enter password", Toast.LENGTH_SHORT).show();
            editTextPa.setError("*Required");
            editTextPa.requestFocus();
            return;
        }
        pd.setMessage("Logging User in...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        firebaseAuth.signInWithEmailAndPassword(em, pa).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {

                        remem = findViewById(R.id.checkbox);
                        boolean yeah = remem.isChecked();

                        if (yeah) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("Rember", true);
                            editor.putString("Username", em);
                            editor.apply();
                        }else {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                        }
//                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        editTextPa.setText(null);
                        Toast.makeText(LoginActivity.this, "Login successful!!", Toast.LENGTH_SHORT).show();

                        pd.dismiss();

                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));

                    } else {
                        pd.dismiss();
                        // email not sent
                        final View view = findViewById(android.R.id.content);
                        Snackbar.make(view, "Please check your mail and verify your email", Snackbar.LENGTH_SHORT)
                                .setAction(null, null)
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }
                } else {
                    pd.dismiss();
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Couldn't Login User \n"
                            + "Network Connection" + "\n" + task.getException().getMessage());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

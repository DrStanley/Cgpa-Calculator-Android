package com.example.stanley.cgpacalculator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {
    EditText em, pw, cpw, nam, phn;
    private static final int PICK_IMAGE = 100;
    FirebaseAuth firebaseAuth;
    Uri imgUri;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    Button button;
    ImageView pix;
    private ProgressDialog pd;
    boolean choose_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pd = new ProgressDialog(this);
        views();

        pix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void openImage() {
        try {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        } catch (Exception ignored) {

        }
    }

    private void registerUser() {

        if (TextUtils.isEmpty(nam.getText().toString())) {
            //email is empty
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            nam.setError("*Required");
            nam.requestFocus();
            return;

        }
        if (!choose_image) {
            //email is empty
            Toast.makeText(this, "Please select a picture", Toast.LENGTH_SHORT).show();
            return;

        }
        if (TextUtils.isEmpty(em.getText().toString())) {
            //email is empty
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
            em.setError("*Required");
            em.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phn.getText().toString())) {
            //email is empty
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
            phn.setError("*Required");
            phn.requestFocus();
            return;
        }
        if (phn.getText().toString().length() != 11) {
            //is phn == 11
            Toast.makeText(this, "Incorrect details 11 character", Toast.LENGTH_SHORT).show();
            phn.setError("11 characters");
            phn.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pw.getText().toString())) {
            //password is empty
            Toast.makeText(this, "Please Enter password", Toast.LENGTH_SHORT).show();
            pw.setError("*Required");
            pw.requestFocus();
            return;
        }
//        checks if email is valid
        if (!isValidEmail(em.getText().toString())) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            em.setError("Invalid Email Address");
            em.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(cpw.getText().toString())) {
            Toast.makeText(this, "Please Enter Confirm password ", Toast.LENGTH_SHORT).show();
            cpw.setError("*Required");
            cpw.requestFocus();
            return;
        }
        if (!cpw.getText().toString().equals(pw.getText().toString())) {
            //password match confirm password
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            cpw.setError("*Required");
            cpw.requestFocus();
            return;
        }
        pd.setMessage("Registering User...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        try {
            firebaseAuth.createUserWithEmailAndPassword(em.getText().toString(), pw.getText().toString()).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("Profile");
                                Drawable mDrawable = pix.getDrawable();
                                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                                String imgs = encodeBitmapAndSaveToFirebase(mBitmap);
//                                                    save
                                Model ss = new Model(imgs, nam.getText().toString(), phn.getText().toString(),
                                        em.getText().toString(), firebaseAuth.getUid());

                                databaseReference.setValue(ss).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // email sent
                                                            final View view = findViewById(android.R.id.content);
                                                            Snackbar.make(view, "Please check your email for verification", Snackbar.LENGTH_SHORT)
                                                                    .setAction(null, null)
                                                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                                                    .show();
                                                            FirebaseAuth.getInstance().signOut();
                                                        }
                                                    }
                                                });
                                    }
                                });
                                Toast.makeText(RegisterActivity.this,
                                        "Registration Successful !!!",
                                        Toast.LENGTH_SHORT).show();
                                SharedPreferences Pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor piss = Pref.edit();
                                piss.clear();
                                piss.putBoolean("Updated", false);
                                piss.putBoolean("Login", true);
                                piss.putString("Username", em.getText().toString());
                                piss.apply();
                                Intent z = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(z);
                                finish();
                            } else {
                                pd.dismiss();
                                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Couldn't  Register User \n"
                                        + task.getException().getMessage());
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
        } catch (Exception f) {
            pd.dismiss();
            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Alert " + f.getMessage() + " " + f.getCause());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            try {
                imgUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] imageInByte = stream.toByteArray();
                long length = imageInByte.length;
                long spec = 500000;
                if (length > spec) {
                    AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Error Setting image\n" +
                            "Image size is > 500kb");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }
//                setting bitmap into imageview
                pix.setImageBitmap(bitmap);
                choose_image = true;
            } catch (Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Error Updating\n" + e.getMessage());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }

        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Error Updating\n" + e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    public String encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return imageEncoded;
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

    private void views() {
        em = findViewById(R.id.email);
        nam = findViewById(R.id.name);
        phn = findViewById(R.id.phn);
        pw = findViewById(R.id.pwd);
        cpw = findViewById(R.id.cpwd);
        pix = findViewById(R.id.profile_imageR);
        button = findViewById(R.id.register);
    }
}

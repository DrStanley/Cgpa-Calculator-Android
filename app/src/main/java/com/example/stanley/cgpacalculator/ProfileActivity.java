package com.example.stanley.cgpacalculator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageButton edit;
    ImageView pix;
    EditText editTextName, editTextEmail, editTextPhone;
    Button saveit;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private int PICK_IMAGE = 100;
    private Uri imgUri;
    private ProgressDialog pd, npd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        saveit = findViewById(R.id.saveP);
        edit = findViewById(R.id.ena);
        pix = findViewById(R.id.proImg);
        editTextName = findViewById(R.id.Pname);
        editTextEmail = findViewById(R.id.Pemail);
        editTextPhone = findViewById(R.id.Pphone);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(this);
        npd = new ProgressDialog(this);

        enab(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        pd.setMessage("Loading Data...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);
        databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("Profile");
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enab(true);
                saveit.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                saveit.setTextColor(getResources().getColor(R.color.white));
            }
        });
        editTextEmail.setEnabled(false);

        saveit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saved();
            }
        });

        pix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String aha = dataSnapshot.child("name").getValue(String.class);
                    String img = dataSnapshot.child("image").getValue(String.class);
                    String phn2 = dataSnapshot.child("phone_number").getValue(String.class);
                    String ema = firebaseAuth.getCurrentUser().getEmail();

                    editTextEmail.setText(ema);
                    editTextPhone.setText(phn2);
                    editTextName.setText(aha);
//                    usd.setText(firebaseAuth.getUid());
                    Bitmap bb = decodeFromFirebaseBase64(img);
                    pix.setImageBitmap(bb);
                    pd.dismiss();
                } catch (Exception f) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public String encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return imageEncoded;
    }

    private void openImage() {
        try {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        } catch (Exception ignored) {

        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reset) {
            recreate();
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void enab(boolean val) {
        pix.setEnabled(val);
        saveit.setEnabled(val);
        saveit.setBackgroundColor(getResources().getColor(R.color.gray));
        saveit.setTextColor(getResources().getColor(R.color.colorPrimary));
        editTextName.setEnabled(val);
        editTextPhone.setEnabled(val);
    }

    void saved() {
        if (TextUtils.isEmpty(editTextName.getText().toString())) {
            //email is empty
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            editTextName.setError("*Required");
            editTextName.requestFocus();
            return;

        }
        if (TextUtils.isEmpty(editTextPhone.getText().toString())) {
            //email is empty
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
            editTextPhone.setError("*Required");
            editTextPhone.requestFocus();
            return;
        }
        if (editTextPhone.getText().toString().length() != 11) {
            //is phn == 11
            Toast.makeText(this, "Incorrect details 11 character", Toast.LENGTH_SHORT).show();
            editTextPhone.setError("11 characters");
            editTextPhone.requestFocus();
            return;
        }

        Drawable mDrawable = pix.getDrawable();
        Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
        String imgUri2 = encodeBitmapAndSaveToFirebase(mBitmap);
        Map<String, Object> updates = new HashMap<String, Object>();
        npd.setMessage("Saving Data Please wait...");
        npd.show();
        updates.put("name", editTextName.getText().toString());
        updates.put("image", imgUri2);
        updates.put("phone_number",editTextPhone.getText().toString() );
        databaseReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                npd.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Profile Updated");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                enab(false);
                            }
                        });
                alertDialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                npd.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
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
                    AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
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
            } catch (Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
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
            AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
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

}

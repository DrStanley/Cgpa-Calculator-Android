package com.example.stanley.cgpacalculator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import me.relex.circleindicator.CircleIndicator;

public class MenuActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private MyPager myPager;
    String name;
    Button cal;
    private ProgressDialog npd;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private static final long SLIDER_TIMER = 2000; // change slider interval
    private int currentPage = 0; // this will tell us the current page available on the view pager
    // currentPage variable
    private boolean isCountDownTimerActive = false; // let the timer start if and only if it has completed previous task
    private Handler handler;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle barDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        handler = new Handler();
        handler.postDelayed(runnable, 1000);
        runnable.run();
        npd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();
        myPager = new MyPager(this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(myPager);
        circleIndicator = findViewById(R.id.circle);
        circleIndicator.setViewPager(viewPager);

        cal = findViewById(R.id.calculate);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(MenuActivity.this, "Poor Network Please wait and Retry", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(MenuActivity.this, GPAActivity.class).putExtra("aha", name));
                }
            }
        });

        NavigationView nV = findViewById(R.id.nav_view);
        nV.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.hep:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        return true;
                    case R.id.result:
                        startActivity(new Intent(getApplicationContext(),ResultsActivity.class));
                        return true;
                    case R.id.cgpa:
                        startActivity(new Intent(getApplicationContext(),ViewGPAActivity.class).putExtra("aha", name));
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        return true;
                }
                return true;
            }
        });

        npd.setMessage("Loading Please wait...");
        npd.show();
        npd.setCanceledOnTouchOutside(false);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    currentPage = 0;
                } else if (position == 1) {
                    currentPage = 1;
                } else if (position == 2) {
                    currentPage = 2;
                } else if (position == 3) {
                    currentPage = 3;
                } else {
                    currentPage = 4;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("Profile");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    System.out.println("datasnap " + dataSnapshot);
                    if (dataSnapshot.getValue() == null) {
                        return;
                    } else {
                        name = dataSnapshot.child("name").getValue(String.class);
                        String aha = firebaseAuth.getCurrentUser().getEmail();
                        String img = dataSnapshot.child("image").getValue(String.class);
                        ImageView h_img = findViewById(R.id.h_image);
                        TextView h_txt = findViewById(R.id.h_email);
                        Bitmap bb = decodeFromFirebaseBase64(img);
                        h_img.setImageBitmap(bb);
                        h_txt.setText(aha);
                        npd.dismiss();
                    }

                } catch (Exception e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MenuActivity.this).create();
                    alertDialog.setTitle("Error Alert");
                    alertDialog.setIcon(R.drawable.ic_error_black_24dp);
                    alertDialog.setMessage("The following errors occurred\n" + e.getMessage());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
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


    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isCountDownTimerActive) {
                automateSlider();
            }
            handler.postDelayed(runnable, 1000);
            // our runnable should keep running for every 1000 milliseconds (1 seconds)
        }
    };

    private void automateSlider() {
        isCountDownTimerActive = true;
        new CountDownTimer(SLIDER_TIMER, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                int nextSlider = currentPage + 1;

                if (nextSlider == 4) {
                    nextSlider = 0; // if it's last Image, let it go to the first image
                }
                viewPager.setCurrentItem(nextSlider);
                isCountDownTimerActive = false;
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Kill this background task once the activity has been killed
        handler.removeCallbacks(runnable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case android.R.id.home:
//                finish();
//                return true;
//        }\
        if (barDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

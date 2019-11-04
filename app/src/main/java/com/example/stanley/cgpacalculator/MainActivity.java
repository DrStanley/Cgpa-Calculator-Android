package com.example.stanley.cgpacalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private long timeout = 3500;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Animation clock = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.clockwise);
        final Animation blink = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.blink);
        imageView = findViewById(R.id.logo);

        imageView.startAnimation(clock);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent welcome = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(welcome);
                finish();
            }
        }, timeout);


    }
}

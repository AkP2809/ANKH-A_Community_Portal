package com.example.ankhcommunity.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.ankhcommunity.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Thread td = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };
        td.start();
    }
}
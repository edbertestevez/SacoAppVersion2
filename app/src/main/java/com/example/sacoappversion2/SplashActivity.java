package com.example.sacoappversion2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    String prename="myspref";
    SharedPreferences sharedpref;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This code will render a full screen view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 3000) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    finish();

                    // get the SharedPreferences object
                    sharedpref = getSharedPreferences(prename, MODE_PRIVATE);
                    // Retrieve the saved values
                    boolean flag = sharedpref.getBoolean("LOGGED", false);

                    if(flag == false) {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }else{
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                    }

                    startActivity(intent);
                }
            }
        };
        splashThread.start();

    }
}

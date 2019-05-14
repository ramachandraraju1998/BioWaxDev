package com.srinivas.biowax;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Splash extends Activity {
int start=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        final String PREFS_NAME = "MyPrefsFile";





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                if (settings.getBoolean("my_first_time", true)) {


                    //the app is being launched for first time, do something
                    Log.d("Comments", "First time");

                    if (start == 0) {
                        start++;
                        Intent login = new Intent(Splash.this, Home.class);
                        startActivity(login);
                    }

                    // first time task

                    // record the fact that the app has been started at least once
                    settings.edit().putBoolean("my_first_time", false).commit();


                }else
                {
                    Intent login = new Intent(Splash.this, Login.class);
                    startActivity(login);
                }
            }
        }, 2000);

    }
}

package com.srinivas.biowax;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.srinivas.Driver.Agentdetails;
import com.srinivas.Driver.Driverdetails;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class Dashboard extends Activity implements View.OnClickListener {
    private StarAnimationView mAnimationView;
    LinearLayout garbage_ll, history_ll, mapview_ll, driverinfo_ll, receipt_ll, logout_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        garbage_ll = findViewById(R.id.garbage_ll);
        history_ll = findViewById(R.id.history_ll);
        mapview_ll = findViewById(R.id.mapview_ll);
        driverinfo_ll = findViewById(R.id.driverinfo_ll);
        receipt_ll = findViewById(R.id.receipt_ll);
        logout_ll = findViewById(R.id.logout_ll);

        garbage_ll.setOnClickListener(this);
        history_ll.setOnClickListener(this);
        mapview_ll.setOnClickListener(this);
        driverinfo_ll.setOnClickListener(this);
        receipt_ll.setOnClickListener(this);
        logout_ll.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_NETWORK_STATE}, 0);
        } else {
            Toast.makeText(getBaseContext(), "Else Part partd", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            Toast.makeText(getBaseContext(), "asking permission", Toast.LENGTH_SHORT).show();


        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_NETWORK_STATE}, 0);
            Toast.makeText(getBaseContext(), "Else Part I think all permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(50);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.garbage_ll:
                vibrate();
                Intent garbagecollection = new Intent(Dashboard.this, GarbageCollection.class);
                startActivity(garbagecollection);
                break;
            case R.id.history_ll:
                vibrate();
                Intent garbageHistory = new Intent(Dashboard.this, GarbageHistory.class);
                startActivity(garbageHistory);
                break;
            case R.id.mapview_ll:
                vibrate();
                Intent locationhistory = new Intent(Dashboard.this, LocationHistory.class);
                startActivity(locationhistory);
                break;
            case R.id.driverinfo_ll:
                vibrate();
                // Intent driver = new Intent(Dashboard.this, Driverdetails.class);
                Intent driver = new Intent(Dashboard.this, Agentdetails.class);
                startActivity(driver);
                break;
            case R.id.receipt_ll:
                vibrate();
                Intent invoices = new Intent(Dashboard.this, GarbageInvoices.class);
                startActivity(invoices);
                break;
            case R.id.logout_ll:
                vibrate();
                break;
        }
    }


}

package com.srinivas.biowax;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;

public class GarbageCollection extends Activity implements View.OnClickListener {
    RecyclerView hospitalb_rv;

    ArrayList<Hospitals> checkins, checkins_filter;
    Hospitals_Adapter hospitals_adapter, hospitals_adapter2;
    Handler handler;
    private Runnable mRunnable;
    TextView latitude_tv, longitude_tv;
    ImageView myimage;
    ProgressDialog pd;
    SearchView svOutletNameAddress;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.garbage_collection);
        hospitalb_rv = findViewById(R.id.hospitalb_rv);
        myimage = findViewById(R.id.myimage);
        myimage.setOnClickListener(this);
        hospitalb_rv.setLayoutManager(new LinearLayoutManager(this));
        longitude_tv = findViewById(R.id.longitude_tv);
        svOutletNameAddress = (SearchView) findViewById(R.id.svOutletNameAddress);

        latitude_tv = findViewById(R.id.latitude_tv);
        pd = new ProgressDialog(GarbageCollection.this);
        pd.setMessage("Fetching Garbage History..");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
       // recyclerView=findViewById(R.id.recyleview);
       /* try {
            getRoutes();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        checkins = new ArrayList<Hospitals>();
        checkins_filter = new ArrayList<Hospitals>();
       /* checkins.add(new Hospitals("Dadi veerinaidu college", "1002", 17.701001, 82.998878, "visakhapatnam", "Lane1", "8885270193"));
        checkins.add(new Hospitals("Lakshmi ganapathi temple", "1002", 17.704581, 82.997954, "visakhapatnam", "Lane2", "8885270193"));
        checkins.add(new Hospitals("Union Bank of india", "1002", 17.702920, 82.998180, "visakhapatnam", "Route3", "8885270193"));
        checkins.add(new Hospitals("Sai baba temple", "1002", 17.700445, 82.997803, "visakhapatnam", "Route4", "8885270193"));
        checkins.add(new Hospitals("Real Choice", "1002", 17.6998393, 82.9986091, "visakhapatnam", "Route5", "8885270193"));
        checkins.add(new Hospitals("post office thummapala", "1002", 17.721465, 82.965939, "visakhapatnam", "Route 6 ", "8885270193"));
        checkins.add(new Hospitals("Penna4 office", "1002", 17.741711, 83.3261952, "visakhapatnam", "Route 7", "8885270193"));
        checkins.add(new Hospitals("Indian Oil petrol bunk", "1002", 17.7390317, 83.3206353, "visakhapatnam", "Route 8", "8885270193"));
        checkins.add(new Hospitals("Enadu office", "1002", 17.7391631, 83.3109891, "visakhapatnam", "Route 9", "8885270193"));
        checkins.add(new Hospitals("Maddipalem junction", "1002", 17.7362124, 83.3182955, "visakhapatnam", "Route 9", "8885270193"));
        checkins.add(new Hospitals("Gurudwara Junction", "1002", 17.7368127, 83.3051191, "visakhapatnam", "Route 5", "8885270193"));
       *//* hospitals_adapter = new Hospitals_Adapter(checkins, R.layout.hostpital_single, getApplicationContext());
        hospitalb_rv.setAdapter(hospitals_adapter);
        hospitals_adapter.notifyDataSetChanged();
*/
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_NETWORK_STATE}, 0);
        }


       /* handler = new Handler();
        handler.postDelayed(mRunnable = new Runnable() {
            public void run() {

                checkins_filter.clear();
                getlocationstatus();          // this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, 3000);
            }
        }, 3000);
*/



        try {
            getRoutes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        svOutletNameAddress.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

               // Toast.makeText(GarbageCollection.this,"No result Found",Toast.LENGTH_LONG).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                hospitals_adapter.filteraddress(newText.toString());


               // rla.filteraddress(newText.toString());
                return false;

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        //handler.removeCallbacks(mRunnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission

            try {
                pd = new ProgressDialog(GarbageCollection.this);
                pd.setMessage("Fetching Garbage History..");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();

                getRoutes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void getlocationstatus() {
        checkins_filter.clear();
        hospitals_adapter = new Hospitals_Adapter(checkins_filter, R.layout.hostpital_single, getApplicationContext());
        hospitalb_rv.setAdapter(hospitals_adapter);
        GPSTracker gpsTracker = new GPSTracker(GarbageCollection.this);
        if (gpsTracker.canGetLocation) {
            System.out.println("loacotin update " + gpsTracker.getLatitude() + " longitude " + gpsTracker.getLongitude());
            String x = String.valueOf(gpsTracker.getLatitude());
            String xy = String.valueOf(gpsTracker.getLongitude());
            latitude_tv.setText(x);
            longitude_tv.setText(xy);
        } else {
            System.out.println("okay no location update");
        }

        for (int i = 0; i < checkins.size(); i++) {
            Location startPoint = new Location("locationA");
            startPoint.setLatitude(gpsTracker.getLatitude());
            startPoint.setLongitude(gpsTracker.getLongitude());


            Location endPoint = new Location("locationA");
            endPoint.setLatitude(checkins.get(i).getH_lat());
            endPoint.setLongitude(checkins.get(i).getH_long());

            double distance = startPoint.distanceTo(endPoint);
            System.out.println("hoooooooooooo distance............. " + distance);
/*
            System.out.println("Dadi location update distance " + distance(startPoint.getLatitude(), startPoint.getLongitude(),
                    checkins.get(i).getH_lat(), checkins.get(i).getH_long()));
*/
            //checkins_filter.clear();
            if (distance(startPoint.getLatitude(), startPoint.getLongitude(),
                    checkins.get(i).getH_lat(), checkins.get(i).getH_long()) < 3) {
                System.out.println("Location update " + checkins.get(i).getH_name());
                checkins_filter.add(new Hospitals(checkins.get(i).getHcf_id(),checkins.get(i).getH_name(), checkins.get(i).getH_code(),
                        checkins.get(i).getH_lat(), checkins.get(i).getH_long(), checkins.get(i).getH_address(),
                        checkins.get(i).getRoute_name(), checkins.get(i).getMobile()));
              /*  hospitals_adapter = new Hospitals_Adapter(checkins_filter, R.layout.hostpital_single, getApplicationContext());
                hospitalb_rv.setAdapter(hospitals_adapter);
                hospitals_adapter.notifyDataSetChanged();
*/
                hospitals_adapter = new Hospitals_Adapter(checkins_filter, R.layout.hostpital_single, getApplicationContext());
                hospitalb_rv.setAdapter(hospitals_adapter);
                hospitals_adapter.notifyDataSetChanged();
            }
        }
        pd.dismiss();
    }

    /**
     * calculates the distance between two locations in MILES
     */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }



    public void getRoutes() throws IOException {

        SharedPreferences ss = getSharedPreferences("Login", MODE_PRIVATE);
        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/routesscheduleformobile")
                .get()
                .build();


        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("result", e.getMessage().toString());
                e.printStackTrace();
                pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                //  pd.dismiss();
                if (!response.isSuccessful()) {
                    pd.dismiss();
                    Log.d("result", response.toString());
                    throw new IOException("Unexpected code " + response);
                } else {

                    Log.d("result", response.toString());
                    String responseBody = response.body().string();
                    final JSONObject obj;
                    try {
                        obj = new JSONObject(responseBody);
                        if (obj.getString("status").equals("true")) {
                            System.out.println("JONDDDd sdfasdjflsadf" + obj.toString());

                            JSONArray jsonArray = obj.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject res = jsonArray.getJSONObject(i);
                                String priority = res.getString("priority");
                                JSONObject routes_master = res.getJSONObject("routes_master");

                                JSONObject hos = res.getJSONObject("hcf_master");
                                if(res.getString("hcf_master")!="null") {
                                    checkins.add(new Hospitals(hos.getString("id"), hos.getString("facility_name"),
                                            priority,
                                            Double.valueOf(hos.getString("hcf_longitude")),
                                            Double.valueOf(hos.getString("hcf_lattitudue"))
                                            , hos.getString("hcf_address"), routes_master.getString("route_name"),
                                            hos.getString("contact_number")));

                                    System.out.println("dadi see here " + hos.getString("contact_number"));
                                }
                            }pd.dismiss();
                            
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hospitals_adapter = new Hospitals_Adapter(checkins, R.layout.hostpital_single, getApplicationContext());
                                    hospitalb_rv.setAdapter(hospitals_adapter);
                                    hospitals_adapter.notifyDataSetChanged();

                                }
                            });
                        } else {
                            System.out.println("JONDDDd " + obj.toString());
                            System.out.println("JONDDDd " + obj.getString("token"));

                            GarbageCollection.this.longitude_tv.post(new Runnable() {
                                public void run() {
                                    showDialog(GarbageCollection.this, "Invalid Response..", "true");
                                }
                            });
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    public void showDialog(Activity activity, String msg, final String status) {
        final Dialog dialog = new Dialog(activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        ImageView b = dialog.findViewById(R.id.b);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myimage:
                finish();
                break;
        }
    }
}

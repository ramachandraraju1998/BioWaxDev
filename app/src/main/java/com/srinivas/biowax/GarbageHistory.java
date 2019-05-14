package com.srinivas.biowax;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.srinivas.Helper.DBHelper;
import com.srinivas.validations.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;

public class GarbageHistory extends Activity implements View.OnClickListener {
    RecyclerView hospitalb_rv;
    ImageView history_back;
    ArrayList<Transactions> Transactionss;
    Transaction_Adapter transaction_adapter, hospitals_adapter2;
    Handler handler;
    private Runnable mRunnable;
    TextView latitude_tv, longitude_tv;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.garbage_history);
        history_back = findViewById(R.id.history_back);
        history_back.setOnClickListener(this);
        hospitalb_rv = findViewById(R.id.hospitalb_rv);
        hospitalb_rv.setLayoutManager(new LinearLayoutManager(this));
        longitude_tv = findViewById(R.id.longitude_tv);
        latitude_tv = findViewById(R.id.latitude_tv);

        Transactionss = new ArrayList<Transactions>();

        if (Validations.hasActiveInternetConnection(GarbageHistory.this)) {
            try {
                pd = new ProgressDialog(GarbageHistory.this);
                pd.setMessage("Fetching Barcode Details..");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();
                getRoutes();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getBaseContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
          //  getcheckins_from_local();
        }

    }


    public void getRoutes() throws IOException {

        SharedPreferences ss = getSharedPreferences("Login", MODE_PRIVATE);
        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/hcfwastecollectiondataformobile")
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
                    pd.dismiss();
                    Log.d("result", response.toString());
                    String responseBody = response.body().string();
                    final JSONObject obj;
                    try {
                        obj = new JSONObject(responseBody);
                        if (obj.getString("status").equals("true")) {
                            System.out.println("hcfwastecollectiondataformobile " + obj.toString());

                            JSONArray jsonArray = obj.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject res = jsonArray.getJSONObject(i);
                                Transactionss.add(new Transactions(res.getString("transaction_code"),
                                        res.getString("transaction_ref"),
                                        res.getString("transaction_num"),
                                        res.getString("facility_name")
                                        , res.getString("color_name")));
                            }

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    // Stuff that updates the UI
                                    transaction_adapter = new Transaction_Adapter(Transactionss, R.layout.transaction_single, getApplicationContext());
                                    hospitalb_rv.setAdapter(transaction_adapter);
                                }
                            });

                        } else {
                            System.out.println("JONDDDd " + obj.toString());
                            System.out.println("JONDDDd " + obj.getString("token"));

                            GarbageHistory.this.longitude_tv.post(new Runnable() {
                                public void run() {
                                    showDialog(GarbageHistory.this, "Invalid Response..", "true");
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

    public void getcheckins_from_local() {

        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String cdt_date = sdf.format(cd.getTime());

        ////Log.d("displaycount", cdt_date.toString());
        SQLiteDatabase db;
        db = openOrCreateDatabase("RMAT", Context.MODE_PRIVATE, null);

        // final Cursor c = db.rawQuery("SELECT * FROM dailyreportss ORDER BY cdt ASC LIMIT 1000", null);
        final Cursor c = db.rawQuery("SELECT * FROM dailyreportss LIMIT 1000", null);
        ////Log.d("overallstring", c.toString());
        String ccc = String.valueOf(c.getCount());
        // Toast.makeText(getBaseContext(),"installation "+ccc.toString(),Toast.LENGTH_SHORT).show();
        ////Log.d("displaycount", ccc);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String weight = "";
                String transno = c.getString(c.getColumnIndex("transno"));
                SharedPreferences.Editor ss = getSharedPreferences("Tracno", MODE_PRIVATE).edit();

                SharedPreferences tack = getSharedPreferences("Tracno", MODE_PRIVATE);
                if (tack.getString("tracno", "").equals("")) {
                    ss.putString("tracno", transno);
                    ss.commit();
                    weight = c.getString(c.getColumnIndex("bag_weight_in_hcf"));
                } else {
                    if (transno.equals(tack.getString("tracno", ""))) {
                        weight = weight + " " + c.getString(c.getColumnIndex("bag_weight_in_hcf"));
                    }
                }
                if (c.getString(c.getColumnIndex("status")).equals("local")) {

                    try {
                        uploadWasteform(c.getString(c.getColumnIndex("hcf_master_id")),
                                c.getString(c.getColumnIndex("truck_id")),
                                c.getString(c.getColumnIndex("route_master_id")),
                                c.getString(c.getColumnIndex("barcode_number")),
                                c.getString(c.getColumnIndex("cover_color_id")),
                                c.getString(c.getColumnIndex("is_approval_required"))
                                , c.getString(c.getColumnIndex("bag_weight_in_hcf")),
                                c.getString(c.getColumnIndex("latitude")),
                                c.getString(c.getColumnIndex("longitude")),
                                c.getString(c.getColumnIndex("is_manual_input"))
                                , c.getString(c.getColumnIndex("hcf_authorized_person_name")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                c.moveToNext();
            }
        }
        db.close();

       /* hospitals_adapter = new Histroy_Adapter(hospitalshistories, R.layout.history_single, getApplicationContext());
        hospitalb_rv.setAdapter(hospitals_adapter);*/


        //finish();
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
            case R.id.history_back:
                finish();
                break;
        }
    }


    public void uploadWasteform(String hcf_master_id, String truckid, String route_master_id, String barcodenumber,
                                String cover_color_id, String is_approval_required, String bag_weight_in_hcf,
                                String latitude, String logiitude, String is_manual_input, String hcf_authorized_person_name) throws IOException {

        final SharedPreferences ss = getSharedPreferences("Login", MODE_PRIVATE);
        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        final String formattedDate = df.format(c);


        RequestBody formBody = new FormBody.Builder()
                .add("hcf_master_id", hcf_master_id)
                .add("waste_collection_date", formattedDate)
                .add("truck_id", truckid)
                .add("route_master_id", route_master_id)
                .add("barcode_number", barcodenumber)
                .add("cover_color_id", cover_color_id)
                .add("is_approval_required", is_approval_required)
                .add("approved_by", "1")
                .add("bag_weight_in_hcf", bag_weight_in_hcf)
                .add("longitude", latitude)
                .add("latitude", logiitude)
                .add("is_manual_input", is_manual_input)
                .add("hcf_authorized_person_name", hcf_authorized_person_name)
                .add("driver_id", "1")
                .add("driver_imei_number", "123456789")
                .add("is_sagregation_completed", "no")
                .add("sagregation_image", "")

                .build();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/addhcfwastecollectionfrommobile")
                .post(formBody)
                .build();

        if (Validations.hasActiveInternetConnection(GarbageHistory.this)) {
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.d("result", e.getMessage().toString());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                    //  pd.dismiss();
                    if (!response.isSuccessful()) {
                        Log.d("result", response.toString());
                        throw new IOException("Unexpected code " + response);
                    } else {
                        Log.d("result", response.toString());
                        String responseBody = response.body().string();
                        final JSONObject obj;
                        try {
                            obj = new JSONObject(responseBody);
                            System.out.println("dadi out put here " + obj);
                            if (obj.getString("status").equals("true")) {
                                System.out.println("JONDDDd " + obj.toString());
                            } else {

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } else {
        }

    }


}

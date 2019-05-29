package com.srinivas.biowax;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class LocationHistory extends Activity implements View.OnClickListener {
    RecyclerView hospitalb_rv;

    ArrayList<Locationshistory> Locationshistory;
    Location_Adapter hospitals_adapter, hospitals_adapter2;
    Handler handler;
    private Runnable mRunnable;
    TextView latitude_tv, longitude_tv;
    ImageView loaction_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_history);
        hospitalb_rv = findViewById(R.id.hospitalb_rv);
        hospitalb_rv.setLayoutManager(new LinearLayoutManager(this));
        longitude_tv = findViewById(R.id.longitude_tv);
        latitude_tv = findViewById(R.id.latitude_tv);
        loaction_back = findViewById(R.id.loaction_back);
        Locationshistory = new ArrayList<Locationshistory>();
        Locationshistory.add(new Locationshistory("17.6999999", "83.33332", "Dwarakanagar "));
        Locationshistory.add(new Locationshistory("17.223333333", "83.3323232", "Gajuwaka"));
        Locationshistory.add(new Locationshistory("17.26666444", "83.3356565", "Sevenhills "));
        Locationshistory.add(new Locationshistory("17.322223333", "83.387878783", "Madhurwada"));
        Locationshistory.add(new Locationshistory("17.445545454", "83.3399999", "Seethamadara"));
        Locationshistory.add(new Locationshistory("17.622323232", "83.33121221212", "Complex"));
        Locationshistory.add(new Locationshistory("17.65656565656", "83.38676763", "NAD Junction"));
        Locationshistory.add(new Locationshistory("17.656565656", "83.33", "Railway Station"));
        hospitals_adapter = new Location_Adapter(Locationshistory, R.layout.location_single, getApplicationContext());
        hospitalb_rv.setAdapter(hospitals_adapter);
       /* try {
            getRoutes();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        loaction_back.setOnClickListener(this);
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
                        if (obj.getString("status").equals("true")) {
                            System.out.println("hcfwastecollectiondataformobile " + obj.toString());

                            JSONArray jsonArray = obj.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject res = jsonArray.getJSONObject(i);
                                Locationshistory.add(new Locationshistory("22.22", "83.33", "Vija"));
                            }


                        } else {
                            System.out.println("JONDDDd " + obj.toString());
                            System.out.println("JONDDDd " + obj.getString("token"));

                            LocationHistory.this.longitude_tv.post(
                                    new Runnable() {
                                public void run() {
                                    showDialog(LocationHistory.this, "Invalid Response..", "true");
                                }
                            });
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                hospitals_adapter = new Location_Adapter(Locationshistory, R.layout.history_single, getApplicationContext());
                                hospitalb_rv.setAdapter(hospitals_adapter);
                            }
                        });

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
            case R.id.loaction_back:
                finish();
                break;
        }
    }
}

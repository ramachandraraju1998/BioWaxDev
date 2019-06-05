package com.srinivas.biowax;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.srinivas.validations.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CollectinAgentReciptScreen extends AppCompatActivity implements View.OnClickListener {
    RecyclerView hospital_rec;
    ImageView history_back;
    ArrayList<Collection> Collections;
    CollectionagentAdapter collection_adapter, hospitals_adapter2;
    Handler handler;
    private Runnable mRunnable;

    ProgressDialog pd;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collectin_agent_recipt_screen);

//        getActionBar().hide();
        id=getIntent().getStringExtra("id");
        Toast.makeText(CollectinAgentReciptScreen.this,id.toString(),Toast.LENGTH_SHORT).show();
        history_back = findViewById(R.id.history_back);
        history_back.setOnClickListener(this);
        hospital_rec = findViewById(R.id.hospitalb_rv);
        hospital_rec.setLayoutManager(new LinearLayoutManager(this));



        Collections = new ArrayList<Collection>();

        if (Validations.hasActiveInternetConnection(CollectinAgentReciptScreen.this)) {
            pd = new ProgressDialog(CollectinAgentReciptScreen.this);
            pd.setMessage("Fetching Barcode Details..");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();

        } else {
            Toast.makeText(getBaseContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
            //  getcheckins_from_local();
        }

    getRecipts();

    }

    private void getRecipts() {


        SharedPreferences ss = getSharedPreferences("Login", MODE_PRIVATE);
        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/receiptslistofhospital/"+id)
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
                            JSONObject data = obj.getJSONObject("data");
                            JSONArray jsonArray = data.getJSONArray("receipt_list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject res = jsonArray.getJSONObject(i);
                                Collections.add(new Collection(res.getString("receipt_number"),
                                        res.getString("receipt_date"))
                                      );
                            }

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {


                                    collection_adapter = new CollectionagentAdapter(Collections,id,R.layout.collectionagentreciptsingle,getApplicationContext());
                                    hospital_rec.setAdapter(collection_adapter);
                                    // Stuff that updates the UI
//                                    transaction_adapter = new Transaction_Adapter(Transactionss, R.layout.transaction_single, getApplicationContext());
//                                    hospitalb_rv.setAdapter(transaction_adapter);
                                }
                            });

                        } else {

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    @Override
    public void onClick(View v) {

    }
}

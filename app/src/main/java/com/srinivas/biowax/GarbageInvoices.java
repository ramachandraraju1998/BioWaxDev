package com.srinivas.biowax;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.srinivas.Models.Invoice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GarbageInvoices extends Activity implements View.OnClickListener {
    RecyclerView hospitalb_rv;
    ProgressDialog pd;
    ArrayList<Invoice> invoices;
    Invoices_Adapter invoicesadapter;
    Handler handler;
    private Runnable mRunnable;
    TextView latitude_tv, longitude_tv;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.garbage_invoices);
        hospitalb_rv = findViewById(R.id.hospitalb_rv);
        hospitalb_rv.setLayoutManager(new LinearLayoutManager(this));
        longitude_tv = findViewById(R.id.longitude_tv);
        latitude_tv = findViewById(R.id.latitude_tv);
        back=findViewById(R.id.back);
        invoices = new ArrayList<Invoice>();
        invoicesadapter = new Invoices_Adapter(invoices, R.layout.invoices_single, getApplicationContext());
        hospitalb_rv.setAdapter(invoicesadapter);

        pd = new ProgressDialog(GarbageInvoices.this);
        pd.setMessage("Fetching Invoices ..");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        try {
            getRoutes();
        } catch (IOException e) {
            e.printStackTrace();
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
                .url("http://175.101.151.121:8002/api/getdueinvoicesformobile")
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
                                invoices.add(new Invoice(res.getString("hcf_master_id"),
                                        res.getString("invoice_date"),
                                        res.getString("billing_month"),
                                        res.getString("no_of_beds")
                                        , res.getString("no_of_labs"),
                                        res.getString("cost_per_lab"),
                                        res.getString("lab_amount"),
                                        res.getString("paid_amount"),
                                        res.getString("total_invoice_amount")));
                            }


                        } else {
                            System.out.println("JONDDDd " + obj.toString());
                            System.out.println("JONDDDd " + obj.getString("token"));

                            GarbageInvoices.this.longitude_tv.post(new Runnable() {
                                public void run() {
                                    showDialog(GarbageInvoices.this, "Invalid Response..", "true");
                                }
                            });
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                invoicesadapter = new Invoices_Adapter(invoices, R.layout.invoices_single, getApplicationContext());
                                hospitalb_rv.setAdapter(invoicesadapter);
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


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }


}

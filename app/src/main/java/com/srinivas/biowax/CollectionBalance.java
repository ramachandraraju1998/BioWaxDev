package com.srinivas.biowax;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CollectionBalance extends AppCompatActivity implements  View.OnClickListener {

    private Spinner spinner;

    String[] plants = new String[]{
            "Select Hospital",

    };
    String[] payment = new String[10];

    List<String> paymode;//    = {"Cash", "Chequ", "Debit", "Credit", "wallet"};
//hfgrhhgjgf

    String responseBody;
    List<String> plantsList;
    TextView hspname, balence_textview;
    ArrayAdapter<String> spinnerArrayAdapter;
    ProgressDialog pd;
    int send = 0;
    String table = "";
    Button pay;
    EditText balamount;
    int balence;
    String set;
    LinearLayout li;
    int amm, ch = 0;
    ImageView print;
    TextView inv_date, inv_month, inv_amt, paid_amt, total_amt;
    String invoice_id;
    String invoice_date;
    String billing_month;
    String invoice_amount;
    int paid_amount;
    String total_amount;
    String finalInvoice_date;
    String inid = "", indt = "", inamt = "", pa = "", ta = "";
    int checkedItem = 0;
    SharedPreferences ss;
    int amtt = 0;
    TextView getrecp;
    ImageView loaction_back;


    // int checkedItem=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_balencescreen);

        //alertbox dialog
        // setup the alert builder
        loaction_back = findViewById(R.id.imageback);
        loaction_back.setOnClickListener(this);

        inv_date = findViewById(R.id.inv_date);
        inv_month = findViewById(R.id.inv_month);
        inv_amt = findViewById(R.id.inv_amt);
        paid_amt = findViewById(R.id.paid_amt);
        total_amt = findViewById(R.id.totalamt);

        getrecp = findViewById(R.id.getrecp);
        getrecp.setPaintFlags(getrecp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        pd = new ProgressDialog(CollectionBalance.this);
        pd.setMessage("getting Data..");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.setCancelable(true);
        pd.show();
        ss = getSharedPreferences("Login", MODE_PRIVATE);
        spinner = (Spinner) findViewById(R.id.spinner1);
        pay = findViewById(R.id.pay);
        balamount = findViewById(R.id.balamount);
        balamount.setFocusable(false);
        balamount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                balamount.setFocusableInTouchMode(true); //to enable it
            }
        });
        li = findViewById(R.id.linearlayout);

        hspname = findViewById(R.id.hspname);

        balence_textview = findViewById(R.id.balence);
        balamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (set != null) {
                    if (s != null) {
                        String getdata = s.toString();
                        int enter = 0;
                        if (!getdata.equals(""))
                            enter = Integer.parseInt(getdata);
                        int bb = Integer.parseInt(set) - enter;

                        if (bb > 0) {
                            balence_textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                            balence_textview.setTextColor(Color.parseColor("#000000"));
                            balence_textview.setText("Balance = " + bb);

                        }
                        if (bb < 0) {
                            balence_textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            balence_textview.setTextColor(Color.parseColor("#ff0000"));
                            balence_textview.setText("Balance = " + bb);
                        } else if (bb == 0) {
                            balence_textview.setText("Balance = 0");
                        }
                    } else {
                        balence_textview.setText("Balance = 0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String am = balamount.getText().toString();
                amm = Integer.parseInt(am);
                // print.setVisibility(View.GONE);
                // success.setVisibility(View.GONE);
                if (amm <= balence && amm > 0) {//


                    final AlertDialog.Builder builder = new AlertDialog.Builder(CollectionBalance.this);
                    builder.setTitle("Pay Amount " + amm + "rs Through");


                    builder.setSingleChoiceItems(payment, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // user checked an item
                            //Toast.makeText(getBaseContext(), , Toast.LENGTH_SHORT).show();
                            ch = which;


                            //


                        }

                    });

// add OK and Cancel buttons
                    builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pd.show();
                            //Toast.makeText(getBaseContext(), paymode.get(ch), Toast.LENGTH_SHORT).show();
                            ch = ch + 1;
                            payAmount(amm, ch);
                            ch = 0;

                            // user clicked OK
                        }
                    });
                    builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();


                } else {
                    Toast.makeText(CollectionBalance.this, "Please Enter Valid Amount", Toast.LENGTH_SHORT).show();
                }

            }
        });

        plantsList = new ArrayList<>(Arrays.asList(plants));
        paymode = new ArrayList<>();

        // Initializing an ArrayAdapter
        spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.support_simple_spinner_dropdown_item, plantsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        listApi();
        spinnerArrayAdapter.notifyDataSetChanged();
        // Toast.makeText(getBaseContext(), responseBody, Toast.LENGTH_SHORT).show();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                if (!spinner.getSelectedItem().equals("Select Hospital")) {
                    li.setVisibility(View.GONE);
                }

                //   print.setVisibility(View.GONE);
                //  success.setVisibility(View.GONE);
                if (position != 0) {
                    String s = spinnerArrayAdapter.getItem(position).toString();
                    hspname.setText(s);
                    // Your code here
                    int pos = position - 1;

                    try {
                        JSONObject obj = new JSONObject(responseBody);
                        if (obj.getString("status").equals("true")) {

                            JSONArray array = obj.getJSONArray("data");
                            JSONObject res = array.getJSONObject(pos);

                            send = res.getInt("id");

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (send != 0) {

                        pd.show();

                        getTableDetails(send);

                    }
                }
                if (position == 0) {
                    Toast.makeText(CollectionBalance.this, "please select hospital", Toast.LENGTH_SHORT).show();
                }
                // selectionCurrent= position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void payAmount(int amt, int payid) {
        amtt = amt;
        RequestBody formBody = new FormBody.Builder().build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/paymentreceiptsformobile/" + send + "/" + amt + "/" + payid)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //login.setVisibility(View.GONE);
                Log.d("result", e.getMessage().toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

                //pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {


                String responseBody1 = response.body().string();

                final JSONObject obj;


                try {

                    obj = new JSONObject(responseBody1);
                    if (obj.getString("status").equals("true")) {

                        JSONObject data = obj.getJSONObject("data");
                        JSONArray ar = data.getJSONArray("receipt_details");
                        JSONObject rs = ar.getJSONObject(0);
                        String rid = rs.getString("receipt_id");


                        String s = String.valueOf(amtt);
                        String id = String.valueOf(send);


                        Intent i = new Intent(CollectionBalance.this, CollectinAgentPrintScreen.class);
                        i.putExtra("amt", s);
                        i.putExtra("hs_id", id);
                        i.putExtra("rid", rid);
                        pd.dismiss();
                        startActivity(i);


                        //  print.setVisibility(View.VISIBLE);
                        // success.setVisibility(View.VISIBLE);
                        // success.setText("successfully paid "+amt+"rs");


                        // getTableDetails(send);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    pd.dismiss();
                }

            }
        });


    }

    private void getTableDetails(int position) {

        //   Toast.makeText(collectioRecipts.this, position, Toast.LENGTH_SHORT).show();
        Log.d("positionList=", String.valueOf(position));


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/invoicedetailsofhospital/" + position)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //login.setVisibility(View.GONE);
                Log.d("result", e.getMessage().toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

                //pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {


                String responseBody1 = response.body().string();

                final JSONObject obj;


                try {
//                        Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT).show();
                    obj = new JSONObject(responseBody1);
                    if (obj.getString("status").equals("true")) {

                        JSONObject data = obj.getJSONObject("data");
                        JSONArray ar = data.getJSONArray("invoice_list");
                        if (ar.length() == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    li.setVisibility(View.INVISIBLE);

                                    Toast.makeText(getBaseContext(), "No data", Toast.LENGTH_SHORT).show();

                                    pd.dismiss();

                                }
                            });
                        } else {
                            for (int i = 0; i < ar.length(); i++) {
                                JSONObject rs = ar.getJSONObject(i);
                                invoice_id = rs.getString(("invoice_id"));
                                invoice_date = rs.getString("invoice_date").toString();
                                billing_month = rs.getString("billing_month").toString();
                                invoice_amount = String.valueOf(rs.getInt("invoice_amount"));
                                paid_amount = rs.getInt("paid_amount");
                                total_amount = String.valueOf(rs.getInt("total_amount"));
                                invoice_date = invoice_date.substring(0, 11);
                                finalInvoice_date = invoice_date;

//String inid,indt,inamt,pa,ta;
                                inid = inid + invoice_date + "\n";
                                indt = indt + billing_month + "\n";
                                inamt = inamt + invoice_amount + "\n";
                                pa = pa + paid_amount + "\n";
                                ta = ta + total_amount + "\n";


                                //       table=table+invoice_date+"    "+billing_month+"       "+invoice_amount+"          "+paid_amount+"          "+total_amount+"  \n";
                            }
                            //payment mode
                            JSONArray pay_mode = data.getJSONArray("payment_mode");
                            for (int i = 0; i < pay_mode.length(); i++) {
                                JSONObject res = pay_mode.getJSONObject(i);
                                paymode.add(res.getString("payment_method"));

                            }

                            payment = GetStringArray((ArrayList<String>) paymode);


                            JSONArray total = data.getJSONArray("total");
                            JSONObject bal = total.getJSONObject(0);
                            balence = bal.getInt("balance");
                            set = String.valueOf(balence);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    li.setVisibility(View.VISIBLE);

                                    inv_date.setText(inid);
                                    inv_month.setText(indt);
                                    inv_amt.setText(inamt);
                                    paid_amt.setText(pa);
                                    total_amt.setText(ta);
                                    //  just.setText(table);

                                    balamount.setText(set);
                                    //table = "";
                                    inid = "";
                                    indt = "";
                                    inamt = "";
                                    pa = "";
                                    ta = "";

                                    pd.dismiss();
                                }
                            });

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    pd.dismiss();
                }


            }


        });


    }


    public void listApi() {
        SharedPreferences ss = getSharedPreferences("Login", MODE_PRIVATE);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/hcflistforagent")
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //login.setVisibility(View.GONE);
                Log.d("result", e.getMessage().toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        Toast.makeText(CollectionBalance.this, "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

                //pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {


                responseBody = response.body().string();

                final JSONObject obj;


                try {
//                        Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT).show();

                    obj = new JSONObject(responseBody);
                    if (obj.getString("status").equals("true")) {

                        JSONArray array = obj.getJSONArray("data");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject res = array.getJSONObject(i);
                            //   mStrings.add(res.getString("facility_name"));
                            plantsList.add(res.getString("facility_name"));

                        }
                        pd.dismiss();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    pd.dismiss();
                }
            }


        });

    }

    public static String[] GetStringArray(ArrayList<String> arr) {

        // declaration and initialise String Array
        String str[] = new String[arr.size()];

        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {

            // Assign each value to String array
            str[j] = arr.get(j);
        }

        return str;
    }

    public void getRecipts(View view) {


        if (send != 0) {

            String ss = String.valueOf(send);
            Intent ii = new Intent(CollectionBalance.this, CollectinAgentReciptScreen.class);
            ii.putExtra("id", ss);
            startActivity(ii);


        } else {
            Toast.makeText(CollectionBalance.this, "please select Hospital", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageback:
                finish();
                break;

        }
    }
}
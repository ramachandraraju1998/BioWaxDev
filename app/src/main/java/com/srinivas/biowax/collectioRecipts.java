package com.srinivas.biowax;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class collectioRecipts extends AppCompatActivity {
    private Spinner spinner;

String[] plants = new String[]{
        "Select Hospital",

};

    String[] animals = {"Cash", "Chequ", "Debit", "Credit", "wallet"};
//hfgrhhgjgf

    String responseBody;
    List<String> plantsList;
    TextView just,success,hspname,balence_textview;
    ArrayAdapter<String> spinnerArrayAdapter;
    ProgressDialog pd;
    int send=0;
    String table="";
    Button pay;
    EditText balamount;
    int balence;
    String set;
    LinearLayout li;
    int amm,ch;
    ImageView print;

    SharedPreferences ss;
    int checkedItem=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collectio_recipts);

        //alertbox dialog
        // setup the alert builder




        pd = new ProgressDialog(collectioRecipts.this);
        pd.setMessage("getting Data..");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        ss = getSharedPreferences("Login", MODE_PRIVATE);
        spinner = (Spinner) findViewById(R.id.spinner1);
       pay=findViewById(R.id.pay);
       balamount=findViewById(R.id.balamount);
        li=findViewById(R.id.linearlayout);
        print=findViewById(R.id.printimage);
        hspname=findViewById(R.id.hspname);
        success=findViewById(R.id.success);
        balence_textview=findViewById(R.id.balence);
        balamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=("") || s.length()!=0 || s!=null) {
                String getdata=s.toString();
                    long enter=0;
                    if(!getdata.equals(""))
                     enter = Long.parseLong(getdata);
                    long bb = Long.parseLong(set) - enter;

if(bb>0) {
    balence_textview.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
    balence_textview.setTextColor(Color.parseColor("#000000"));
    balence_textview.setText("Balance = " + bb);

}
                    if(bb<0){
                        balence_textview.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                        balence_textview.setTextColor(Color.parseColor("#ff0000"));
                        balence_textview.setText("Balance = " + bb);
                    }else if(bb==0){
                        balence_textview.setText("Balance = 0");
                    }
                }else {
                    balence_textview.setText("Balance = 0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        pay.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
         String am=balamount.getText().toString();
           amm= Integer.parseInt(am);
               print.setVisibility(View.GONE);
               success.setVisibility(View.GONE);
               if(amm<=balence ){//&& amm>0


                   final AlertDialog.Builder builder = new AlertDialog.Builder(collectioRecipts.this);
                   builder.setTitle("Pay Amount "+amm+" Through");

                   builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           // user checked an item
                           ch=checkedItem;
                       //    ch=checkedItem;
                           //


                       }

                   });

// add OK and Cancel buttons
                   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           payAmount(amm);

                           // user clicked OK
                       }
                   });
                   builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
                   AlertDialog dialog = builder.create();
                   dialog.show();



               }else{
                   Toast.makeText(collectioRecipts.this, "Please Enter Valid Amount", Toast.LENGTH_SHORT).show();
               }

           }
       });
     just=findViewById(R.id.just);
     plantsList = new ArrayList<>(Arrays.asList(plants));

        // Initializing an ArrayAdapter
       spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,plantsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        listApi();
        spinnerArrayAdapter.notifyDataSetChanged();
       // Toast.makeText(getBaseContext(), responseBody, Toast.LENGTH_SHORT).show();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                print.setVisibility(View.GONE);
                success.setVisibility(View.GONE);
                if (position!=0){
                    String s=spinnerArrayAdapter.getItem(position).toString();
                    hspname.setText(s);
                    // Your code here
                    int pos=position-1;

                    try {
                        JSONObject obj= new JSONObject(responseBody);
                        if(obj.getString("status").equals("true")){

                            JSONArray array=obj.getJSONArray("data");
                            JSONObject res = array.getJSONObject(pos);

                            send=res.getInt("id");

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(send!=0) {

                        pd.show();

                        getTableDetails(send);

                    }
                }
                if(position==0){
                    Toast.makeText(collectioRecipts.this, "please select hospital", Toast.LENGTH_SHORT).show();
                }
               // selectionCurrent= position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void payAmount(final int amt) {

        RequestBody formBody = new FormBody.Builder().build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/paymentreceiptsformobile/"+send+"/"+amt)
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
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        print.setVisibility(View.VISIBLE);
        success.setVisibility(View.VISIBLE);
        success.setText("successfully paid "+amt+"rs");
        Toast.makeText(getBaseContext(), animals[ch].toString(), Toast.LENGTH_SHORT).show();
    }
});
    getTableDetails(send);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            });

ch=0;
    }

    private void getTableDetails(int position) {

            //   Toast.makeText(collectioRecipts.this, position, Toast.LENGTH_SHORT).show();
        Log.d("positionList=", String.valueOf(position));



        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/invoicedetailsofhospital/"+position)
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
                    obj=new JSONObject(responseBody1);
                    if(obj.getString("status").equals("true")){

                        JSONObject data = obj.getJSONObject("data");
                        JSONArray ar = data.getJSONArray("invoice_list");
                        if(ar.length()==0){  runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                li.setVisibility(View.INVISIBLE);
                                just.setText("NO DATA");
                                Toast.makeText(getBaseContext(),"No data", Toast.LENGTH_SHORT).show();

                                pd.dismiss();
                            }
                        }); }
                        for(int i=0;i<ar.length();i++){
                            JSONObject rs = ar.getJSONObject(i);
                            String invoice_id=rs.getString(("invoice_id"));
                            String invoice_date=rs.getString("invoice_date");
                            String billing_month=rs.getString("billing_month");
                            String invoice_amount=rs.getString("invoice_amount");
                            String paid_amount=rs.getString("paid_amount");
                            String total_amount=rs.getString("total_amount");
                            invoice_date = invoice_date.substring( 0, 11);
                             table=table+invoice_date+"    "+billing_month+"       "+invoice_amount+"          "+paid_amount+"          "+total_amount+"  \n";
                        }
                        JSONArray total= data.getJSONArray("total");
                        JSONObject bal=total.getJSONObject(0);
                       balence = bal.getInt("balance");
                      set = String.valueOf(balence);

                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             li.setVisibility(View.VISIBLE);
                             just.setText(table);

                            balamount.setText(set);
                             table="";
                             pd.dismiss();
                         }
                     });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    pd.dismiss();
                }


            }


        });




    }


    public void listApi()
    {
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
                        Toast.makeText(collectioRecipts.this, "Failed", Toast.LENGTH_SHORT).show();
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

                        obj=new JSONObject(responseBody);
                       if(obj.getString("status").equals("true")){

                            JSONArray array=obj.getJSONArray("data");

                            for(int i=0;i<array.length();i++){
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

}

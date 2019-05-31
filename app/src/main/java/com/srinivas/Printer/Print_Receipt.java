package com.srinivas.Printer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.srinivas.PrintBt.DeviceListActivity;
import com.srinivas.PrintBt.FirstActivity;
import com.srinivas.PrintBt.UnicodeFormatter;
import com.srinivas.PrintBt.Utils;
import com.srinivas.biowax.GarbageHistory;
import com.srinivas.biowax.R;
import com.srinivas.biowax.Transaction_Adapter;
import com.srinivas.biowax.Transactions;
import com.srinivas.validations.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.srinivas.PrintBt.FirstActivity.getCurrentTimeStamp;
import static com.srinivas.PrintBt.FirstActivity.intToByteArray;

public class Print_Receipt extends Activity implements Runnable {
    TextView trans_code_tv, weights_tv, netweight, headder;
    TextView barcode_list,barcode_color,barcode_weight;
    String br_list="",br_color="",br_weight="";
    ProgressDialog pd;
    String def;
    ImageView printer_img, history_back;
    int sum = 0;

    //print
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    ImageView mScan; //, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    EditText e1;
    ImageView im;
    boolean ch=false;

    boolean isCanceled = false;
    //var
    String hos;
    String hoscode;
    String transid;
    //json print
    JSONArray jsonArray;
    JSONObject data;
    JSONObject obj;
    String responseBody;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print__receipt);

        transid = getIntent().getStringExtra("transaction_code").toString();
        //printer

        barcode_list=findViewById(R.id.barcode_list);
        barcode_color=findViewById(R.id.barcode_color);
        barcode_weight=findViewById(R.id.barcode_weight);

        final SharedPreferences ss = getSharedPreferences("Login", MODE_PRIVATE);
        mScan = (ImageView) findViewById(R.id.mscan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if(ch==true){
                    if(mBluetoothAdapter!=null) {
                        print();
                    }else{   scan(); }
                }else {
                    scan();
                }
            }
        });


//        mPrint = (Button) findViewById(R.id.mPrint);
//        mPrint.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View mView) {
//print();
//    }
//});

//        mDisc = (Button) findViewById(R.id.dis);
//        mDisc.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View mView) {
//                if (mBluetoothAdapter != null)
//                    mBluetoothAdapter.disable();
//            }
//        });



        //



      //  weights_tv = findViewById(R.id.weights_tv);
        trans_code_tv = findViewById(R.id.trans_code_tv);
        printer_img = findViewById(R.id.printer_img);
        netweight = findViewById(R.id.netweight);
        headder = findViewById(R.id.headder);

        def =getIntent().getStringExtra("transaction_code").toString();
        trans_code_tv.setText(""+def+"\n");
      //  Toast.makeText(getBaseContext(),getIntent().getStringExtra("transaction_code"),Toast.LENGTH_SHORT).show();
        history_back = findViewById(R.id.history_back);
        history_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        printer_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (Validations.hasActiveInternetConnection(Print_Receipt.this)) {

                pd = new ProgressDialog(Print_Receipt.this);
                pd.setMessage("Generating Receipt..");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCancelable(true);
                pd.setMax(100);


                pd.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getRoutes(getIntent().getStringExtra("transaction_code"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },1000);



        } else {
            Toast.makeText(getBaseContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
            //  getcheckins_from_local();
        }






    }

    public void getRoutes(String xx) throws IOException {
       System.out.println("See here "+def);
        SharedPreferences ss = getSharedPreferences("Login", MODE_PRIVATE);
        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

          Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                //.url("http://175.101.151.121:8001/api/hcfwastecollectionviewformobile/EVB/TRANSACTION/35.1")
                 .url("http://175.101.151.121:8002/api/hcfwastecollectionviewformobile/"+xx.toString())
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
            public void onResponse(okhttp3.Call call,okhttp3.Response response) throws IOException {
                //  pd.dismiss();
                if (!response.isSuccessful()) {
                    pd.dismiss();
                    Log.d("result", response.toString());
                    throw new IOException("Unexpected code " + response);
                } else {
                    pd.dismiss();
                    Log.d("result", response.toString());
                 responseBody = response.body().string();
                    final JSONObject obj;
                    try {
                        obj = new JSONObject(responseBody);
                        if (obj.getString("status").equals("true")) {
                            System.out.println("hcfwastecollectiondataformobile " + obj.toString());
                            String vall ="";
                            data=obj.getJSONObject("data");
                            jsonArray = data.getJSONArray("hcfwastecollection");


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject res = jsonArray.getJSONObject(i);
                                Log.d("array val=",res.toString());
//                                Toast.makeText(Print_Receipt.this,res.toString(),Toast.LENGTH_LONG).show();;
                                String x = String.valueOf(i+1);

                                br_list=br_list+res.getString("barcode_number")+"\n";
                                br_color=br_color+res.getString("color_code")+"\n";
                                br_weight=br_weight+res.getString("bag_weight_in_hcf")+".00\n";

//                                vall = vall+"\t\t"+res.getString("barcode_number")+" \t\t\t "+res.getString("color_code")+" \t\t\t\t\t\t"+res.getString("bag_weight_in_hcf")+".00"+"\n ";
                                 //sum = sum+ Integer.parseInt(res.getString("bag_weight_in_hcf"));
                             }
  //total weight
                            JSONArray jarray = data.getJSONArray("totals");
                            JSONObject total = jarray.getJSONObject(0);
                            String totalweight=total.getString("total_weight");
                            String totalbags=total.getString("bags");
                            final String totals="Total Bags   : "+totalbags+"\n"+"  Total Weight : "+totalweight;

//headder
                            String time =getCurrentTimeStamp();
                            JSONObject res = jsonArray.getJSONObject(0);
                            hos=res.getString("facility_name");
                            hoscode=res.getString("hcf_unique_code");
                            final String head="Hospital : " +hos+"\n HCode  : "+hoscode+"\n Time : "+time;

//rows
                           // final String finalVall = vall;
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                  // Toast.makeText(getBaseContext(),finalVall,Toast.LENGTH_SHORT).show();
                                   headder.setText(head+"\n");
                                  //  weights_tv.setText(finalVall);

                                    barcode_list.setText(br_list);
                                    barcode_color.setText(br_color);
                                    barcode_weight.setText(br_weight);

                                    br_list="";br_color="";br_weight="";
                                    netweight.setText(totals);
                                   // String xxx = String.valueOf(sum);
                                   // netweight.setText("Total Net Weight :"+xxx);
                                }
                            });


                        } else {
                            System.out.println("JONDDDd " + obj.toString());
                            System.out.println("JONDDDd " + obj.getString("token"));

                         }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    //printer

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                ch=false;
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                ch=false;
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
      setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);

                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(Print_Receipt.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);

                } else {
                    Toast.makeText(Print_Receipt.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(Print_Receipt.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
            ch=true;
           print();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

    //print

    public void print(){
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    OutputStream os1 = mBluetoothSocket
                            .getOutputStream();

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                            R.drawable.bio);

                    byte[] command = Utils.decodeBitmap(bmp);

                    os.write(command);

                    String BILL = "";
                    String time = getCurrentTimeStamp();

                    // BILL=""+e1.getText().toString()+"\n";

                    BILL = "Hospital: "+ hos + "\n";
                    BILL = BILL + "Code    : " + hoscode + "\n" + "";
                    BILL = BILL + "Time    : " + time + "\n";
                    BILL = BILL + "TransID : " + transid + "\n";

                    BILL = BILL
                            + "--------------------------------\n";


                    BILL = BILL + String.format("%1$-8s %2$12s %3$10s", "Barcode", "BagColor", "Weight");
                    BILL = BILL
                            + "--------------------------------";
                    obj = new JSONObject(responseBody);
                    data = obj.getJSONObject("data");
                    jsonArray = data.getJSONArray("hcfwastecollection");
                    JSONArray jarray = data.getJSONArray("totals");
                    JSONObject total = jarray.getJSONObject(0);
                    String totalweight = total.getString("total_weight");
                    String totalbags = total.getString("bags");
                    final SharedPreferences ss = getSharedPreferences("Login", MODE_PRIVATE);


//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject res = jsonArray.getJSONObject(i);
//                                Log.d("array val=",res.toString());
////                                Toast.makeText(Print_Receipt.this,res.toString(),Toast.LENGTH_LONG).show();;
//                                String x = String.valueOf(i+1);
//                                vall = vall+"\t\t"+res.getString("barcode_number")+" \t\t\t "+res.getString("color_code")+" \t\t\t\t\t\t"+res.getString("bag_weight_in_hcf")+".00"+"\n ";
//                                //sum = sum+ Integer.parseInt(res.getString("bag_weight_in_hcf"));
//                            }


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject res = jsonArray.getJSONObject(i);
                        BILL = BILL + "\n " + String.format("%1$-13s %2$5s %3$8s ", res.getString("barcode_number"), res.getString("color_code"), res.getString("bag_weight_in_hcf"));

                    }
                    BILL = BILL
                            + "\n-------------------------------";
                    BILL = BILL + "\n ";

                    BILL = BILL + "      Total weight" + "  -  " + totalweight + "\n";
                    BILL = BILL + "       Total Bags" + "    -  " + totalbags + "\n";

                    BILL = BILL
                            + "-------------------------------\n\n";
                    BILL = BILL + "" + String.format("%1$-16s %2$10s  ", "", "  " + ss.getString("employee_name", ""));
                    BILL = BILL + " \n" + String.format("%1$-15s %2$13s  ", "Authorized", ss.getString("role_name", ""));
                    BILL = BILL + "\n\n\n ";


                    os1.write(BILL.getBytes()); 
                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));


                } catch (Exception e) {
                    Log.e("FirstActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }

    public void scan(){


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(Print_Receipt.this, "Message1", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
            }
            else {
                ListPairedDevices();
                Intent connectIntent = new Intent(Print_Receipt.this,
                        DeviceListActivity.class);
                //   print();
                startActivityForResult(connectIntent,
                        REQUEST_CONNECT_DEVICE);

            }
        }
    }

}





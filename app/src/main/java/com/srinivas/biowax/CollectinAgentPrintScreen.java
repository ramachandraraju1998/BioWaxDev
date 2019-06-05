package com.srinivas.biowax;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.srinivas.PrintBt.DeviceListActivity;
import com.srinivas.PrintBt.FirstActivity;
import com.srinivas.PrintBt.UnicodeFormatter;
import com.srinivas.PrintBt.Utils;
import com.srinivas.Printer.Print_Receipt;
import com.srinivas.biowax.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CollectinAgentPrintScreen extends AppCompatActivity implements Runnable{
    ProgressDialog pd;
    TextView success,due_amt;
    ImageView mScan;
    String hcf_id,amt,rid;
    String ridd,hcf_idd,hcf_name,recNum,recDate,amtReciced,totalamount,balamount,dueamt;
    SharedPreferences ss;
    TextView rcid,hsname,rcnumber,rcdate,amtpaid,balcamt;
    LinearLayout ll;
    boolean ch=false;
    String responseBody1;
    EditText e1;


    //blt
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collectin_agent_print_screen);





        ss= getSharedPreferences("Login", MODE_PRIVATE);
        success=findViewById(R.id.success);
        mScan=findViewById(R.id.printimage);

        pd = new ProgressDialog(CollectinAgentPrintScreen.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("getting Recipt Data..");
        pd.show();

        ll=findViewById(R.id.ll);
        //rcid=findViewById(R.id.rec_id);
        e1 = (EditText) findViewById(R.id.incdata);
        hsname=findViewById(R.id.hs_name);
        rcnumber=findViewById(R.id.rcp_num);
        rcdate=findViewById(R.id.rcp_date);
        amtpaid=findViewById(R.id.paid_amt);
        balcamt=findViewById(R.id.bal_amt);
        due_amt=findViewById(R.id.due_amt);


        amt=   getIntent().getStringExtra("amt");
        hcf_id =getIntent().getStringExtra("hs_id");
        rid =getIntent().getStringExtra("rid");

        Toast.makeText(CollectinAgentPrintScreen.this,"rid="+rid,Toast.LENGTH_SHORT).show();
       // Toast.makeText(CollectinAgentPrintScreen.this,"hid="+hcf_id,Toast.LENGTH_LONG).show();



        success.setText("successfully paid "+amt+"Rs");


getData();

        mScan = (ImageView) findViewById(R.id.printimage);
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




}



    private void scan() {


            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(CollectinAgentPrintScreen.this, "Message1", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                }
                else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(CollectinAgentPrintScreen.this,
                            DeviceListActivity.class);
                    //   print();
                    startActivityForResult(connectIntent,
                            REQUEST_CONNECT_DEVICE);

                }
            }

    }





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
                    Log.v("", "Coming incoming address " + mDeviceAddress);
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
                    Intent connectIntent = new Intent(CollectinAgentPrintScreen.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(CollectinAgentPrintScreen.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v("", "PairedDevices: " + mDevice.getName() + "  "
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
            Log.d("", "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d("", "SocketClosed");
        } catch (IOException ex) {
            Log.d("", "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(CollectinAgentPrintScreen.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
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


    private void getData() {


        RequestBody formBody = new FormBody.Builder().build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer" + ss.getString("access_token", ""))
                .url("http://175.101.151.121:8002/api/paymentreceiptprintformobile/"+rid+"/"+hcf_id)
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
                        pd.dismiss();

                        // Stuff that updates the UI
                        Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();

                    }
                });

                //pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {


               responseBody1 = response.body().string();

                final JSONObject obj;


                try {

                    obj = new JSONObject(responseBody1);
                    if (obj.getString("status").equals("true")) {

                        JSONObject data = obj.getJSONObject("data");
                        JSONArray ar = data.getJSONArray("receipt_details");
                        JSONObject rs = ar.getJSONObject(0);
                        ridd=rs.getString("receipt_id");
                        hcf_idd=rs.getString("hcf_master_id");
                        hcf_name=rs.getString("facility_name");
                        recNum=rs.getString("receipt_number");
                        recDate=rs.getString("receipt_date");
                        amtReciced=rs.getString("amount_received");



                        JSONArray inar = data.getJSONArray("invoice_details");
                        JSONObject inob = inar.getJSONObject(0);

                        totalamount=inob.getString("total_amount");
                        balamount=inob.getString("balance_amount");
                        //dueamt=inob.getString("total_amount");


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                               // rcid.setText("Receipt-id              :   "+ridd);
                                hsname.setText("Hospital Name     :   "+hcf_name);
                                rcnumber.setText("Receipt Number   :   "+recNum);
                                rcdate.setText("Receipt Date         :   "+recDate);
                                amtpaid.setText("Amount Paid          :   "+amtReciced+".00");
                                balcamt.setText("Balance Amount       :   "+balamount+".00");
                                due_amt.setText("Due Amount               :   "+totalamount+".00");
                                ll.setVisibility(View.VISIBLE);
                                pd.dismiss();

                            }
                        });



                        // getTableDetails(send);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        });
    }



    private void print() {


        Thread t = new Thread() {
            public void run() {
                try {
                    final SharedPreferences ss = getSharedPreferences("Login", MODE_PRIVATE);

                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    OutputStream os1 = mBluetoothSocket
                            .getOutputStream();

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                            R.drawable.bio);

                    byte[] command = Utils.decodeBitmap(bmp);



                    os.write(command);
                    String BILL ="\n";
                    BILL = BILL + "Hospital Name  : "+ hcf_name +"\n";
                    BILL = BILL + "Receipt Number : " + recNum +"\n" + "";
                    BILL = BILL + "Date   : " + recDate +"\n";


                    BILL = BILL+ "--------------------------------\n";

                    BILL = BILL +"Due Amount     : "+ totalamount +".00\n";
                    BILL = BILL +"Amount Paid    : "+ amtReciced +".00\n";
                    BILL = BILL +"Balance Amount : "+ balamount +".00\n";
                    BILL = BILL
                            + "--------------------------------\n";




//                            }


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



}

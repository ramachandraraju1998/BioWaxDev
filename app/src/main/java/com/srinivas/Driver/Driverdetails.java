package com.srinivas.Driver;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.srinivas.biowax.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Driverdetails extends Activity implements View.OnClickListener {
    ImageView imageback;
    SharedPreferences ss;
    EditText employeecode,employeename,employephone,email_id,rootname;
    TextView role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driverdetails);
        imageback = findViewById(R.id.imageback);
        imageback.setOnClickListener(this);
        role=findViewById(R.id.role);
        rootname = findViewById(R.id.rootname);
        email_id = findViewById(R.id.email_id);
        employephone= findViewById(R.id.employephone);
        employeename = findViewById(R.id.employeename);
        employeecode = findViewById(R.id.employeecode);

        ss = getSharedPreferences("Login", MODE_PRIVATE);
        ss.getString("access_token", "");
        String s=ss.getString("role_name","");
        Toast.makeText(Driverdetails.this,"role="+s,Toast.LENGTH_LONG).show();
        role.setText(s+" Details");
        try {
            JSONObject jsonObject = new JSONObject(ss.getString("data", "").toString());
            System.out.println("DADi srinivasu "+jsonObject.toString());
            JSONObject res = jsonObject.getJSONObject("user");
            JSONObject routes_masters_driver = res.getJSONObject("routes_masters_driver");



            employeecode.setText(res.getString("employee_code"));
            employephone.setText(res.getString("mobile_number"));
            employeename.setText(res.getString("employee_name"));
            email_id.setText(res.getString("email_id"));

            JSONObject truck = res.getJSONObject("routes_masters_driver");
            rootname.setText(truck.getString("route_name") + " ( " + routes_masters_driver.getString("route_number") + " )");
        } catch (JSONException e) {
            e.printStackTrace();
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

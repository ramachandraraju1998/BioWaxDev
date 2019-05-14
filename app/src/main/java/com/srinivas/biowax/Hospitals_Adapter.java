package com.srinivas.biowax;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Hospitals_Adapter extends RecyclerView.Adapter<Hospitals_Adapter.Hospital> {
    String confirm = "no";
    ArrayList<Hospitals> hospitals,hospitalsfl;
    int Rowlayout;
    Context context;

    public Hospitals_Adapter(ArrayList<Hospitals> hospitals, int check_single, Context applicationContext) {
        this.context = applicationContext;
        this.Rowlayout = check_single;
        this.hospitals = hospitals;
        this.hospitalsfl = hospitals;
    }

    @NonNull
    @Override
    public Hospitals_Adapter.Hospital onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(Rowlayout, viewGroup, false);
        return new Hospital(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Hospitals_Adapter.Hospital hospital, final int i) {
        hospital.routename.setText("Priority:"+hospitals.get(i).getH_code());
        hospital.hospitalname_tv.setText(hospitals.get(i).getH_name());
        hospital.contactno.setText(hospitals.get(i).getMobile());
        hospital.location.setText(hospitals.get(i).getH_address());
        hospital.hostpital_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor trans = context.getSharedPreferences("Transaction", MODE_PRIVATE).edit();
                trans.putString("trans", "");
                trans.putString("hosid", hospitals.get(i).getH_code());
                trans.commit();

                SharedPreferences ss = context.getSharedPreferences("Transaction", MODE_PRIVATE);
                if (ss.getString("hosid", "").equals(hospitals.get(i).getH_code())) {

                } else {
                    SharedPreferences.Editor transs = context.getSharedPreferences("Transaction", MODE_PRIVATE).edit();
                    transs.putString("trans", "");
                    transs.putString("hosid", hospitals.get(i).getH_code());
                    trans.commit();
                }

                SharedPreferences des = context.getSharedPreferences("Login", MODE_PRIVATE);
                if (des.getString("type","").equals("Driver")){
                    Intent biowaxform = new Intent(context, Biowastageform.class);
                    biowaxform.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    biowaxform.putExtra("hcf_id",hospitals.get(i).getHcf_id());
                    biowaxform.putExtra("hcfcode",hospitals.get(i).getH_code());
                    context.startActivity(biowaxform);
                }else {

                }



            }
        });
    }
    public void filteraddress(String charText) {
        charText = charText.toUpperCase(Locale.getDefault());
        // charText = charText.toUpperCase();
        // dealerses.clear();
        if (charText.length() == 0) {
            hospitals = hospitalsfl;
        } else {
            ArrayList<Hospitals> filteredList = new ArrayList<>();
            for (Hospitals androidVersion : hospitalsfl) {
                //  Toast.makeText(context,charText.toString(),Toast.LENGTH_SHORT).show();

                if (androidVersion.getH_name().toString().toLowerCase().contains(charText.toLowerCase())) {
                    filteredList.add(androidVersion);
                }

            }
            hospitals = filteredList;
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    public class Hospital extends RecyclerView.ViewHolder {
        TextView routename, hospitalname_tv, contactno, location;
        LinearLayout hostpital_ll;

        public Hospital(View itemView) {
            super(itemView);
            routename = (TextView) itemView.findViewById(R.id.routename);
            hospitalname_tv = (TextView) itemView.findViewById(R.id.hospitalname_tv);
            contactno = (TextView) itemView.findViewById(R.id.contactno);
            location = (TextView) itemView.findViewById(R.id.location);
            hostpital_ll = (LinearLayout) itemView.findViewById(R.id.hostpital_ll);
        }
    }


}

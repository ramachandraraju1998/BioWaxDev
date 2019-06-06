package com.srinivas.biowax;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;




import java.util.ArrayList;

public class CollectionagentAdapter extends RecyclerView.Adapter<CollectionagentAdapter.Hospital> {

        ArrayList<Collection> Collections;
        int Rowlayout;
        Context context;
        String id;
    public CollectionagentAdapter(ArrayList<Collection> Collections, String id, int check_single, Context applicationContext) {
        this.context = applicationContext;
        this.id=id;
        this.Rowlayout = check_single;
        this.Collections = Collections;
    }



    @NonNull
    @Override
    public CollectionagentAdapter.Hospital onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(Rowlayout, viewGroup, false);
        return new CollectionagentAdapter.Hospital(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Hospital hospital, final int i) {


        hospital.rec_num.setText("  "+Collections.get(i).getReceipt_number());
        hospital.time.setText("  " +Collections.get(i).getReceipt_date());

        hospital.hostpital_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int data=Collections.get(i).getId();
                String ss= String.valueOf(data);
                Intent biowaxform = new Intent(context, CollectinAgentPrintScreen.class);
                biowaxform.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                biowaxform.putExtra("rid",ss);
                biowaxform.putExtra("hs_id",id);
                context.startActivity(biowaxform);
            }
        });

    }




    public class Hospital extends RecyclerView.ViewHolder {
        TextView rec_num, time;
        LinearLayout hostpital_ll;

        public Hospital(View itemView) {
            super(itemView);
            hostpital_ll = (LinearLayout) itemView.findViewById(R.id.hostpital_ll);
            rec_num = (TextView) itemView.findViewById(R.id.rec_num);
            time = (TextView) itemView.findViewById(R.id.time);

        }
    }

    @Override
    public int getItemCount() {
        return Collections.size();
    }
}

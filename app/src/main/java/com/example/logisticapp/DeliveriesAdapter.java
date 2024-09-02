package com.example.logisticapp;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeliveriesAdapter extends RecyclerView.Adapter<DeliveriesAdapter.ViewHolder> {
    private ArrayList<String> data;
    private ArrayList<Double> latitudes = new ArrayList<>();
    private ArrayList<Double> longitudes = new ArrayList<>();
    private Context context;

    public DeliveriesAdapter(ArrayList<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.deliveries_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = data.get(position);
        holder.deliveries_order_id.setText("#"+item);
        holder.deliveries_pickup.setText(position+1+"");
        holder.deliveries_deliver.setText(position+1+"");
        Map<String,Object> map = new HashMap<>();
        map.put("deliveryStatus",true);
        holder.deliveries_tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int pos = position;
                FirebaseFirestore.getInstance().collection("Orders").document(item).
                        update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                notifyItemRemoved(pos);
                                data.remove(pos);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error"+e, Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView deliveries_pickup, deliveries_deliver,deliveries_order_id;
        ImageView deliveries_tick;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveries_pickup = itemView.findViewById(R.id.deliveries_pickup);
            deliveries_order_id = itemView.findViewById(R.id.deliveries_order_id);
            deliveries_deliver = itemView.findViewById(R.id.deliveries_deliver);
            deliveries_tick = itemView.findViewById(R.id.deliveries_tick);
        }
    }
}

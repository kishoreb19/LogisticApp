package com.example.logisticapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final ArrayList<String> orderIds;
    private final Map<String, OrderDetails> orderDetailsMap;
    private final Context context;

    public OrderAdapter(Context context, ArrayList<String> orderIds, Map<String, OrderDetails> orderDetailsMap) {
        this.context = context;
        this.orderIds = orderIds;
        this.orderDetailsMap = orderDetailsMap;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        String orderId = orderIds.get(position);
        OrderDetails orderDetails = orderDetailsMap.get(orderId);

        if (orderDetails != null) {
            holder.order_item_date.setText(orderDetails.getDate() != null ? orderDetails.getDate() : "No Date");
            holder.order_item_id.setText("#" + orderId);
            if(orderDetails.getDeliveryStatus()){
                holder.order_item_delivered.setVisibility(View.VISIBLE);
            }else{
                holder.order_item_not_delivered.setVisibility(View.VISIBLE);
            }
            holder.order_item_lay.setOnClickListener(view -> {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("OrderDetailsObj", orderDetails);
                intent.putExtra("order_id",orderId);
                context.startActivity(intent);
            });
        } else {
            holder.order_item_date.setText("No Details");
            holder.order_item_id.setText(orderId);
        }
    }

    @Override
    public int getItemCount() {
        return orderIds.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView order_item_date, order_item_id,order_item_not_delivered,order_item_delivered;
        LinearLayout order_item_lay;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            order_item_lay = itemView.findViewById(R.id.order_item_lay);
            order_item_date = itemView.findViewById(R.id.order_item_date);
            order_item_id = itemView.findViewById(R.id.order_item_id);
            order_item_not_delivered = (TextView) itemView.findViewById(R.id.order_item_not_delivered);
            order_item_delivered = (TextView) itemView.findViewById(R.id.order_item_delivered);
        }
    }
}

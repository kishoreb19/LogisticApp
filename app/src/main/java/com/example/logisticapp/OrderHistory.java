package com.example.logisticapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderHistory extends AppCompatActivity {

    ArrayList<String> orderIds;
    Map<String, OrderDetails> map;
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    ImageView back_orderHistory_act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        back_orderHistory_act = (ImageView) findViewById(R.id.back_orderHistory_act);
        back_orderHistory_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        recyclerView = findViewById(R.id.order_history_recyclerView);
        orderIds = new ArrayList<>(); //List of order ids
        map = new HashMap<>();// Map -> orderid with orderDetails Object

        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
        String user_phn = sp.getString("phone","no-phn");

        CollectionReference cr = FirebaseFirestore.getInstance().collection("Orders");


        //Fetching Order details of current user
        cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String orderId = documentSnapshot.getId();

                        OrderDetails orderDetails = documentSnapshot.toObject(OrderDetails.class);
                        if (orderDetails != null && orderDetails.getPhone().toString().equals(user_phn)) {
                            orderIds.add(orderId);
                            map.put(orderId, orderDetails);
                        } else {
                            Log.w("OrderHistory", "Document data is null for ID: " + orderId);
                        }
                    }
                    updateRecyclerView();
                } else {
                    Log.w("OrderHistory", "QuerySnapshot is null");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OrderHistory", "Error Fetching Data", e);
                Toast.makeText(OrderHistory.this, "Error Fetching: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView() {
        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(OrderHistory.this,orderIds, map);
        recyclerView.setAdapter(orderAdapter);
    }
}


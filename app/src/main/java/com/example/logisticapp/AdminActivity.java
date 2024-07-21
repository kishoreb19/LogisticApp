package com.example.logisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    ImageView logout_admin_act;
    ArrayList<String> orderIds;
    Map<String, OrderDetails> map;
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    ImageView back_orderHistory_act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        logout_admin_act = (ImageView)findViewById(R.id.logout_admin_act);


        logout_admin_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(AdminActivity.this,LoginActivity.class));
            }
        });
        recyclerView = findViewById(R.id.order_history_recyclerView);
        orderIds = new ArrayList<>();
        map = new HashMap<>();

        CollectionReference cr = FirebaseFirestore.getInstance().collection("Orders");

        cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String orderId = documentSnapshot.getId();
                        orderIds.add(orderId);
                        OrderDetails orderDetails = documentSnapshot.toObject(OrderDetails.class);
                        if (orderDetails != null) {
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
                Toast.makeText(AdminActivity.this, "Error Fetching: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView() {
        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(AdminActivity.this,orderIds, map);
        recyclerView.setAdapter(orderAdapter);
    }

}
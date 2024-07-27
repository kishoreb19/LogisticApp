package com.example.logisticapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DeliveriesActivity extends AppCompatActivity {

    ArrayList<String> data;
    RecyclerView deliveriesRV;
    ImageView back_deliveries_act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deliveries);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        data = new ArrayList<>();
        deliveriesRV = (RecyclerView) findViewById(R.id.deliveriesRV);
        back_deliveries_act = (ImageView) findViewById(R.id.back_deliveries_act);

        back_deliveries_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        CollectionReference cr = FirebaseFirestore.getInstance().collection("Orders");
        cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    OrderDetails obj = documentSnapshot.toObject(OrderDetails.class);
                    if(!obj.getDeliveryStatus()){
                        data.add(documentSnapshot.getId());
                    }
                }
                Log.d("test",data.toString());
                updateRV();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DeliveriesActivity.this, "Error Fetching: "+e, Toast.LENGTH_SHORT).show();
            }
        });


    }
    void updateRV(){
        DeliveriesAdapter adapter = new DeliveriesAdapter(data,this);
        deliveriesRV.setLayoutManager(new LinearLayoutManager(DeliveriesActivity.this));
        deliveriesRV.setAdapter(adapter);
    }
}
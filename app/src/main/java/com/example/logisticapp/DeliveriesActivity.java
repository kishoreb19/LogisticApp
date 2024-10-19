package com.example.logisticapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DeliveriesActivity extends AppCompatActivity {

    ArrayList<String> data;
    ArrayList<Double> latitudes = new ArrayList<>();
    ArrayList<Double> longitudes = new ArrayList<>();
    ArrayList<String> mapUrlsPickup = new ArrayList<>();
    ArrayList<String> mapUrlsDelivery = new ArrayList<>();

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
                        latitudes.add(obj.getSender_lat());
                        latitudes.add(obj.getReceiver_lat());
                        longitudes.add(obj.getSender_lng());
                        longitudes.add(obj.getReceiver_lng());
                    }
                }
                for(int i=0;i<latitudes.size();i=i+2){
                    mapUrlsPickup.add("https://www.google.com/maps/dir/"+latitudes.get(i)+","+longitudes.get(i)+"/"+latitudes.get(i+1)+","+longitudes.get(i+1)+"/");
                }
                for(int i=1;i<latitudes.size();i=i+2){
                    if (i!=latitudes.size()-1){
                        mapUrlsDelivery.add("https://www.google.com/maps/dir/"+latitudes.get(i)+","+longitudes.get(i)+"/"+latitudes.get(i+1)+","+longitudes.get(i+1)+"/");
                    }else{
                        mapUrlsDelivery.add("https://www.google.com/maps/dir/"+latitudes.get(i)+","+longitudes.get(i)+"/");
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
        DeliveriesAdapter adapter = new DeliveriesAdapter(data,mapUrlsPickup,mapUrlsDelivery,this);
        deliveriesRV.setLayoutManager(new LinearLayoutManager(DeliveriesActivity.this));
        deliveriesRV.setAdapter(adapter);
    }
}
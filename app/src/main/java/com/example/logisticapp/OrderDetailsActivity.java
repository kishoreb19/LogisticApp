package com.example.logisticapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class OrderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    ImageView back_orderdetails_act;
    TextView order_details_id,order_details_user_name,order_details_user_phn, length, breadth, height, weight, pickupStart, pickupEnd, deliveryStart, deliveryEnd;
    LinearLayout delivery_status_delivered, delivery_status_to_be_delivered;
    GoogleMap myMap;
    double sender_lat, sender_lng, receiver_lat, receiver_lng;
    private static final String TAG = "OrderDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        order_details_id = findViewById(R.id.order_details_id);
        order_details_user_name = findViewById(R.id.order_details_user_name);
        order_details_user_phn = findViewById(R.id.order_details_user_phn);
        back_orderdetails_act = findViewById(R.id.back_orderdetails_act);
        length = findViewById(R.id.length);
        breadth = findViewById(R.id.breadth);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        pickupStart = findViewById(R.id.pickupStart);
        pickupEnd = findViewById(R.id.pickupEnd);
        deliveryStart = findViewById(R.id.deliveryStart);
        deliveryEnd = findViewById(R.id.deliveryEnd);
        delivery_status_delivered = findViewById(R.id.delivery_status_delivered);
        delivery_status_to_be_delivered = findViewById(R.id.delivery_status_to_be_delivered);

        back_orderdetails_act.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        OrderDetails obj = (OrderDetails) intent.getSerializableExtra("OrderDetailsObj");

        if (obj == null) {
            Log.e(TAG, "OrderDetails object is null");
            return;
        }

        order_details_id.setText("#" + intent.getStringExtra("order_id"));
        order_details_user_name.setText(obj.getUser_name());
        order_details_user_phn.setText("+91-"+obj.getPhone());
        length.setText(Integer.toString(obj.getLength()));
        breadth.setText(Integer.toString(obj.getBreadth()));
        height.setText(Integer.toString(obj.getHeight()));
        weight.setText(Integer.toString(obj.getWeight()));

        pickupStart.setText(obj.getPickupStartHour() + " : " + obj.getPickupStartMinute());
        pickupEnd.setText(obj.getPickupEndHour() + " : " + obj.getPickupEndMinute());

        deliveryStart.setText(obj.getDeliveryStartHour() + " : " + obj.getDeliveryStartMinute());
        deliveryEnd.setText(obj.getDeliveryEndHour() + " : " + obj.getDeliveryEndMinute());

        Boolean deliveryStatus = obj.getDeliveryStatus();

        if (deliveryStatus) {
            delivery_status_delivered.setVisibility(View.VISIBLE);
        } else {
            delivery_status_to_be_delivered.setVisibility(View.VISIBLE);
        }

        sender_lat = obj.getSender_lat();
        sender_lng = obj.getSender_lng();
        receiver_lat = obj.getReceiver_lat();
        receiver_lng = obj.getReceiver_lng();

        Log.d(TAG, "Sender Location: " + sender_lat + ", " + sender_lng);
        Log.d(TAG, "Receiver Location: " + receiver_lat + ", " + receiver_lng);

        initializeMap();
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(OrderDetailsActivity.this);
        } else {
            Log.e(TAG, "Map Fragment is null");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        if (myMap != null) {
            LatLng sender = new LatLng(sender_lat, sender_lng);
            LatLng receiver = new LatLng(receiver_lat, receiver_lng);

            Log.d(TAG, "Adding marker for sender at: " + sender.toString());
            Log.d(TAG, "Adding marker for receiver at: " + receiver.toString());

            myMap.addMarker(new MarkerOptions().position(sender).title("Pickup"));
            myMap.addMarker(new MarkerOptions().position(receiver).title("Deliver"));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sender, 12));
        } else {
            Log.e(TAG, "Google Map is null");
        }
    }
}

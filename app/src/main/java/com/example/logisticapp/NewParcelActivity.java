package com.example.logisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class NewParcelActivity extends AppCompatActivity implements OnMapReadyCallback {

    LinearLayout parcelScreenContent;
    LinearLayout parcelScreenMapView, orderSuccessfulScreen;
    TextView order_id;
    ImageView back_parcel_act;
    GoogleMap myMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button getCurrentLocationBtn, pickupStartTime, pickupEndTime, deliveryStartTime, deliveryEndTime;
    Double sender_lat, sender_lng, receiver_lat, receiver_lng;
    EditText newParcel_length, newParcel_breadth, newParcel_height, newParcel_weight;
    Button newParcel_Book;

    int pickupStartHour, pickupStartMinute, pickupEndHour, pickupEndMinute, deliveryStartHour, deliveryStartMinute, deliveryEndHour, deliveryEndMinute;

    CollectionReference cr = FirebaseFirestore.getInstance().collection("Orders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_parcel);

        parcelScreenContent = findViewById(R.id.parcelScreenContent);
        parcelScreenMapView = findViewById(R.id.parcelScreenMapView);
        orderSuccessfulScreen = findViewById(R.id.orderSuccessfulScreen);
        order_id = findViewById(R.id.order_id);
        newParcel_length = findViewById(R.id.newParcel_length);
        newParcel_breadth = findViewById(R.id.newParcel_breadth);
        newParcel_height = findViewById(R.id.newParcel_height);
        newParcel_weight = findViewById(R.id.newParcel_weight);
        newParcel_Book = findViewById(R.id.newParcel_Book);
        pickupStartTime = findViewById(R.id.pickupStartTime);
        pickupEndTime = findViewById(R.id.pickupEndTime);
        deliveryStartTime = findViewById(R.id.deliveryStartTime);
        deliveryEndTime = findViewById(R.id.deliveryEndTime);

        Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_anim);

        // Back Button
        back_parcel_act = findViewById(R.id.back_parcel_act);
        back_parcel_act.setOnClickListener(v -> finish());

        // Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocationBtn = findViewById(R.id.getCurrentLocationBtn);

        // Initializing Google Maps API
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(NewParcelActivity.this);

        // Sender's current location fetching and marking
        getSenderLocation();
        new Handler().postDelayed(this::getSenderLocation, 1000);

        getCurrentLocationBtn.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);
            getCurrentLocationBtn.setText("Please Wait");
            getSenderLocation();
        });

        pickupStartTime.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);
            TimePickerDialog timePickerDialog = new TimePickerDialog(NewParcelActivity.this, (view, hourOfDay, minute) -> {
                pickupStartHour = hourOfDay;
                pickupStartMinute = minute;
                pickupStartTime.setText(hourOfDay + " : " + minute);
            }, 15, 0, true);
            timePickerDialog.show();
        });

        pickupEndTime.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);
            TimePickerDialog timePickerDialog = new TimePickerDialog(NewParcelActivity.this, (view, hourOfDay, minute) -> {
                pickupEndHour = hourOfDay;
                pickupEndMinute = minute;
                pickupEndTime.setText(hourOfDay + " : " + minute);
            }, 15, 0, true);
            timePickerDialog.show();
        });

        deliveryStartTime.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);
            TimePickerDialog timePickerDialog = new TimePickerDialog(NewParcelActivity.this, (view, hourOfDay, minute) -> {
                deliveryStartHour = hourOfDay;
                deliveryStartMinute = minute;
                deliveryStartTime.setText(hourOfDay + " : " + minute);
            }, 15, 0, true);
            timePickerDialog.show();
        });

        deliveryEndTime.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);
            TimePickerDialog timePickerDialog = new TimePickerDialog(NewParcelActivity.this, (view, hourOfDay, minute) -> {
                deliveryEndHour = hourOfDay;
                deliveryEndMinute = minute;
                deliveryEndTime.setText(hourOfDay + " : " + minute);
            }, 15, 0, true);
            timePickerDialog.show();
        });

        // Shared Preferences
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String user_phn = sp.getString("phone", "no-phn");
        String user_name = sp.getString("firstName", "no-fn") + " " + sp.getString("lastName", "no-fn");

        newParcel_Book.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);

            // Extract values from the input fields
            String lengthStr = newParcel_length.getText().toString();
            String breadthStr = newParcel_breadth.getText().toString();
            String heightStr = newParcel_height.getText().toString();
            String weightStr = newParcel_weight.getText().toString();

            // Validation Check: Check for empty fields and null values
            if (lengthStr.isEmpty() || breadthStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(NewParcelActivity.this, "Please fill in all parcel dimensions and weight!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate that location coordinates are set
            if (sender_lat == null || sender_lng == null) {
                Toast.makeText(NewParcelActivity.this, "Please ensure the sender's location is set!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (receiver_lat == null || receiver_lng == null) {
                Toast.makeText(NewParcelActivity.this, "Please select the receiver's location on the map!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate pickup and delivery times
            if (pickupStartHour == 0 && pickupStartMinute == 0 || pickupEndHour == 0 && pickupEndMinute == 0) {
                Toast.makeText(NewParcelActivity.this, "Please set valid pickup times!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (deliveryStartHour == 0 && deliveryStartMinute == 0 || deliveryEndHour == 0 && deliveryEndMinute == 0) {
                Toast.makeText(NewParcelActivity.this, "Please set valid delivery times!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ensure that pickup start time is earlier than pickup end time
            if (pickupStartHour > pickupEndHour || (pickupStartHour == pickupEndHour && pickupStartMinute >= pickupEndMinute)) {
                Toast.makeText(NewParcelActivity.this, "Pickup start time must be earlier than pickup end time!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ensure that delivery start time is earlier than delivery end time
            if (deliveryStartHour > deliveryEndHour || (deliveryStartHour == deliveryEndHour && deliveryStartMinute >= deliveryEndMinute)) {
                Toast.makeText(NewParcelActivity.this, "Delivery start time must be earlier than delivery end time!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ensure that the pickup end time is earlier than the delivery start time
            if (pickupEndHour > deliveryStartHour || (pickupEndHour == deliveryStartHour && pickupEndMinute >= deliveryStartMinute)) {
                Toast.makeText(NewParcelActivity.this, "Pickup end time must be earlier than delivery start time!", Toast.LENGTH_SHORT).show();
                return;
            }

            // All validations passed, proceed to create the OrderDetails object
            LocalDate localDate = LocalDate.now();

            // Creating object containing all the details of parcels and user
            OrderDetails obj = new OrderDetails(
                    Integer.parseInt(lengthStr),
                    Integer.parseInt(breadthStr),
                    Integer.parseInt(heightStr),
                    Integer.parseInt(weightStr),
                    sender_lat,
                    sender_lng,
                    receiver_lat,
                    receiver_lng,
                    pickupStartHour, pickupStartMinute,
                    pickupEndHour, pickupEndMinute,
                    deliveryStartHour, deliveryStartMinute,
                    deliveryEndHour, deliveryEndMinute,
                    localDate.toString(), user_phn, false, user_name
            );

            // Adding object to Firebase
            cr.add(obj).addOnSuccessListener(documentReference -> {
                parcelScreenContent.setVisibility(View.GONE);
                parcelScreenMapView.setVisibility(View.GONE);
                orderSuccessfulScreen.setVisibility(View.VISIBLE);

                order_id.setText("#" + documentReference.getId());

                new Handler().postDelayed(NewParcelActivity.this::finish, 1400);
            }).addOnFailureListener(e ->
                    Toast.makeText(NewParcelActivity.this, "Error Uploading Details!", Toast.LENGTH_SHORT).show()
            );
        });


    }

    // Initializing Google Map (myMap)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        // Default Sender location
        LatLng sender = new LatLng(22.3210777, 87.3079438);

        myMap.setOnMapClickListener(latLng -> {
            // Clears all existing markers
            myMap.clear();

            // Sender's location
            LatLng senderLocation = new LatLng(sender_lat, sender_lng);
            myMap.addMarker(new MarkerOptions().position(senderLocation).icon(setIcon(NewParcelActivity.this, R.drawable.sender_pin)).title("Sender"));

            // Receiver's location
            receiver_lat = latLng.latitude;
            receiver_lng = latLng.longitude;
            LatLng receiver = new LatLng(receiver_lat, receiver_lng);
            myMap.addMarker(new MarkerOptions().position(receiver).icon(setIcon(NewParcelActivity.this, R.drawable.receiver_pin)).title("Receiver"));

            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(receiver, 10), 1000, null);
        });

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sender, 10), 1000, null);
    }

    // Fetching sender's current location
    private void getSenderLocation() {
        if (ActivityCompat.checkSelfPermission(NewParcelActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewParcelActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    sender_lat = location.getLatitude();
                    sender_lng = location.getLongitude();

                    myMap.clear();
                    LatLng sender = new LatLng(sender_lat, sender_lng);
                    myMap.addMarker(new MarkerOptions().position(sender).icon(setIcon(NewParcelActivity.this, R.drawable.sender_pin)).title("Sender"));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sender, 10), 1000, null);
                }
            });
        }
    }

    // BitmapDescriptor for setting custom marker icons
    private BitmapDescriptor setIcon(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}

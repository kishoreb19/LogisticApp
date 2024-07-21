package com.example.logisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewParcelActivity extends AppCompatActivity implements OnMapReadyCallback {

    ScrollView parcelScreenContent;
    LinearLayout orderSuccessfulScreen;
    ImageView back_parcel_act;
    GoogleMap myMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button getCurrentLocationBtn,pickupStartTime,pickupEndTime,deliveryStartTime,deliveryEndTime;
    Double sender_lat,sender_lng,receiver_lat,receiver_lng;
    EditText newParcel_length,newParcel_breadth,newParcel_height,newParcel_weight;
    Button newParcel_Book;

    int pickupStartHour,pickupStartMinute,pickupEndHour,pickupEndMinute,deliveryStartHour,deliveryStartMinute,deliveryEndHour,deliveryEndMinute;

    CollectionReference cr = FirebaseFirestore.getInstance().collection("Orders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_parcel);


        parcelScreenContent = (ScrollView) findViewById(R.id.parcelScreenContent);
        orderSuccessfulScreen = (LinearLayout) findViewById(R.id.orderSuccessfulScreen);
        newParcel_length = (EditText) findViewById(R.id.newParcel_length);
        newParcel_breadth = (EditText) findViewById(R.id.newParcel_breadth);
        newParcel_height = (EditText) findViewById(R.id.newParcel_height);
        newParcel_weight = (EditText) findViewById(R.id.newParcel_weight);
        newParcel_Book = (Button) findViewById(R.id.newParcel_Book);
        pickupStartTime = (Button) findViewById(R.id.pickupStartTime);
        pickupEndTime = (Button) findViewById(R.id.pickupEndTime);
        deliveryStartTime = (Button) findViewById(R.id.deliveryStartTime);
        deliveryEndTime = (Button) findViewById(R.id.deliveryEndTime);

        Animation buttonAnimation = AnimationUtils.loadAnimation(this,R.anim.button_anim);


        //Back Button
        back_parcel_act = (ImageView) findViewById(R.id.back_parcel_act);
        back_parcel_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocationBtn = (Button) findViewById(R.id.getCurrentLocationBtn);

        //Initialising Google Maps Api
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(NewParcelActivity.this);



        getSenderLocation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSenderLocation();
                getSenderLocation();
            }
        },1000);
        
        getCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);
                getCurrentLocationBtn.setText("Please Wait");
                getSenderLocation();
            }
        });

        pickupStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewParcelActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        pickupStartHour = hourOfDay;
                        pickupStartMinute = minute;
                        pickupStartTime.setText(""+hourOfDay+" : "+minute);
                    }
                },15,00,true);
                timePickerDialog.show();
            }
        });

        pickupEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewParcelActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        pickupEndHour = hourOfDay;
                        pickupEndMinute = minute;
                        pickupEndTime.setText(""+hourOfDay+" : "+minute);
                    }
                },15,00,true);
                timePickerDialog.show();
            }
        });

        deliveryStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewParcelActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        deliveryStartHour = hourOfDay;
                        deliveryStartMinute = minute;
                        deliveryStartTime.setText(""+hourOfDay+" : "+minute);
                    }
                },15,00,true);
                timePickerDialog.show();
            }
        });

        deliveryEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewParcelActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        deliveryEndHour = hourOfDay;
                        deliveryEndMinute = minute;
                        deliveryEndTime.setText(""+hourOfDay+" : "+minute);
                    }
                },15,00,true);
                timePickerDialog.show();
            }
        });

        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);

        String user_phn = sp.getString("phone","no-phn");

        newParcel_Book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);

                String lengthStr = newParcel_length.getText().toString();
                String breadthStr = newParcel_breadth.getText().toString();
                String heightStr = newParcel_height.getText().toString();
                String weightStr = newParcel_weight.getText().toString();

//                Check if all fields are filled
                if (lengthStr.isEmpty() || breadthStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty() ||
                        sender_lat == 0.00 || sender_lng == 0.00 || receiver_lat == 0.00 || receiver_lng == 0.00 ||
                        pickupStartHour == 0 || pickupStartMinute == 0 ||
                        pickupEndHour == 0 || pickupEndMinute == 0 ||
                        deliveryStartHour == 0 || deliveryStartMinute == 0 ||
                        deliveryEndHour == 0 || deliveryEndMinute == 0) {
                    Toast.makeText(NewParcelActivity.this, "Please fill all details!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Creating object containing all the details
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
                        deliveryEndHour, deliveryEndMinute);

                cr.document(user_phn).set(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        parcelScreenContent.setVisibility(View.GONE);
                        orderSuccessfulScreen.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1400);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewParcelActivity.this, "Error Uploading Details !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        LatLng sender = new LatLng(22.3210777,87.3079438);
//        myMap.addMarker(new MarkerOptions().position(sender).title("Sender"));
//        myMap.moveCamera(CameraUpdateFactory.newLatLng(sender));
//        myMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                myMap.clear();

                //Sender
                LatLng sender = new LatLng(sender_lat,sender_lng);
                myMap.addMarker(new MarkerOptions().position(sender).icon(setIcon(NewParcelActivity.this,R.drawable.sender_pin)).title("Sender"));

                //Receiver
                receiver_lat = latLng.latitude;
                receiver_lng = latLng.longitude;
                Toast.makeText(NewParcelActivity.this,Double.toString(receiver_lat)+","+Double.toString(receiver_lng), Toast.LENGTH_SHORT).show();
                myMap.addMarker(new MarkerOptions().position(latLng).icon(setIcon(NewParcelActivity.this,R.drawable.receiver_pin)).title("Receiver"));
                myMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            }
        });

    }
    void getSenderLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},0);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    sender_lat = location.getLatitude();
                    sender_lng = location.getLongitude();

                    LatLng sender = new LatLng(sender_lat,sender_lng);
                    myMap.addMarker(new MarkerOptions().position(sender).icon(setIcon(NewParcelActivity.this,R.drawable.sender_pin)).title("Sender"));
                    myMap.moveCamera(CameraUpdateFactory.newLatLng(sender));
                    myMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                }else{
                    Toast.makeText(NewParcelActivity.this, "Error retrieving the location!", Toast.LENGTH_SHORT).show();
                    getCurrentLocationBtn.setText("Locate Me");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewParcelActivity.this,e.toString(),Toast.LENGTH_SHORT);
            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                getCurrentLocationBtn.setText("Locate Me");
            }
        });
    }

    BitmapDescriptor setIcon(Context context, int drawable){
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, drawable);

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
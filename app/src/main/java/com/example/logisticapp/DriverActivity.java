package com.example.logisticapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.DirectionsApi;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<LatLng> locations;
    TextView driver_welcome_text;
    ImageView logout_driver_act;
    ArrayList<Double> latitudes = new ArrayList<>();
    ArrayList<Double> longitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        driver_welcome_text = findViewById(R.id.driver_welcome_text);
        logout_driver_act = findViewById(R.id.logout_driver_act);

        logout_driver_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(DriverActivity.this, LoginActivity.class));
            }
        });

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String name = sp.getString("firstName", "FirstName") + " " + sp.getString("lastName", "LastName");
        driver_welcome_text.setText("Welcome, " + name);

        locations = new ArrayList<>();

        //Fetching locations(lat,lng) from firebase
        CollectionReference cr = FirebaseFirestore.getInstance().collection("Orders");
        cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    OrderDetails obj = documentSnapshot.toObject(OrderDetails.class);
                    latitudes.add(obj.getSender_lat());
                    latitudes.add(obj.getReceiver_lat());
                    longitudes.add(obj.getSender_lng());
                    longitudes.add(obj.getReceiver_lng());
                }
                // Now add LatLng objects to locations list and log the data
                for (int i = 0; i < latitudes.size(); i++) {
                    locations.add(new LatLng(latitudes.get(i), longitudes.get(i)));
                }
                // Initialize the map only after locations list is populated
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.driver_map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(DriverActivity.this);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DriverActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        int i = 1;
        boolean snap = false;
        String t="";
        for (LatLng location : locations) {

            if(!snap){
                t="Pickup";
            }else{
                t="Deliver";
            }

            snap = !snap;
            mMap.addMarker(new MarkerOptions().position(location).title(t+" "+i));
            if(t.equals("Deliver")) i++;

            mMap.moveCamera(CameraUpdateFactory.newLatLng(locations.get(0)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }

        drawRoute();
    }

    private void drawRoute() {
        if (locations.size() > 1) {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyDmlnxhstdEONvN9Ejr9pmdYbsVLs6idH0")
                    .build();

            String[] waypoints = new String[locations.size()];
            for (int i = 0; i < locations.size(); i++) {
                waypoints[i] = locations.get(i).latitude + "," + locations.get(i).longitude;
            }

            DirectionsApi.newRequest(context)
                    .origin(waypoints[0])
                    .destination(waypoints[waypoints.length - 1])
                    .waypoints(waypoints)
                    .optimizeWaypoints(true)
                    .setCallback(new PendingResult.Callback<DirectionsResult>() {
                        @Override
                        public void onResult(DirectionsResult result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (result.routes != null && result.routes.length > 0) {
                                            DirectionsRoute route = result.routes[0];
                                            List<LatLng> decodedPath = new ArrayList<>();
                                            EncodedPolyline polyline = route.overviewPolyline;
                                            List<com.google.maps.model.LatLng> decodedLatLngs = polyline.decodePath();
                                            for (com.google.maps.model.LatLng latLng : decodedLatLngs) {
                                                decodedPath.add(new LatLng(latLng.lat, latLng.lng));
                                            }
                                            PolylineOptions polylineOptions = new PolylineOptions()
                                                    .addAll(decodedPath)
                                                    .width(10)
                                                    .color(Color.BLUE);
                                            mMap.addPolyline(polylineOptions);
                                        } else {
                                            Log.e("RouteError", "No routes found");
                                        }
                                    } catch (Exception e) {
                                        Log.e("RouteError", "Exception while drawing route: ", e);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            Log.e("RouteError", "Error getting route: ", e);
                        }
                    });
        } else {
            Log.e("RouteError", "Not enough locations to draw a route");
        }
    }

}


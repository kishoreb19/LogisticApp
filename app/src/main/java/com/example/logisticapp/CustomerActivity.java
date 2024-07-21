package com.example.logisticapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomerActivity extends AppCompatActivity {

    LinearLayout trackOrder,orderHistory,newParcelPickup;
    ImageView logout_parcel_act;
    TextView customer_name_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        trackOrder = (LinearLayout) findViewById(R.id.trackOrder);
        orderHistory = (LinearLayout) findViewById(R.id.orderHistory);
        newParcelPickup = (LinearLayout) findViewById(R.id.newParcelPickup);
        logout_parcel_act = (ImageView) findViewById(R.id.logout_parcel_act);
        customer_name_show = (TextView) findViewById(R.id.customer_name_show);

        Animation buttonAnimation = AnimationUtils.loadAnimation(this,R.anim.button_anim);

        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
        String firstName = sp.getString("firstName","--");
        String lastName = sp.getString("lastName","--");
        customer_name_show.setText("Welcome \n"+firstName+" "+lastName);

        trackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);
                startActivity(new Intent(CustomerActivity.this,OrderDetailsActivity.class));
            }
        });

        orderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);
                startActivity(new Intent(CustomerActivity.this,OrderHistory.class));
            }
        });

        newParcelPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);
                startActivity(new Intent(CustomerActivity.this,NewParcelActivity.class));
            }
        });

        logout_parcel_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(CustomerActivity.this,LoginActivity.class));
            }
        });

    }
}
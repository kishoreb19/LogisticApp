package com.example.logisticapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
        boolean flag = sp.getBoolean("flag",false);
        String userType = sp.getString("userType","--");
        if(flag==true){
            //If user is logged in
            if(userType.equals("Customer")){
                //Opens Customer Activity
                startActivity(new Intent(SplashScreen.this,CustomerActivity.class));
                finish();
            }else if(userType.equals("Driver")){
                //Opens Driver Activity
                startActivity(new Intent(SplashScreen.this,DriverActivity.class));
                finish();
            } else if (userType.equals("Administrator")) {
                //Opens Administrator Activity
                startActivity(new Intent(SplashScreen.this,AdminActivity.class));
                finish();
            }else{
                Toast.makeText(SplashScreen.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        }else{
            //If user is not logged in redirect to LoginActivity
            startActivity(new Intent(SplashScreen.this,LoginActivity.class));
        }
    }
}

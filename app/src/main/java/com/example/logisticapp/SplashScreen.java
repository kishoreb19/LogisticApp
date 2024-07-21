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
//            startActivity(new Intent(SplashScreen.this,CustomerActivity.class));
            if(userType.equals("Customer")){
                startActivity(new Intent(SplashScreen.this,CustomerActivity.class));
                finish();
            }else if(userType.equals("Driver")){
                startActivity(new Intent(SplashScreen.this,DriverActivity.class));
                finish();
            } else if (userType.equals("Administrator")) {
                startActivity(new Intent(SplashScreen.this,AdminActivity.class));
                finish();
            }else{
                Toast.makeText(SplashScreen.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        }else{
            startActivity(new Intent(SplashScreen.this,LoginActivity.class));
        }
    }
}

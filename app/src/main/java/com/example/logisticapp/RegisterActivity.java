package com.example.logisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ImageView register_back;
    Spinner register_userType;
    EditText register_user_firstname,register_user_lastname, register_userPhn,register_userPwd;
    Button registerBtn;
    TextView loginActivityBtn;
    ScrollView registerScreenContent;
    LinearLayout registerSuccessfulScreen;
    String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_back = (ImageView) findViewById(R.id.register_back);
        register_userType = (Spinner) findViewById(R.id.register_userType);
        register_user_firstname = (EditText) findViewById(R.id.register_user_firstname);
        register_user_lastname = (EditText) findViewById(R.id.register_user_lastname);
        register_userPhn = (EditText) findViewById(R.id.register_userPhn);
        register_userPwd = (EditText) findViewById(R.id.register_userPwd);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        loginActivityBtn = (TextView) findViewById(R.id.loginActivityBtn);
        registerScreenContent = (ScrollView) findViewById(R.id.registerScreenContent);
        registerSuccessfulScreen = (LinearLayout) findViewById(R.id.registerSuccessfulScreen);

        FirebaseAuth fa = FirebaseAuth.getInstance();
        CollectionReference ff = FirebaseFirestore.getInstance().collection("Users");

        String []arr = {"Customer","Driver","Administrator"};
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,arr);
        register_userType.setAdapter(userTypeAdapter);

        Animation buttonAnimation = AnimationUtils.loadAnimation(this,R.anim.button_anim);

        register_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        registerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.startAnimation(buttonAnimation);
//                registerScreenContent.setVisibility(View.GONE);
//                registerSuccessfulScreen.setVisibility(View.VISIBLE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
//                        finish();
//                    }
//                },1400);
//            }
//        });
//        register_userType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(RegisterActivity.this, arr[position], Toast.LENGTH_SHORT).show();
//            }
//        });

        register_userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = register_userType.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);
                DocumentReference dr = ff.document(register_userPhn.getText().toString());

                Map<String,String> map = new HashMap<>();
                map.put("userType",userType);
                map.put("firstname",register_user_firstname.getText().toString());
                map.put("lastname",register_user_lastname.getText().toString());
                map.put("pwd",register_userPwd.getText().toString());

                dr.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        registerScreenContent.setVisibility(View.GONE);
                        registerSuccessfulScreen.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                finish();
                            }
                        },1400);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Unable to register!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loginActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

    }
}
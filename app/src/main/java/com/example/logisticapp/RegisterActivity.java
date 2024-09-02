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
    EditText register_user_firstname, register_user_lastname, register_userPhn, register_userPwd;
    Button registerBtn;
    TextView loginActivityBtn;
    ScrollView registerScreenContent;
    LinearLayout registerSuccessfulScreen;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_back = findViewById(R.id.register_back);
        register_userType = findViewById(R.id.register_userType);
        register_user_firstname = findViewById(R.id.register_user_firstname);
        register_user_lastname = findViewById(R.id.register_user_lastname);
        register_userPhn = findViewById(R.id.register_userPhn);
        register_userPwd = findViewById(R.id.register_userPwd);
        registerBtn = findViewById(R.id.registerBtn);
        loginActivityBtn = findViewById(R.id.loginActivityBtn);
        registerScreenContent = findViewById(R.id.registerScreenContent);
        registerSuccessfulScreen = findViewById(R.id.registerSuccessfulScreen);

        // Firebase Initialization
        FirebaseAuth fa = FirebaseAuth.getInstance();
        CollectionReference ff = FirebaseFirestore.getInstance().collection("Users");

        String[] arr = {"Customer", "Driver", "Administrator"};
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, arr);
        userTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        register_userType.setAdapter(userTypeAdapter);

        Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_anim);

        register_back.setOnClickListener(v -> finish());

        register_userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = register_userType.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        registerBtn.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);

            // Validation
            if (isValidInput()) {
                DocumentReference dr = ff.document(register_userPhn.getText().toString());

                // Creating map object consisting of usertype, firstname, lastname, and password
                Map<String, String> map = new HashMap<>();
                map.put("userType", userType);
                map.put("firstname", register_user_firstname.getText().toString());
                map.put("lastname", register_user_lastname.getText().toString());
                map.put("pwd", register_userPwd.getText().toString());

                // Adding map data to Firebase
                dr.set(map).addOnSuccessListener(unused -> {
                    registerScreenContent.setVisibility(View.GONE);
                    registerSuccessfulScreen.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }, 1400);
                }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Unable to register!", Toast.LENGTH_SHORT).show());
            }
        });

        // Login Activity button
        loginActivityBtn.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private boolean isValidInput() {
        if (register_user_firstname.getText().toString().trim().isEmpty()) {
            register_user_firstname.setError("First name is required");
            return false;
        }
        if (register_user_lastname.getText().toString().trim().isEmpty()) {
            register_user_lastname.setError("Last name is required");
            return false;
        }
        if (register_userPhn.getText().toString().trim().isEmpty()) {
            register_userPhn.setError("Phone number is required");
            return false;
        }
        if (register_userPwd.getText().toString().trim().isEmpty()) {
            register_userPwd.setError("Password is required");
            return false;
        }
        if (userType == null || userType.isEmpty()) {
            Toast.makeText(this, "User type is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

package com.example.logisticapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity {

    Spinner login_userType;
    EditText userPhn, userPwd;
    Button userLoginBtn;
    ScrollView loginScreenContent;
    LinearLayout successfulScreen;
    TextView registerActivityBtn;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_userType = findViewById(R.id.loginUserType);
        userPhn = findViewById(R.id.userPhn);
        userPwd = findViewById(R.id.userPwd);
        userLoginBtn = findViewById(R.id.userLoginBtn);
        loginScreenContent = findViewById(R.id.loginScreenContent);
        successfulScreen = findViewById(R.id.loginSuccessfulScreen);
        registerActivityBtn = findViewById(R.id.registerActivityBtn);

        // Firebase Initialization
        FirebaseAuth fa = FirebaseAuth.getInstance();
        CollectionReference ff = FirebaseFirestore.getInstance().collection("Users");

        String[] arr = {"Customer", "Driver", "Administrator"};
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, arr);
        userTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        login_userType.setAdapter(userTypeAdapter);

        // Button Animation
        Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_anim);

        // Collecting User Type
        login_userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = login_userType.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Login Button Function
        userLoginBtn.setOnClickListener(v -> {
            v.startAnimation(buttonAnimation);

            // Validation
            if (isValidInput()) {
                // Fetching and checking credentials from Firebase Firestore Database
                ff.document(userPhn.getText().toString()).addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && value.exists()) {
                        String usert = value.getString("userType");
                        String pwd = value.getString("pwd");

                        if (usert != null && pwd != null && usert.equals(userType) && pwd.equals(userPwd.getText().toString())) {
                            // Load login screen animation
                            loginScreenContent.setVisibility(View.GONE);
                            successfulScreen.setVisibility(View.VISIBLE);

                            // Opens activity after a delay of 1400 milliseconds based on user type
                            new Handler().postDelayed(() -> {
                                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putBoolean("flag", true);
                                edit.putString("userType", userType);
                                edit.putString("firstName", value.getString("firstname"));
                                edit.putString("lastName", value.getString("lastname"));
                                edit.putString("phone", userPhn.getText().toString());
                                edit.apply();

                                if (userType.equals("Customer")) {
                                    startActivity(new Intent(LoginActivity.this, CustomerActivity.class));
                                } else if (userType.equals("Driver")) {
                                    startActivity(new Intent(LoginActivity.this, DriverActivity.class));
                                } else if (userType.equals("Administrator")) {
                                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                } else {
                                    Toast.makeText(LoginActivity.this, "Incorrect User Type!", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }, 1400);
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect Credentials!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Opens Register Activity on button click
        registerActivityBtn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private boolean isValidInput() {
        if (userPhn.getText().toString().trim().isEmpty()) {
            userPhn.setError("Phone number is required");
            return false;
        }
        if (userPwd.getText().toString().trim().isEmpty()) {
            userPwd.setError("Password is required");
            return false;
        }
        if (userType == null || userType.isEmpty()) {
            Toast.makeText(this, "User type is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

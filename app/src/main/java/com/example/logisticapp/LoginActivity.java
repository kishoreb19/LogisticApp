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

        login_userType = (Spinner) findViewById(R.id.loginUserType);
        userPhn = (EditText) findViewById(R.id.userPhn);
        userPwd = (EditText) findViewById(R.id.userPwd);
        userLoginBtn = (Button) findViewById(R.id.userLoginBtn);
        loginScreenContent = (ScrollView) findViewById(R.id.loginScreenContent);
        successfulScreen = (LinearLayout) findViewById(R.id.loginSuccessfulScreen);
        registerActivityBtn = (TextView) findViewById(R.id.registerActivityBtn);

        //Firebase Initialisation
        FirebaseAuth fa = FirebaseAuth.getInstance();
        CollectionReference ff = FirebaseFirestore.getInstance().collection("Users");



        String []arr = {"Customer","Driver","Administrator"};
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,arr);
        login_userType.setAdapter(userTypeAdapter);
//        login_userType.getSelectedItem().toString();

        //Button Animation
        Animation buttonAnimation = AnimationUtils.loadAnimation(this,R.anim.button_anim);


        //Collecting User type
        login_userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = login_userType.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Login Button Function
        userLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonAnimation);

                //Fetching and checking credentials from FireBase Firestore Database
                ff.document(userPhn.getText().toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String usert = value.get("userType").toString();
                        String pwd = value.get("pwd").toString();

                        if(usert.equals(userType) && pwd.equals(userPwd.getText().toString())){
                            //Load login screen animation
                            loginScreenContent.setVisibility(View.GONE);
                            successfulScreen.setVisibility(View.VISIBLE);

                            //Opens activity after a delay 1400 milli seconds based on user type
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putBoolean("flag",true);
                                    edit.putString("userType",userType);
                                    edit.putString("firstName",value.get("firstname").toString());
                                    edit.putString("lastName",value.get("lastname").toString());
                                    edit.putString("phone", userPhn.getText().toString());
                                    edit.apply();


                                    if(userType=="Customer"){
                                        startActivity(new Intent(LoginActivity.this,CustomerActivity.class));
                                        finish();
                                    }else if(userType=="Driver"){
                                        startActivity(new Intent(LoginActivity.this,DriverActivity.class));
                                        finish();
                                    } else if (userType=="Administrator") {
                                        startActivity(new Intent(LoginActivity.this,AdminActivity.class));
                                        finish();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Incorrect User Type!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },1400);
                        }else{
                            Toast.makeText(LoginActivity.this, "Incorrect Credentials!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        //Opens Register Activity on button click
        registerActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }
}
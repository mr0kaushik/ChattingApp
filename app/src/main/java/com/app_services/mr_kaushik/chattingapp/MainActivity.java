package com.app_services.mr_kaushik.chattingapp;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    EditText email, password;
    Button login, register;
    TextView forget_password;

    FirebaseUser firebaseUser;
    FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            Intent intent = new Intent(MainActivity.this, MainChatActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            Intent intent = new Intent(MainActivity.this, MainChatActivity.class);
            startActivity(intent);
            finish();
        }

        progressBar = findViewById(R.id.progress_bar);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        forget_password = findViewById(R.id.forget_password);
        forget_password.setPaintFlags(forget_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        auth = FirebaseAuth.getInstance();

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                    email.setError("Invalid Email Address");
                    login.setEnabled(false);
                    login.setFocusable(false);
                    login.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                }
                else if (password.length()>7){
                    login.setEnabled(true);
                    login.setFocusable(true);
                    login.setBackgroundColor(getResources().getColor(R.color.colorButton));
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(password.getText().toString().trim().length() > 7)) {
                    password.setError("Password must be 8 character long");
                    login.setEnabled(false);
                    login.setFocusable(false);
                    login.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                }
                else if (email.length()>0) {
                    login.setEnabled(true);
                    login.setFocusable(true);
                    login.setBackgroundColor(getResources().getColor(R.color.colorButton));
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(MainActivity.this, MainChatActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Authentication Failed! Please Enter valid Email & Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setBackgroundColor(getResources().getColor(R.color.colorButton));
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forget_password.setTextColor(getResources().getColor(R.color.colorBlue));
                startActivity(new Intent(MainActivity.this, ForgetActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        forget_password.setTextColor(getResources().getColor(R.color.colorGrey));
        register.setBackgroundColor(getResources().getColor(R.color.colorGrey));
    }

}

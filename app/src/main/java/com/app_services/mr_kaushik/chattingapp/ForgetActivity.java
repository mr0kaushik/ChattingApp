package com.app_services.mr_kaushik.chattingapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgetActivity extends AppCompatActivity {
    EditText email;
    Button btn_reset;
    TextView tv_clicking;

    FirebaseAuth firebaseAuth;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        email = findViewById(R.id.email);
        btn_reset = findViewById(R.id.btn_reset);
        tv_clicking = findViewById(R.id.clicking);
        firebaseAuth = FirebaseAuth.getInstance();

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.getText().toString().equals("")){
                    btn_reset.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString();
                if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(ForgetActivity.this, "Field is required!", Toast.LENGTH_SHORT).show();
                } else {
                    btn_reset.setBackgroundColor(getResources().getColor(R.color.colorButton));
                    tv_clicking.setVisibility(View.VISIBLE);
                    firebaseAuth.sendPasswordResetEmail(str_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgetActivity.this, "Check Your Inbox!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgetActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(ForgetActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });
    }
}

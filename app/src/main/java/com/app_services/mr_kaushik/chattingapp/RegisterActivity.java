package com.app_services.mr_kaushik.chattingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    ProgressBar progressBar;

    EditText fName, username, email, password, confirmPassword;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        progressBar = findViewById(R.id.progress_bar);

        auth = FirebaseAuth.getInstance();
        fName = findViewById(R.id.fName);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        btn_register = findViewById(R.id.btn_register);



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_fName = fName.getText().toString();
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_conPassword = confirmPassword.getText().toString();

                if (TextUtils.isEmpty(txt_fName) || TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_conPassword)) {

                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                if(txt_password.length() < 8){

                    Toast.makeText(RegisterActivity.this, "Password must be at least 8 character long!", Toast.LENGTH_SHORT).show();
                }
                if (!txt_password.equals(txt_conPassword)) {

                    Toast.makeText(RegisterActivity.this, "Password not matched!, Please write it again!", Toast.LENGTH_SHORT).show();
                }
                register(txt_fName, txt_username, txt_email, txt_password);
            }
        });

    }

    private void register(final String fName, final String username,
                          final String email, final String password){

        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Log.i("Task is Unsuccessful", "onComplete: Task: error" + task.getResult().toString());
                        Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String userId = firebaseUser.getUid();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                        HashMap<String, String> hashMap = new HashMap<>();
                        putDataOnHash(hashMap, fName, email, userId, username);

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this,MainChatActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
            });

    }

    private void putDataOnHash(HashMap<String, String> hashMap, String fName, String email, String userId, String username) {
        String default_status = "Hey I'm using Chatting App.";
        String default_user_status = "offline";
        String default_image = "default";
        hashMap.put("id", userId);
        hashMap.put("name", fName);
        hashMap.put("username", username);
        hashMap.put("email", email);
        hashMap.put("imageURL", default_image);
        hashMap.put("status", default_user_status);
        hashMap.put("searchable_name", username.toLowerCase());
        hashMap.put("user_about", default_status);
    }

}


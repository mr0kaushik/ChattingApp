package com.app_services.mr_kaushik.chattingapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app_services.mr_kaushik.chattingapp.Adapter.MessageAdapter;
import com.app_services.mr_kaushik.chattingapp.UserModel.Chat;
import com.app_services.mr_kaushik.chattingapp.UserModel.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    RelativeLayout user_profile_view;
    CircleImageView profile_pic;
    TextView tv_username;

    ImageButton btn_send;
    EditText editText;


    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent intent;
    String userId;

    MessageAdapter messageAdapter;
    List<Chat> chat_list;

    ValueEventListener seenListener;

    RecyclerView recyclerView;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, MainChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        user_profile_view = findViewById(R.id.user_profile_view);
        profile_pic = findViewById(R.id.profile_pic);
        tv_username = findViewById(R.id.username);
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        btn_send = findViewById(R.id.btn_send);
        editText = findViewById(R.id.et_message);

        recyclerView = findViewById(R.id.recycler_view_for_chats);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        user_profile_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                if(!message.trim().equals("")){
                    sendMessage(firebaseUser.getUid(), userId, message);
                }
                editText.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                tv_username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_pic.setImageResource(R.mipmap.ic_default_profile_pic);
                } else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_pic);
                }
                readMessage(firebaseUser.getUid(), userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Database Error", "onCancelled: Error -> "+ databaseError.toString());
            }
        });

        isSeen(userId);
    }

    private void isSeen(final String userId){
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isSeen", false);

        reference.child("Chats").push().setValue(hashMap);
        final DatabaseReference chatReferenceForSender = FirebaseDatabase.getInstance().getReference("ChatLists")
                .child(firebaseUser.getUid())
                .child(userId);

        chatReferenceForSender.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatReferenceForSender.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatReferenceForReceiver = FirebaseDatabase.getInstance().getReference("ChatLists")
                .child(receiver)
                .child(firebaseUser.getUid());

        chatReferenceForReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatReferenceForReceiver.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessage(final String readerId, final String senderId){
        chat_list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chat_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(readerId) && chat.getSender().equals(senderId) || chat.getReceiver().equals(senderId) && chat.getSender().equals(readerId)) {
                        chat_list.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, chat_list);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("DB Error in readMessage", "onCancelled: Error -> "+ databaseError.toString());
            }
        });


    }



    private void Status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        Status("offline");
    }

}

package com.app_services.mr_kaushik.chattingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app_services.mr_kaushik.chattingapp.MessageActivity;
import com.app_services.mr_kaushik.chattingapp.R;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;
    private boolean isInChat;
    private String lastMessage;

    public UserAdapter(Context context, List<User> userList, Boolean isInChat){
        this.context = context;
        this.userList = userList;
        this.isInChat = isInChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = userList.get(i);
        viewHolder.tv_username.setText(user.getUsername());
        if (user.getImageURL().equals("default")){
            viewHolder.profile_pic.setImageResource(R.mipmap.ic_default_profile_pic);
        } else {
            Glide.with(context).load(user.getImageURL()).into(viewHolder.profile_pic);
        }

        Log.i("USER LIST : ", "onBindViewHolder: USER DETAILS = " + user.toString());

        if (isInChat){
            checkLastMessage(user.getId(), viewHolder.tv_user_about_or_last_message);
        } else {
            viewHolder.tv_user_about_or_last_message.setText(user.getUser_about());
        }
        if (isInChat){
            Log.i("USER STATUS", "onBindViewHolder: status = " + user.getStatus());

            switch (user.getStatus()) {
                case "online":
                    viewHolder.status.setImageResource(R.drawable.shape_bubble_online);
                    break;
                case "offline":
                    viewHolder.status.setImageResource(R.drawable.shape_bubble_offline);
                    break;

            }
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            }
        });
        viewHolder.tv_user_about_or_last_message.setText(user.getUser_about());

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_username;
        TextView tv_user_about_or_last_message;
        ImageView profile_pic;
        CircleImageView status;

        ViewHolder(View itemView){
            super(itemView);
            tv_username = itemView.findViewById(R.id.username);
            tv_user_about_or_last_message = itemView.findViewById(R.id.tv_user_about_or_last_message);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            status = itemView.findViewById(R.id.status_icon);
        }
    }

    private void checkLastMessage(final String userId, final TextView tv_user_about_or_last_message){
        lastMessage = "";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        assert firebaseUser != null;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                                chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                            lastMessage = chat.getMessage();
                        }
                    }


                    switch (lastMessage){
                        case "":
                            tv_user_about_or_last_message.setText("");
                            break;

                        default:
                            tv_user_about_or_last_message.setText(lastMessage);
                            break;
                    }
                    lastMessage = "";
                } catch (NullPointerException e){
                    Log.i("NullPointerException", "onDataChange: " + e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.app_services.mr_kaushik.chattingapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app_services.mr_kaushik.chattingapp.R;
import com.app_services.mr_kaushik.chattingapp.UserModel.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int  MSG_TYPE_SENT = 1;
    private static final int  MSG_TYPE_RECEIVED = 0;
    private Context context;
    private List<Chat> chatList;


    public MessageAdapter(Context context, List<Chat> chatList){
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MSG_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sent_chat_item, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.received_chat_item, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Chat chat = chatList.get(i);
        viewHolder.tv_message_text.setText(chat.getMessage());
        Log.i("DEBUG via COUNT", "onBindViewHolder: isSeen = " + chat.getIsSeen() );

        if (i <= chatList.size()-1){
            if (chat.getIsSeen()){
                viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_seen_icon));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_seen_double_tick, context.getApplicationContext().getTheme()));
//                } else {
//                    viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_seen_double_tick));
//                }


            } else {
                viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delivered_icon));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_deliver_indicator, context.getApplicationContext().getTheme()));
//                } else {
//                    viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_deliver_indicator));
//                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_message_text;
        ImageView iv_seen;
        TextView messageTimeStamp;

        ViewHolder(View itemView){
            super(itemView);
            tv_message_text = itemView.findViewById(R.id.messageText);
            iv_seen = itemView.findViewById(R.id.iv_seen);
            messageTimeStamp = itemView.findViewById(R.id.messageTimeStamp);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_SENT;
        } else {
            return MSG_TYPE_RECEIVED;
        }
    }
}


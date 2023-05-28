package com.example.telechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telechat.activities.ChatActivity;
import com.example.telechat.activities.MainDoctorActivity;
import com.example.telechat.databinding.ConversationItemBinding;
import com.example.telechat.models.Conversation;

import java.util.ArrayList;

public class ConversationWorkerAdapter extends RecyclerView.Adapter<ConversationWorkerAdapter.ConversationViewHolder> {
    Context mainDoctorActivity;
    ArrayList<Conversation> conversations;

    public ConversationWorkerAdapter(MainDoctorActivity mainDoctorActivity, ArrayList<Conversation> conversations) {
        this.mainDoctorActivity = mainDoctorActivity;
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConversationItemBinding conversationItemBinding = ConversationItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ConversationViewHolder(conversationItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.setUserData(conversation);
        holder.itemView.setOnClickListener(event -> {
            Intent intent = new Intent(mainDoctorActivity, ChatActivity.class);
            intent.putExtra("name", conversation.getReceiverName());
            intent.putExtra("receiverImage", conversation.getImage());
            intent.putExtra("uid", conversation.getReceiverId());
            mainDoctorActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        ConversationItemBinding binding;

        ConversationViewHolder(ConversationItemBinding conversationItemBinding) {
            super(conversationItemBinding.getRoot());
            binding = conversationItemBinding;
        }

        void setUserData(Conversation conversation) {
            binding.userName.setText(conversation.receiverName);
            binding.lastMessage.setText(conversation.message);
            binding.userImage.setImageBitmap(getUserImage(conversation.image));
        }
    }

    private Bitmap getUserImage(String image){
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}

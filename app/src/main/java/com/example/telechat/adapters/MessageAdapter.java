package com.example.telechat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telechat.databinding.ReceiveMessageItemBinding;
import com.example.telechat.databinding.SendMessageItemBinding;
import com.example.telechat.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Message> messages;
    int VIEW_TYPE_SENT = 1;
    int VIEW_TYPE_RECEIVED = 2;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SenderViewHolder(SendMessageItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false));
        }else{
            return new ReceiverViewHolder(ReceiveMessageItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SenderViewHolder) holder).setData(messages.get(position));
        }else {
            ((ReceiverViewHolder) holder).setData(messages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder{
        private final SendMessageItemBinding binding;

        SenderViewHolder(SendMessageItemBinding sendMessageItemBinding){
            super(sendMessageItemBinding.getRoot());
            binding = sendMessageItemBinding;
        }

        public void setData(Message message) {
            binding.textMessage.setText(message.message);
            binding.textDateTime.setText(message.dateTime);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder{
        private final ReceiveMessageItemBinding binding;

        ReceiverViewHolder(ReceiveMessageItemBinding receiveMessageItemBinding){
            super(receiveMessageItemBinding.getRoot());
            binding = receiveMessageItemBinding;
        }

        public void setData(Message message) {
            binding.textMessage.setText(message.message);
            binding.textDateTime.setText(message.dateTime);
        }
    }
}
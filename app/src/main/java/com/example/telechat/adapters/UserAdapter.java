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
import com.example.telechat.activities.UserActivity;
import com.example.telechat.databinding.UserItemBinding;
import com.example.telechat.models.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    Context usersActivity;
    ArrayList<User> users;

    public UserAdapter(UserActivity userActivity, ArrayList<User> users) {
        this.usersActivity = userActivity;
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserItemBinding userItemBinding = UserItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(userItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.setUserData(user);
        holder.itemView.setOnClickListener(event -> {
            Intent intent = new Intent(usersActivity, ChatActivity.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("receiverImage", user.getImage());
            intent.putExtra("uid", user.getUid());
            usersActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        UserItemBinding binding;

        UserViewHolder(UserItemBinding userItemBinding) {
            super(userItemBinding.getRoot());
            binding = userItemBinding;
        }

        public void setUserData(User user) {
            binding.userName.setText(user.name);
            binding.userStatus.setText(user.status);
            binding.userImage.setImageBitmap(getUserImage(user.image));
        }
    }

    private Bitmap getUserImage(String image){
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}

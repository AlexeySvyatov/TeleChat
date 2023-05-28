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
import com.example.telechat.activities.NewChatActivity;
import com.example.telechat.databinding.DoctorItemBinding;
import com.example.telechat.models.Doctor;

import java.util.ArrayList;

public class NewChatAdapter extends RecyclerView.Adapter<NewChatAdapter.UserViewHolder> {
    Context newChatActivity;
    ArrayList<Doctor> doctors;

    public NewChatAdapter(NewChatActivity newChatActivity, ArrayList<Doctor> doctors) {
        this.newChatActivity = newChatActivity;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public NewChatAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DoctorItemBinding doctorItemBinding = DoctorItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(doctorItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewChatAdapter.UserViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.setUserData(doctor);
        holder.itemView.setOnClickListener(event -> {
            Intent intent = new Intent(newChatActivity, ChatActivity.class);
            intent.putExtra("name", doctor.getName());
            intent.putExtra("receiverImage", doctor.getImage());
            intent.putExtra("uid", doctor.getUid());
            newChatActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        DoctorItemBinding binding;

        UserViewHolder(DoctorItemBinding doctorItemBinding) {
            super(doctorItemBinding.getRoot());
            binding = doctorItemBinding;
        }

        public void setUserData(Doctor doctor) {
            binding.doctorName.setText(doctor.name);
            binding.doctorProfession.setText(doctor.profession);
            binding.doctorImage.setImageBitmap(getDoctorImage(doctor.image));
        }
    }

    private Bitmap getDoctorImage(String image){
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
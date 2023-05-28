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

import com.example.telechat.activities.AppointmentsActivity;
import com.example.telechat.databinding.PatientItemBinding;
import com.example.telechat.models.User;

import java.util.ArrayList;

public class PatientSelectAdapter extends RecyclerView.Adapter<PatientSelectAdapter.PatientViewHolder> {
    Context selectPatientActivity;
    ArrayList<User> users;

    public PatientSelectAdapter(Context selectPatientActivity, ArrayList<User> users) {
        this.selectPatientActivity = selectPatientActivity;
        this.users = users;
    }

    @NonNull
    @Override
    public PatientSelectAdapter.PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PatientItemBinding patientItemBinding = PatientItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PatientViewHolder(patientItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientSelectAdapter.PatientViewHolder holder, int position) {
        User user = users.get(position);
        holder.setPatientData(user);
        holder.itemView.setOnClickListener(event -> {
            Intent intent = new Intent(selectPatientActivity, AppointmentsActivity.class);
            intent.putExtra("patientName", user.getName());
            intent.putExtra("patientUid", user.getUid());
            selectPatientActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder {
        PatientItemBinding binding;

        PatientViewHolder(PatientItemBinding patientItemBinding){
            super(patientItemBinding.getRoot());
            binding = patientItemBinding;
        }

        public void setPatientData(User user){
            binding.userName.setText(user.name);
            binding.userImage.setImageBitmap(getUserImage(user.image));
        }

        private Bitmap getUserImage(String image){
            byte[] bytes = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }
}

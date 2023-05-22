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

import com.example.telechat.activities.ChangeDoctorActivity;
import com.example.telechat.databinding.DoctorItemBinding;
import com.example.telechat.models.Doctor;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    Context doctorListActivity;
    ArrayList<Doctor> doctors;

    public DoctorAdapter(Context doctorListActivity, ArrayList<Doctor> doctors) {
        this.doctorListActivity = doctorListActivity;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DoctorItemBinding doctorItemBinding = DoctorItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DoctorViewHolder(doctorItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.setDoctorData(doctor);
        holder.itemView.setOnClickListener(event -> {
            Intent intent = new Intent(doctorListActivity, ChangeDoctorActivity.class);
            intent.putExtra("uid", doctor.getUid());
            doctorListActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    class DoctorViewHolder extends RecyclerView.ViewHolder{
        DoctorItemBinding binding;

        DoctorViewHolder(DoctorItemBinding doctorItemBinding){
            super(doctorItemBinding.getRoot());
            binding = doctorItemBinding;
        }

        public void setDoctorData(Doctor doctor) {
            binding.doctorName.setText(doctor.name);
            binding.doctorProfession.setText(doctor.profession);
            binding.doctorImage.setImageBitmap(getDoctorImage(doctor.image));
        }

        private Bitmap getDoctorImage(String image){
            byte[] bytes = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }
}
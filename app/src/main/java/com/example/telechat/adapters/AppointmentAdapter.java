package com.example.telechat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telechat.databinding.AppointmentItemBinding;
import com.example.telechat.models.Appointment;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    Context profileActivity;
    ArrayList<Appointment> appointments;

    public AppointmentAdapter(Context profileActivity, ArrayList<Appointment> appointments) {
        this.profileActivity = profileActivity;
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public AppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppointmentItemBinding appointmentItemBinding = AppointmentItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AppointmentViewHolder(appointmentItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.setPatientData(appointment);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        AppointmentItemBinding binding;

        AppointmentViewHolder(AppointmentItemBinding appointmentItemBinding){
            super(appointmentItemBinding.getRoot());
            binding = appointmentItemBinding;
        }

        public void setPatientData(Appointment appointment){
            binding.doctorName.setText(appointment.doctorName);
            binding.date.setText(appointment.datetime);
            binding.description.setText(appointment.description);
        }
    }
}

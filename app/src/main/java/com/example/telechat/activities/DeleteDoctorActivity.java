package com.example.telechat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.telechat.adapters.DoctorDeleteAdapter;
import com.example.telechat.databinding.ActivityDoctorListBinding;
import com.example.telechat.models.Doctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteDoctorActivity extends AppCompatActivity {
    ActivityDoctorListBinding binding;
    FirebaseDatabase database;
    ArrayList<Doctor> doctors;
    DoctorDeleteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initialize();
        getDoctors();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(event -> onBackPressed());
    }

    private void initialize() {
        database = FirebaseDatabase.getInstance();
        doctors = new ArrayList<>();
        adapter = new DoctorDeleteAdapter(DeleteDoctorActivity.this, doctors);
        binding.doctorsRecyclerView.setAdapter(adapter);
    }

    private void getDoctors() {
        loading(true);
        DatabaseReference reference = database.getReference().child("doctors");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Doctor doctor = dataSnapshot.getValue(Doctor.class);
                    doctors.add(doctor);
                }
                loading(false);
                adapter.notifyDataSetChanged();
                if(doctors.size() > 0){
                    binding.doctorsRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    showErrorMessage();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException());
            }
        });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "Нет доступных докторов"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
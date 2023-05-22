package com.example.telechat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.telechat.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {
    ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.createDoctor.setOnClickListener(event ->
                startActivity(new Intent(AdminActivity.this, CreateDoctorActivity.class)));
        binding.changeDoctor.setOnClickListener(event ->
                startActivity(new Intent(AdminActivity.this, DoctorListActivity.class)));
    }
}
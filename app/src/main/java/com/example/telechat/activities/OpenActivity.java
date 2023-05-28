package com.example.telechat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.telechat.databinding.ActivityOpenBinding;

public class OpenActivity extends AppCompatActivity {
    ActivityOpenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginUser.setOnClickListener(event -> startActivity(new Intent(OpenActivity.this, LoginUserActivity.class)));
        binding.loginWorker.setOnClickListener(event -> startActivity(new Intent(OpenActivity.this, LoginWorkerActivity.class)));
    }
}
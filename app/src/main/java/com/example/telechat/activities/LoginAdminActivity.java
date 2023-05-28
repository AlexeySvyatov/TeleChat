package com.example.telechat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.telechat.databinding.ActivityLoginAdminBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginAdminActivity extends AppCompatActivity {
    ActivityLoginAdminBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        setListeners();
    }

    private void setListeners() {
        binding.textWorkerLogin.setOnClickListener(event ->
                startActivity(new Intent(LoginAdminActivity.this, LoginWorkerActivity.class)));

        binding.loginButton.setOnClickListener(event -> {
            if(isValidSignInDetails()){
                String email = binding.inputEmail.getText().toString();
                String password = binding.inputPassword.getText().toString();
                signIn(email, password);
            }
        });
    }

    private void signIn(String email, String password){
        loading(true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        startActivity(new Intent(LoginAdminActivity.this, AdminActivity.class));
                    }else{
                        loading(false);
                        showToast("Не удалось войти в аккаунт");
                    }
                });
    }

    private Boolean isValidSignInDetails() {
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            binding.inputEmail.setError("Это поле не может быть пустым");
            showToast("Введите почту");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            binding.inputEmail.setError("Введите корректную почту");
            showToast("Введите корректную почту");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            binding.inputPassword.setError("Это поле не может быть пустым");
            showToast("Введите пароль");
            return false;
        }else{
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if(isLoading){
            binding.loginButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.loginButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
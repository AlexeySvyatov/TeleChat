package com.example.telechat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.telechat.adapters.PatientSelectAdapter;
import com.example.telechat.databinding.ActivityPatientListBinding;
import com.example.telechat.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectPatientActivity extends AppCompatActivity {
    ActivityPatientListBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    PatientSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initialize();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(event -> onBackPressed());
    }

    private void initialize(){
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        adapter = new PatientSelectAdapter(SelectPatientActivity.this, users);
        binding.usersRecyclerView.setAdapter(adapter);
    }

    private void getUsers() {
        loading(true);
        DatabaseReference reference = database.getReference().child("patients");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                }
                loading(false);
                adapter.notifyDataSetChanged();
                if(users.size() > 0){
                    binding.usersRecyclerView.setVisibility(View.VISIBLE);
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
        binding.textErrorMessage.setText(String.format("%s", "Нет доступных пациентов"));
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
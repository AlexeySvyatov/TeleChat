package com.example.telechat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.telechat.adapters.AppointmentAdapter;
import com.example.telechat.databinding.ActivityProfileBinding;
import com.example.telechat.models.Appointment;
import com.example.telechat.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    AppointmentAdapter adapter;
    ArrayList<Appointment> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
        setListeners();

        DatabaseReference reference = database.getReference().child("patients").child(auth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                binding.userName.setText(user.name);
                binding.userEmail.setText(user.email);
                binding.userDate.setText(user.date);
                byte[] bytes = Base64.decode(user.image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.profileImage.setImageBitmap(bitmap);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException());
            }
        });
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(event -> onBackPressed());
        binding.changeData.setOnClickListener(event -> startActivity(new Intent(ProfileActivity.this, ChangeProfileActivity.class)));
        binding.myAppointments.setOnClickListener(event -> {
            getAppointments();
            binding.frameLayout.setVisibility(View.VISIBLE);

        });
    }

    private void initialize() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        appointments = new ArrayList<>();
        adapter = new AppointmentAdapter(ProfileActivity.this, appointments);
    }

    private void getAppointments() {
        loading(true);
        DatabaseReference reference = database.getReference().child("appointments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    appointments.add(appointment);
                }
                loading(false);
                adapter.notifyDataSetChanged();
                if(appointments.size() > 0){
                    binding.recyclerView.setVisibility(View.VISIBLE);
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

    private void loading(Boolean isLoading) {
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showErrorMessage() {
        binding.errorMessage.setText(String.format("%s", "Нет доступных записей"));
        binding.errorMessage.setVisibility(View.VISIBLE);
    }
}
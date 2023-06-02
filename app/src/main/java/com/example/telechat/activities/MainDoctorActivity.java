package com.example.telechat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.telechat.adapters.ConversationWorkerAdapter;
import com.example.telechat.databinding.ActivityMainDoctorBinding;
import com.example.telechat.models.Conversation;
import com.example.telechat.models.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainDoctorActivity extends AppCompatActivity {
    ActivityMainDoctorBinding binding;
    ConversationWorkerAdapter conversationWorkerAdapter;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<Conversation> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
        setListeners();
        loadDetails();

        DatabaseReference reference = database.getReference().child("conversations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Conversation conversation = dataSnapshot.getValue(Conversation.class);
                    conversations.add(conversation);
                }
                conversationWorkerAdapter.notifyDataSetChanged();
                binding.conversationsRecyclerView.smoothScrollToPosition(0);
                binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException());
            }
        });
    }

    private void loadDetails() {
        DatabaseReference reference = database.getReference().child("doctors").child(auth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Doctor doctor = snapshot.getValue(Doctor.class);
                binding.textName.setText(doctor.name);
                byte[] bytes = Base64.decode(doctor.image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.imageProfile.setImageBitmap(bitmap);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException());
            }
        });
    }

    private void initialize() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        conversations = new ArrayList<>();
        conversationWorkerAdapter = new ConversationWorkerAdapter(MainDoctorActivity.this, conversations);
        binding.conversationsRecyclerView.setAdapter(conversationWorkerAdapter);
    }

    private void setListeners() {
        binding.logOut.setOnClickListener(event -> {
            showToast("Выход из аккаунта...");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainDoctorActivity.this, LoginWorkerActivity.class));
        });
        binding.newAppointmentButton.setOnClickListener(event -> {
            Intent intent = new Intent(MainDoctorActivity.this, AppointmentsActivity.class);
            intent.putExtra("doctorName", binding.textName.getText().toString());
            intent.putExtra("doctorUid", auth.getCurrentUser().getUid());
            startActivity(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
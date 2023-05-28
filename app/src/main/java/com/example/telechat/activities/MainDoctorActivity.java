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
    String userName;
    String userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initialize();
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
        userName = getIntent().getStringExtra("name");
        userImage = getIntent().getStringExtra("image");

        byte[] bytes = Base64.decode(userImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        binding.textName.setText(userName);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void initialize() {
        database = FirebaseDatabase.getInstance();
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
            intent.putExtra("doctorName", userName);
            intent.putExtra("doctorUid", auth.getCurrentUser().getUid());
            startActivity(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
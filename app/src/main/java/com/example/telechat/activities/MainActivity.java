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

import com.example.telechat.adapters.ConversationAdapter;
import com.example.telechat.databinding.ActivityMainBinding;
import com.example.telechat.models.Conversation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ConversationAdapter conversationAdapter;
    FirebaseDatabase database;
    ArrayList<Conversation> conversations;
    String userName;
    String userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initialize();
        loadDetails();

        DatabaseReference conReference = database.getReference().child("conversations");
        conReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Conversation conversation = dataSnapshot.getValue(Conversation.class);
                    conversations.add(conversation);
                }
                conversationAdapter.notifyDataSetChanged();
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

    private void setListeners() {
        binding.logOut.setOnClickListener(event -> {
            showToast("Signing out...");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
        });
        binding.imageProfile.setOnClickListener(event ->
                startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        binding.newChatButton.setOnClickListener(event ->
                startActivity(new Intent(MainActivity.this, UserActivity.class)));
    }

    private void initialize() {
        database = FirebaseDatabase.getInstance();
        conversations = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(MainActivity.this, conversations);
        binding.conversationsRecyclerView.setAdapter(conversationAdapter);
    }

    private void loadDetails() {
        userName = getIntent().getStringExtra("name");
        userImage = getIntent().getStringExtra("image");

        byte[] bytes = Base64.decode(userImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        binding.textName.setText(userName);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
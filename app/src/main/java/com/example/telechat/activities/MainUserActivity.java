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

import com.example.telechat.adapters.ConversationUserAdapter;
import com.example.telechat.databinding.ActivityMainUserBinding;
import com.example.telechat.models.Conversation;
import com.example.telechat.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainUserActivity extends AppCompatActivity {
    ActivityMainUserBinding binding;
    ConversationUserAdapter conversationUserAdapter;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<Conversation> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainUserBinding.inflate(getLayoutInflater());
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
                conversationUserAdapter.notifyDataSetChanged();
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
            showToast("Выход из аккаунта...");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainUserActivity.this, RegistrationActivity.class));
        });
        binding.imageProfile.setOnClickListener(event ->
                startActivity(new Intent(MainUserActivity.this, ProfileActivity.class)));
        binding.newChatButton.setOnClickListener(event ->
                startActivity(new Intent(MainUserActivity.this, NewChatActivity.class)));
    }

    private void initialize() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        conversations = new ArrayList<>();
        conversationUserAdapter = new ConversationUserAdapter(MainUserActivity.this, conversations);
        binding.conversationsRecyclerView.setAdapter(conversationUserAdapter);
    }

    private void loadDetails() {
        DatabaseReference reference = database.getReference().child("patients").child(auth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                binding.textName.setText(user.name);
                byte[] bytes = Base64.decode(user.image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.imageProfile.setImageBitmap(bitmap);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
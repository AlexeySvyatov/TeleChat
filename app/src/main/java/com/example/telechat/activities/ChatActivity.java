package com.example.telechat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.telechat.adapters.MessageAdapter;
import com.example.telechat.databinding.ActivityChatBinding;
import com.example.telechat.models.Conversation;
import com.example.telechat.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String receiverImage;
    String receiverName;
    String receiverUId;
    String senderUId;
    FirebaseDatabase database;
    FirebaseAuth auth;
    MessageAdapter messageAdapter;
    ArrayList<Message> messages;
    String conversationsId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initialize();
        loadDetails();

        DatabaseReference chatReference = database.getReference().child("chats").child("messages");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messages.add(message);
                }
                if(messages.size() == 0){
                    messageAdapter.notifyDataSetChanged();
                }else{
                    messageAdapter.notifyItemRangeInserted(messages.size(), messages.size());
                    binding.chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
                    binding.chatRecyclerView.setVisibility(View.VISIBLE);
                }
                binding.progressBar.setVisibility(View.GONE);
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
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(ChatActivity.this, messages);
        binding.chatRecyclerView.setAdapter(messageAdapter);
    }

    private void setListeners() {
        binding.layoutSend.setOnClickListener(event -> sendMessage());
        binding.imageBack.setOnClickListener(event -> onBackPressed());
    }

    private void sendMessage(){
        String message = binding.inputMessage.getText().toString();
        if(message.isEmpty()){
            showToast("Пожалуйста, введите сообщение");
            return;
        }
        Date date = new Date();
        Message messages = new Message(senderUId, receiverUId, message, getReadableDateTime(date));
        database.getReference().child("chats").child("messages").push().setValue(messages);
        if(conversationsId != null){
            updateConversation(message, getReadableDateTime(date));
        }else{
            addConversation(senderUId, receiverUId, receiverName, message, getReadableDateTime(date), receiverImage);
        }
        binding.inputMessage.setText("");
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void loadDetails(){
        receiverName = getIntent().getStringExtra("name");
        receiverImage = getIntent().getStringExtra("receiverImage");
        receiverUId = getIntent().getStringExtra("uid");

        byte[] bytes = Base64.decode(receiverImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        binding.receiverName.setText(receiverName);
        binding.receiverImage.setImageBitmap(bitmap);

        senderUId = auth.getCurrentUser().getUid();
    }

    private void addConversation(String senderID, String receiverID, String receiverName, String message, String date, String image){
        DatabaseReference reference = database.getReference().child("conversations");
        conversationsId = reference.push().getKey();
        Conversation conversations = new Conversation(conversationsId, senderID, receiverID, receiverName, message, date, image);
        reference.push().setValue(conversations);
    }

    private void updateConversation(String message, String date){
        DatabaseReference reference = database.getReference().child("conversations");
        conversationsId = reference.push().getKey();
        reference.child(conversationsId).child("message").setValue(message);
        reference.child(conversationsId).child("date").setValue(date);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
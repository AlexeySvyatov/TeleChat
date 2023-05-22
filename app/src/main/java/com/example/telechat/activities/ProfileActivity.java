package com.example.telechat.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.telechat.databinding.ActivityProfileBinding;
import com.example.telechat.models.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser user;
    AuthCredential credential;
    ArrayList<User> users;
    String image;
    String oldEmail;
    String oldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
        setListeners();

        DatabaseReference reference = database.getReference().child("patients").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                    oldEmail = dataSnapshot.child(auth.getUid()).child("email").getValue(String.class);
                    oldPassword = dataSnapshot.child(auth.getUid()).child("password").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toException());
            }
        });

        binding.inputName.setText(String.valueOf(users.get(0)));
        binding.inputEmail.setText(String.valueOf(users.get(1)));
        binding.inputDate.setText(String.valueOf(users.get(2)));
        binding.inputStatus.setText(String.valueOf(users.get(3)));
        byte[] bytes = Base64.decode(String.valueOf(users.get(4)), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.profileImage.setImageBitmap(bitmap);
    }

    private void setListeners() {
        binding.textBack.setOnClickListener(event -> onBackPressed());
        binding.inputDate.setOnClickListener(event -> pickDate());
        binding.profileImage.setOnClickListener(event -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });
        binding.saveChangesButton.setOnClickListener(event -> {
            if(isValidDataDetails()){
                String name = binding.inputName.getText().toString();
                String email = binding.inputEmail.getText().toString();
                String status = binding.inputStatus.getText().toString();
                String date = binding.inputDate.getText().toString();
                saveChanges(name, email, status, date);
            }
        });
    }

    private void saveChanges(String name, String email, String status, String date) {
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                user.updateEmail(email);
            }
        });

        loading(true);
        HashMap<String, Object> newData = new HashMap<>();
        newData.put("name", name);
        newData.put("email", email);
        newData.put("status", status);
        newData.put("date", date);
        DatabaseReference reference = database.getReference().child("patients").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    dataSnapshot.getRef().updateChildren(newData);
                }
                loading(false);
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
        users = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        credential = EmailAuthProvider.getCredential(oldEmail, oldPassword);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.profileImage.setImageBitmap(bitmap);
                            image = encodeImage(bitmap);
                        }catch (FileNotFoundException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            });

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void pickDate(){
        binding.inputDate.setVisibility(View.GONE);
        binding.birthDate.setVisibility(View.VISIBLE);
        binding.saveDate.setVisibility(View.VISIBLE);
        binding.saveDate.setOnClickListener(save -> {
            int year = binding.birthDate.getYear();
            int month = binding.birthDate.getMonth();
            int day = binding.birthDate.getDayOfMonth();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(calendar.getTime());
            binding.inputDate.setText(date);
            binding.inputDate.setVisibility(View.VISIBLE);
            binding.birthDate.setVisibility(View.GONE);
            binding.saveDate.setVisibility(View.GONE);
        });
    }

    private Boolean isValidDataDetails() {
        if (image == null) {
            showToast("Select profile image");
            return false;
        }else if (binding.inputName.getText().toString().trim().isEmpty()) {
            binding.inputName.setError("This field can not be blank");
            showToast("Enter name");
            return false;
        }else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            binding.inputEmail.setError("This field can not be blank");
            showToast("Enter email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            binding.inputEmail.setError("Enter correct email");
            showToast("Enter valid email");
            return false;
        }else if (binding.inputDate.getText().equals("")) {
            binding.inputDate.setError("This field can not be blank");
            showToast("Enter birth date");
            return false;
        }else if (binding.inputStatus.getText().toString().trim().isEmpty()) {
            binding.inputStatus.setError("This field can not be blank");
            showToast("Enter status");
            return false;
        }else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if(isLoading) {
            binding.saveChangesButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.saveChangesButton.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
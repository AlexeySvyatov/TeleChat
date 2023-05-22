package com.example.telechat.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.example.telechat.databinding.ActivityRegistrationBinding;
import com.example.telechat.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initialize();
    }

    private void initialize() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    private void setListeners() {
        binding.textSignIn.setOnClickListener(event ->
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));
        binding.signUpButton.setOnClickListener(event -> {
            if(isValidSignUpDetails()){
                String name = binding.inputName.getText().toString();
                String email = binding.inputEmail.getText().toString();
                String password = binding.inputPassword.getText().toString();
                String date = binding.inputDate.getText().toString();
                String status = "Hey, i`m new one here";
                signUp(name, email, status, password, date);
            }
        });
        binding.inputDate.setOnClickListener(event -> pickDate());
        binding.profileImage.setOnClickListener(event -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });
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

    private void signUp(String name, String email, String status, String password, String date){
        loading(true);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DatabaseReference reference = database.getReference().child("patients").child(auth.getUid());
                        User user = new User(auth.getUid(), name, email, status, image, date, password);
                        reference.setValue(user).addOnCompleteListener(users -> {
                            if(users.isSuccessful()){
                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("image", image);
                                startActivity(intent);
                            }else{
                                loading(false);
                                showToast("Registration error");
                            }
                        });
                    }else{
                        loading(false);
                        showToast("Something went wrong");
                    }
                });
    }

    private Boolean isValidSignUpDetails() {
        if (image == null) {
            showToast("Select profile image");
            return false;
        } else if (binding.inputName.getText().toString().trim().isEmpty()) {
            binding.inputName.setError("This field can not be blank");
            showToast("Enter name");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            binding.inputEmail.setError("This field can not be blank");
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            binding.inputEmail.setError("Enter correct email");
            showToast("Enter valid email");
            return false;
        } else if (binding.inputDate.getText().equals("")) {
            binding.inputDate.setError("This field can not be blank");
            showToast("Enter birth date");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            binding.inputPassword.setError("This field can not be blank");
            showToast("Enter password");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().length() < 6) {
            binding.inputPassword.setError("Enter more than 6 characters");
            showToast("Password should be more than 6 symbols");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if(isLoading) {
            binding.signUpButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.signUpButton.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
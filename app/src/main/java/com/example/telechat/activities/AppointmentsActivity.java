package com.example.telechat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.telechat.databinding.ActivityAppointmentsBinding;
import com.example.telechat.models.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AppointmentsActivity extends AppCompatActivity {
    ActivityAppointmentsBinding binding;
    FirebaseDatabase database;
    String doctorName;
    String doctorUid;
    String patientName;
    String patientUid;
    String appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initialize();

        binding.selectDoctor.setText(doctorName);
        binding.selectPatient.setText(patientName);
        if(!(binding.selectDoctor.getText().toString().isEmpty() && binding.selectPatient.getText().toString().isEmpty() &&
                binding.inputDate.getText().toString().isEmpty() && binding.inputDescription.getText().toString().isEmpty())){
            saveAppointment(doctorUid, patientUid, doctorName, binding.inputDate.getText().toString(), binding.inputDescription.getText().toString());
        }
    }

    private void saveAppointment(String doctor, String name, String patient, String date, String description) {
        DatabaseReference reference = database.getReference().child("appointments");
        appointmentId = reference.push().getKey();
        Appointment appointment = new Appointment(appointmentId, doctor, name, patient, date, description);
        reference.setValue(appointment).addOnCompleteListener(complete -> {
            if(complete.isSuccessful()){
                startActivity(new Intent(AppointmentsActivity.this, MainDoctorActivity.class));
            }
        });
    }

    private void initialize() {
        doctorName = getIntent().getStringExtra("doctorName");
        doctorUid = getIntent().getStringExtra("doctorUid");
        patientName = getIntent().getStringExtra("patientName");
        patientUid = getIntent().getStringExtra("patientUid");
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(event -> startActivity(new Intent(AppointmentsActivity.this, MainDoctorActivity.class)));
        binding.selectPatient.setOnClickListener(event -> startActivity(new Intent(AppointmentsActivity.this, SelectPatientActivity.class)));
    }
}
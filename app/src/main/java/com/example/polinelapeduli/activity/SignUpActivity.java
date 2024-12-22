package com.example.polinelapeduli.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.R;
import com.example.polinelapeduli.model.User;
import com.example.polinelapeduli.repository.UserRepository;
import com.example.polinelapeduli.utils.Enum.ELoginMethod;
import com.example.polinelapeduli.utils.Enum.ERole;
import com.example.polinelapeduli.utils.InputValidator;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        userRepository = new UserRepository(this);

        initializeFirebase();
        initializeUI();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initializeUI() {
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        Button btnSignUp = findViewById(R.id.btn_sign_up);
        TextView tvAlreadyHaveAccount = findViewById(R.id.tv_already_have_account);

        btnSignUp.setOnClickListener(v -> registerUser());
        tvAlreadyHaveAccount.setOnClickListener(v -> navigateToSignIn());
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        String fullName = InputValidator.getValidatedText(etFullName, "Full name is required");
        if (fullName == null) return;

        String email = InputValidator.getValidatedEmail(etEmail);
        if (email == null) return;

        if (!InputValidator.validatePassword(etPassword)) return;
        if (!InputValidator.validateConfirmPassword(etConfirmPassword, etPassword)) return;

        String password = etPassword.getText().toString().trim();
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        User user = userRepository.getUserByEmail(email);

        if (user != null) {
            if (user.isActive()) {
                showToast("Email is already registered.");
            } else {
                showToast("Email is already registered but it's not active.");
            }
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulRegistration(fullName, email, createdAt);
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        showToast("Registration failed: " + errorMessage);
                        Log.e("SignUpActivity", "Registration failed: " + errorMessage);
                    }
                });
    }

    private void handleSuccessfulRegistration(String fullName, String email, String createdAt) {
        User user = createUser(fullName, email, createdAt);
        if (userRepository.insertUser(user)) {
            Log.i("SignUpActivity", "User inserted into database successfully");
        } else {
            Log.w("SignUpActivity", "Failed to insert user into database");
        }
        showToast("Registration successful");
        navigateToSignIn();
    }

    private User createUser(String fullName, String email, String createdAt) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setLoginMethod(ELoginMethod.EMAIL);
        user.setRole(ERole.USER);
        user.setProfilePicture("");
        user.setActive(true);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt("");
        return user;
    }

    private void showToast(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}

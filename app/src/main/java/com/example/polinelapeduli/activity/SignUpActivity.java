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
import com.example.polinelapeduli.utils.CurrentTime;
import com.example.polinelapeduli.utils.Enum.ELoginMethod;
import com.example.polinelapeduli.utils.Enum.ERole;
import com.example.polinelapeduli.utils.InputValidator;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpActivity extends AppCompatActivity {

    // Firebase Authentication
    private FirebaseAuth mAuth;

    // UI Components
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

    // Initialize Firebase Authentication
    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Set up UI components and event listeners
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

    // Navigate to SignInActivity
    private void navigateToSignIn() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    // Register a new user
    private void registerUser() {
        String fullName = InputValidator.getValidatedText(etFullName, "Full name is required");
        if (fullName == null) return;

        String email = InputValidator.getValidatedEmail(etEmail);
        if (email == null) return;

        if (InputValidator.validatePassword(etPassword)) return;

        if (!InputValidator.validateConfirmPassword(etConfirmPassword, etPassword)) return;

        String password = etPassword.getText().toString().trim();

        // Check if email is already registered
        User existingUser = userRepository.getUserByEmail(email);
        if (existingUser != null) {
            handleExistingUser(existingUser);
            return;
        }

        // Firebase registration
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulRegistration(fullName, email, CurrentTime.getCurrentTime());
                    } else {
                        handleRegistrationFailure(task.getException());
                    }
                });
    }

    // Handle existing user scenarios
    private void handleExistingUser(User existingUser) {
        String loginMethod = existingUser.getLoginMethod() != null ? existingUser.getLoginMethod().toString() : "";
        boolean isActive = existingUser.isActive();
        if ("EMAIL".equalsIgnoreCase(loginMethod) && isActive) {
            showToast("Email is already registered.");
        } else if ("GOOGLE".equalsIgnoreCase(loginMethod) && isActive) {
            showToast("Email is already registered with Google.");
        }
    }

    // Handle successful registration and input data to SQLite local
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

    // Handle registration failure
    private void handleRegistrationFailure(Exception exception) {
        String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
        showToast("Registration failed: " + errorMessage);
        Log.e("SignUpActivity", "Registration failed: " + errorMessage);
    }

    // Create a new user object
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

    // Display a toast message
    private void showToast(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}

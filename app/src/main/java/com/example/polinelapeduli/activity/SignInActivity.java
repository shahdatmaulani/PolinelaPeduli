package com.example.polinelapeduli.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.R;
import com.example.polinelapeduli.model.User;
import com.example.polinelapeduli.repository.UserRepository;
import com.example.polinelapeduli.utils.CurrentTime;
import com.example.polinelapeduli.utils.Enum.ELoginMethod;
import com.example.polinelapeduli.utils.Enum.ERole;
import com.example.polinelapeduli.utils.constants.Constants;
import com.example.polinelapeduli.utils.InputValidator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;


public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private EditText emailField, passwordField;
    private UserRepository userRepository;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.getData()));
                } else {
                    showToast("Google sign-in canceled");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        userRepository = new UserRepository(this);
        initializeFirebaseAuth();
        configureGoogleSignIn();
        initializeViews();
    }

    private void initializeFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initializeViews() {
        emailField = findViewById(R.id.et_email);
        passwordField = findViewById(R.id.et_password);
        Button signInButton = findViewById(R.id.btn_sign_in);
        Button googleSignInButton = findViewById(R.id.btn_sign_in_google);
        TextView forgotPasswordText = findViewById(R.id.tv_forgot_password);
        TextView signUpText = findViewById(R.id.tv_signup);
        CheckBox rememberMeCheckBox = findViewById(R.id.cb_remember_me);

        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String savedEmail = prefs.getString(Constants.KEY_USER_EMAIL, "");
        boolean isRemembered = prefs.getBoolean(Constants.KEY_REMEMBER_ME, false);

        if (isRemembered) {
            emailField.setText(savedEmail);
            rememberMeCheckBox.setChecked(true);
        }

        rememberMeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.KEY_REMEMBER_ME, isChecked);
            if (isChecked) {
                editor.putString(Constants.KEY_USER_EMAIL, emailField.getText().toString().trim());
            } else {
                editor.remove(Constants.KEY_USER_EMAIL);
            }
            editor.apply();
        });

        signInButton.setOnClickListener(v -> signInWithEmail());
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        forgotPasswordText.setOnClickListener(v -> navigateToActivity(ForgotPasswordActivity.class));
        signUpText.setOnClickListener(v -> navigateToActivity(SignUpActivity.class));
    }

    private void signInWithGoogle() {
        // Reset the sign-in session
        googleSignInClient.signOut().addOnCompleteListener(task -> googleSignInClient.revokeAccess().addOnCompleteListener(revokeTask -> {
            // Launch Google Sign-In intent
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        }));
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            User existingUser = userRepository.getUserByEmail(account.getEmail());
            if (existingUser != null) {
                String loginMethod = existingUser.getLoginMethod() != null ? existingUser.getLoginMethod().toString() : "";
                if ("EMAIL".equalsIgnoreCase(loginMethod) && existingUser.isActive()) {
                    showToast("Email is already registered with EMAIL");
                    //Reset Google Sign-In on failure
                    resetGoogleSignIn();
                } else if("GOOGLE".equalsIgnoreCase(loginMethod) && existingUser.isActive()) {
                    authenticateWithFirebase(account);
                } else {
                    showToast("Account is not active");
                }
            } else {
                authenticateWithFirebase(account);
                createUserInDatabase(account);
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign-in failed", e);
            showToast("Google sign-in failed");
            // Reset Google Sign-In on failure
            resetGoogleSignIn();
        }
    }

    private void authenticateWithFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                showToast("Login with Google successful");
                navigateToHome();
            } else {
                handleFirebaseAuthException(task.getException());
            }
        });
    }

    private void resetGoogleSignIn() {
        googleSignInClient.signOut().addOnCompleteListener(task ->
                googleSignInClient.revokeAccess().addOnCompleteListener(revokeTask ->
                        Log.i(TAG, "Google Sign-In reset completed")
                )
        );
    }

    private void createUserInDatabase(GoogleSignInAccount account) {
        User newUser = new User();
        newUser.setFullName(account.getDisplayName());
        newUser.setEmail(account.getEmail());
        newUser.setLoginMethod(ELoginMethod.GOOGLE);
        newUser.setRole(Objects.equals(account.getEmail(), "adminpolinelapeduli@gmail.com") ? ERole.ADMIN : ERole.USER);
        newUser.setProfilePicture(account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null);
        newUser.setActive(true);
        newUser.setCreatedAt(CurrentTime.getCurrentTime());

        if (userRepository.insertUser(newUser)) {
            Log.i(TAG, "User inserted into database successfully");
        } else {
            Log.w(TAG, "Failed to insert user into database");
        }
    }

    private void signInWithEmail() {
        String email = InputValidator.getValidatedEmail(emailField);
        if (email == null || InputValidator.validatePassword(passwordField)) return;
        String password = passwordField.getText().toString().trim();

        User existingUser = userRepository.getUserByEmail(email);
        if (existingUser != null) {
            String loginMethod = existingUser.getLoginMethod() != null ? existingUser.getLoginMethod().toString() : "";
            boolean isActive = existingUser.isActive();

            if ("EMAIL".equalsIgnoreCase(loginMethod) && isActive) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveUserEmailToPreferences(email);
                        showToast("Login successful");
                        navigateToHome();
                    } else {
                        handleFirebaseAuthException(task.getException());
                    }
                });
            } else if ("GOOGLE".equalsIgnoreCase(loginMethod) && isActive) {
                showToast("Email is already registered with Google");
            } else {
                showToast("Account is not active");
            }
        } else {
            showToast("Email not registered");
        }
    }

    private void handleFirebaseAuthException(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            showToast("Invalid email or password");
        } else {
            showToast("Authentication failed: " + exception.getMessage());
        }
    }

    private void saveUserEmailToPreferences(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.apply();
    }

    private void navigateToHome() {
        navigateToActivity(HomeActivity.class);
        finish();
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

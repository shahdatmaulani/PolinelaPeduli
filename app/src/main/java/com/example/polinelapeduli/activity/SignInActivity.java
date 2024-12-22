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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
            if (!isChecked) {
                editor.remove(Constants.KEY_USER_EMAIL);
            } else {
                editor.putString(Constants.KEY_USER_EMAIL, emailField.getText().toString().trim());
            }
            editor.apply();
        });

        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        signInButton.setOnClickListener(v -> signInWithEmail());
        forgotPasswordText.setOnClickListener(v -> navigateToActivity(ForgotPasswordActivity.class));
        signUpText.setOnClickListener(v -> navigateToActivity(SignUpActivity.class));
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                authenticateWithFirebase(account);
            } else {
                showToast("Google sign-in failed: No account found");
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign-in failed", e);
            showToast("Google sign-in failed: " + e.getMessage());
        }
    }

    private void authenticateWithFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                createUserInDatabase(account);
                saveUserEmailToPreferences(account.getEmail());
                showToast("Login with Google successful");
                navigateToHome();
            } else {
                showToast("Authentication failed");
            }
        });
    }

    private void createUserInDatabase(GoogleSignInAccount account) {
        if (account == null) {
            Log.w(TAG, "GoogleSignInAccount is null");
            showToast("Failed to sign in. Please try again.");
            return;
        }

        String email = account.getEmail();
        if (email == null || email.isEmpty()) {
            showToast("Email is missing from Google account. Please check your account settings.");
            return;
        }

        User existingUser = userRepository.getUserByEmail(email);
        if (existingUser != null) {
            if (!"GOOGLE".equalsIgnoreCase(String.valueOf(existingUser.getLoginMethod()))) {
                showToast("Email is already registered. Please use Sign In with " + existingUser.getLoginMethod() + ".");
                return;
            }
            showToast("Welcome back! Logging you in...");
            return;
        }

        User newUser = new User();
        newUser.setFullName(account.getDisplayName());
        newUser.setEmail(email);
        newUser.setLoginMethod(ELoginMethod.GOOGLE);
        newUser.setRole(email.equals("adminpolinelapeduli@gmail.com") ? ERole.ADMIN : ERole.USER);
        newUser.setProfilePicture(account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null);
        newUser.setActive(true);
        newUser.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        if (userRepository.insertUser(newUser)) {
            showToast("Account created successfully! Welcome, " + newUser.getFullName() + ".");
        } else {
            showToast("Failed to create account. Please try again.");
        }
    }

    private void signInWithEmail() {
        String email = InputValidator.getValidatedEmail(emailField);
        if (email == null) return;
        if (!InputValidator.validatePassword(passwordField)) return;

        User user = userRepository.getUserByEmail(email);
        if (user != null && !"EMAIL".equalsIgnoreCase(String.valueOf(user.getLoginMethod()))) {
            showToast("Email is already registered. Please use Sign In with " + user.getLoginMethod() + ".");
            return;
        } else {
            showToast("Email not found");
        }

        String password = passwordField.getText().toString().trim();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                saveUserEmailToPreferences(email);
                showToast("Login successful");
                navigateToHome();
            } else {
                handleFirebaseAuthException(task.getException());
            }
        });
    }

    private void handleFirebaseAuthException(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            showToast("Email not found");
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
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

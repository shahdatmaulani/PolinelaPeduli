package com.example.polinelapeduli.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.R;
import com.example.polinelapeduli.model.User;
import com.example.polinelapeduli.repository.UserRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private UserRepository userRepository;
    private String role;
    private TextView headerWelcome;
    private EditText searchField;
    private BottomNavigationView bottomNavigationView;
    private FirebaseUser firebaseUser;
    private LinearLayout kategoriBencana, kategoriPendidikan, kategoriKesehatan, kategoriKemanusiaan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inisialisasi Firebase Auth dan UserRepository
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository(this);
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            redirectToLogin();
            return;
        }

        // Ambil data pengguna berdasarkan email
        User userLogin = userRepository.getUserByEmail(firebaseUser.getEmail());

        // Pastikan data pengguna ditemukan dan pengguna aktif
        if (userLogin == null || !userLogin.isActive()) {
            redirectToSignIn();
            return;
        }

        // View Initialization
        headerWelcome = findViewById(R.id.headerWelcome);
        searchField = findViewById(R.id.searchField);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        kategoriBencana = findViewById(R.id.kategoriBencana);
        kategoriPendidikan = findViewById(R.id.kategoriPendidikan);
        kategoriKesehatan = findViewById(R.id.kategoriKesehatan);
        kategoriKemanusiaan = findViewById(R.id.kategoriKemanusiaan);

        // Setup
        setupUserDetails();
        setupSearchField();
        setupCategoryListeners();
        setupBottomNavigation();
    }

    private void redirectToSignIn() {
        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupUserDetails() {
        String email = firebaseUser.getEmail();
        User user = userRepository.getUserByEmail(email);

        if (user != null) {
            role = user.getRole() != null ? String.valueOf(user.getRole()) : "USER";
            String fullName = user.getFullName() != null ? user.getFullName() : "User";
            setupBottomNavigationMenu(role);
            setHeaderWelcome(fullName);
        } else {
            redirectToLogin();
        }
    }

    private void setupBottomNavigationMenu(String role) {
        bottomNavigationView.getMenu().clear(); // Clear existing menu
        if ("ADMIN".equals(role)) {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_admin);
        } else {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_user);
        }
        // Set label visibility mode
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_SELECTED);
    }



    private void setHeaderWelcome(String fullName) {
        headerWelcome.setText(String.format("Welcome%s!", fullName != null ? ", " + fullName : ""));
    }

    private void redirectToLogin() {
        Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    private void setupSearchField() {
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Handle search functionality here
            }
        });
    }

    private void setupCategoryListeners() {
        kategoriBencana.setOnClickListener(v -> openCategoryActivity(BencanaActivity.class));
        kategoriPendidikan.setOnClickListener(v -> openCategoryActivity(PendidikanActivity.class));
        kategoriKesehatan.setOnClickListener(v -> openCategoryActivity(KesehatanActivity.class));
        kategoriKemanusiaan.setOnClickListener(v -> openCategoryActivity(KemanusiaanActivity.class));
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_SELECTED);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }


    private void openCategoryActivity(Class<?> activityClass) {
        Intent intent = new Intent(HomeActivity.this, activityClass);
        startActivity(intent);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        int itemId = item.getItemId();

        if (itemId == R.id.home) {
            return true;
        } else if (itemId == R.id.tambah_donasi && "ADMIN".equals(role)) {
            intent = new Intent(this, TambahDonasiActivity.class);
        } else if (itemId == R.id.laporan_donasi && "ADMIN".equals(role)) {
            intent = new Intent(this, LaporanDonasiActivity.class);
        } else if (itemId == R.id.riwayat_transaksi && "USER".equals(role)) {
            intent = new Intent(this, RiwayatTransaksiActivity.class);
        } else if (itemId == R.id.profile) {
            intent = new Intent(this, ProfileActivity.class);
        } else {
            return false;
        }

        startActivity(intent);
        return true;
    }
}

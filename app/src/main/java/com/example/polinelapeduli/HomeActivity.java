package com.example.polinelapeduli;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.utils.UserUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private TextView headerWelcome;
    private EditText searchField;
    private LinearLayout kategoriBencana, kategoriPendidikan, kategoriKesehatan, kategoriKemanusiaan;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inisialisasi view
        headerWelcome = findViewById(R.id.headerWelcome);
        searchField = findViewById(R.id.searchField);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        kategoriBencana = findViewById(R.id.kategoriBencana);
        kategoriPendidikan = findViewById(R.id.kategoriPendidikan);
        kategoriKesehatan = findViewById(R.id.kategoriKesehatan);
        kategoriKemanusiaan = findViewById(R.id.kategoriKemanusiaan);

        // Dapatkan peran pengguna dari SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = preferences.getString("role", "user");

        // Atur Bottom Navigation berdasarkan peran
        if (role.equals("admin")) {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_admin);
        } else {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_user);
        }

        // Mengambil dan menampilkan nama pengguna
        UserUtils.getCurrentFullName(new UserUtils.OnFullNameReceivedListener() {
            @Override
            public void onFullNameReceived(String fullName) {
                if (fullName != null) {
                    headerWelcome.setText("Welcome, " + fullName + "!");
                } else {
                    headerWelcome.setText("Welcome!");
                }
            }
        });

        // Aksi saat kategori dipilih
        kategoriBencana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BencanaActivity.class);
                startActivity(intent);
            }
        });

        kategoriPendidikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PendidikanActivity.class);
                startActivity(intent);
            }
        });

        kategoriKesehatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, KesehatanActivity.class);
                startActivity(intent);
            }
        });

        kategoriKemanusiaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, KemanusiaanActivity.class);
                startActivity(intent);
            }
        });

        // Aksi saat tombol di bottom navigation dipilih
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    return true; // Sudah di halaman Home
                case R.id.tambah_donasi:
                    if (role.equals("admin")) {
                        startActivity(new Intent(HomeActivity.this, TambahDonasiActivity.class));
                    }
                    return true;
                case R.id.laporan_donasi:
                    if (role.equals("admin")) {
                        startActivity(new Intent(HomeActivity.this, LaporanDonasiActivity.class));
                    }
                    return true;
                case R.id.riwayat_transaksi:
                    if (role.equals("user")) {
                        startActivity(new Intent(HomeActivity.this, RiwayatTransaksiActivity.class));
                    }
                    return true;
                case R.id.profile:
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
            }
            return false;
        });
    }
}

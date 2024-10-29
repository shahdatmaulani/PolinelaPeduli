package com.example.polinelapeduli;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.utils.UserUtils;

public class HomeActivity extends AppCompatActivity {

    private TextView headerWelcome;
    private EditText searchField;
    private ImageView searchIcon, bannerImage;
    private LinearLayout kategoriBencana, kategoriPendidikan, kategoriKesehatan, kategoriKemanusiaan;
    private LinearLayout homeButton, campaignsButton, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inisialisasi view
        headerWelcome = findViewById(R.id.headerWelcome);
        searchField = findViewById(R.id.searchField);
        searchIcon = findViewById(R.id.searchIcon);
        bannerImage = findViewById(R.id.bannerImage);

        kategoriBencana = findViewById(R.id.kategoriBencana);
        kategoriPendidikan = findViewById(R.id.kategoriPendidikan);
        kategoriKesehatan = findViewById(R.id.kategoriKesehatan);
        kategoriKemanusiaan = findViewById(R.id.kategoriKemanusiaan);

        homeButton = findViewById(R.id.bottomNav).findViewById(R.id.homeButton);
        campaignsButton = findViewById(R.id.bottomNav).findViewById(R.id.donasiButton);
        profileButton = findViewById(R.id.bottomNav).findViewById(R.id.profileButton);

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
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sudah di halaman Home, tidak perlu melakukan apa-apa
            }
        });

        campaignsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TambahDonasiActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}

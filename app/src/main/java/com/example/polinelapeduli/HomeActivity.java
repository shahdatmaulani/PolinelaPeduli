package com.example.polinelapeduli;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.utils.UserUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private TextView headerWelcome;
    private EditText searchField;
    private LinearLayout kategoriBencana, kategoriPendidikan, kategoriKesehatan, kategoriKemanusiaan;
    private BottomNavigationView bottomNavigationView;
    private DatabaseHelper databaseHelper;
    private String role;

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

        // Inisialisasi DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Dapatkan peran pengguna dari SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        role = preferences.getString("role", "user");

        // Atur Bottom Navigation berdasarkan peran
        if (role.equals("admin")) {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_admin);
        } else {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_user);
        }

        // Mengambil dan menampilkan nama pengguna
        UserUtils.getCurrentFullName(fullName -> {
            if (fullName != null) {
                headerWelcome.setText("Welcome, " + fullName + "!");
            } else {
                headerWelcome.setText("Welcome!");
            }
        });

        // Pencarian berdasarkan input di searchField
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String queryText = s.toString().trim();
                if (!queryText.isEmpty()) {
                    searchDonations(queryText);
                }
            }
        });

        // Aksi saat kategori dipilih
        kategoriBencana.setOnClickListener(v -> openCategoryActivity(BencanaActivity.class));
        kategoriPendidikan.setOnClickListener(v -> openCategoryActivity(PendidikanActivity.class));
        kategoriKesehatan.setOnClickListener(v -> openCategoryActivity(KesehatanActivity.class));
        kategoriKemanusiaan.setOnClickListener(v -> openCategoryActivity(KemanusiaanActivity.class));

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

    private void searchDonations(String queryText) {
        // Gunakan Cursor untuk menjalankan query pencarian pada DatabaseHelper
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_DONASI + " WHERE " +
                        DatabaseHelper.COLUMN_NAMA + " LIKE ? OR " +
                        DatabaseHelper.COLUMN_KATEGORI + " LIKE ?",
                new String[]{"%" + queryText + "%", "%" + queryText + "%"}
        );

        if (cursor.moveToFirst()) {
            StringBuilder resultBuilder = new StringBuilder();
            do {
                String namaDonasi = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAMA));
                resultBuilder.append("Ditemukan: ").append(namaDonasi).append("\n");
            } while (cursor.moveToNext());

            Toast.makeText(this, resultBuilder.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Tidak ada hasil ditemukan", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void openCategoryActivity(Class<?> activityClass) {
        Intent intent = new Intent(HomeActivity.this, activityClass);
        startActivity(intent);
    }
}

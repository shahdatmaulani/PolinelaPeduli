package com.example.polinelapeduli.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.R;

import java.util.ArrayList;

public class RiwayatTransaksiActivity extends AppCompatActivity {

    private ListView listViewRiwayatDonasi;
    private ArrayList<String> riwayatDonasiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_transaksi);

        listViewRiwayatDonasi = findViewById(R.id.listViewRiwayatDonasi);
        riwayatDonasiList = new ArrayList<>();

        loadRiwayatDonasiDummy();

        // Set adapter untuk ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, riwayatDonasiList);
        listViewRiwayatDonasi.setAdapter(adapter);

        // Set listener untuk item click
        listViewRiwayatDonasi.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDonasi = riwayatDonasiList.get(position);
            Toast.makeText(RiwayatTransaksiActivity.this, "Selected: " + selectedDonasi, Toast.LENGTH_SHORT).show();
            // Anda dapat menambahkan aksi lainnya di sini, seperti menampilkan detail donasi
        });
    }

    private void loadRiwayatDonasiDummy() {
        // Data dummy untuk simulasi tampilan riwayat donasi
        riwayatDonasiList.clear();
        riwayatDonasiList.add("Donasi Kesehatan A - Jumlah Donasi: Rp 750.000");
        riwayatDonasiList.add("Donasi Kemanusiaan B - Jumlah Donasi: Rp 1.250.000");
        riwayatDonasiList.add("Donasi Pendidikan C - Jumlah Donasi: Rp 500.000");
    }
}

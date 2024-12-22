package com.example.polinelapeduli.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.R;

import java.util.ArrayList;

public class LaporanDonasiActivity extends AppCompatActivity {

    private ListView listViewLaporanDonasi;
    private ArrayList<String> laporanDonasiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_donasi);

        listViewLaporanDonasi = findViewById(R.id.listViewLaporanDonasi);
        laporanDonasiList = new ArrayList<>();

        loadLaporanDonasiDummy();
    }

    private void loadLaporanDonasiDummy() {
        // Data dummy untuk simulasi laporan donasi
        laporanDonasiList.clear();
        laporanDonasiList.add("Donasi Kesehatan A - Jumlah Donasi: Rp 750.000");
        laporanDonasiList.add("Donasi Kemanusiaan B - Jumlah Donasi: Rp 1.250.000");
        laporanDonasiList.add("Donasi Pendidikan C - Jumlah Donasi: Rp 500.000");

        // Set adapter untuk ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, laporanDonasiList);
        listViewLaporanDonasi.setAdapter(adapter);
    }
}

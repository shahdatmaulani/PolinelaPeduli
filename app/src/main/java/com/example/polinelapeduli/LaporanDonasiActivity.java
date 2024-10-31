package com.example.polinelapeduli;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LaporanDonasiActivity extends AppCompatActivity {

    private ListView listViewLaporanDonasi;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> laporanDonasiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_donasi);

        listViewLaporanDonasi = findViewById(R.id.listViewLaporanDonasi);
        databaseHelper = new DatabaseHelper(this);
        laporanDonasiList = new ArrayList<>();

        loadLaporanDonasi();
    }

    private void loadLaporanDonasi() {
        Cursor cursor = databaseHelper.getAllDonasi();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nama = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAMA));
                String jumlahDonasi = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_JUMLAH_DONASI));
                laporanDonasiList.add(nama + " - Jumlah Donasi: " + jumlahDonasi);
            }
            cursor.close();
        }

        // Set adapter untuk ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, laporanDonasiList);
        listViewLaporanDonasi.setAdapter(adapter);
    }
}

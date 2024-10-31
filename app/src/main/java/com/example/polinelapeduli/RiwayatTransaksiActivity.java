package com.example.polinelapeduli;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class RiwayatTransaksiActivity extends AppCompatActivity {

    private ListView listViewRiwayatDonasi;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> riwayatDonasiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_transaksi);

        listViewRiwayatDonasi = findViewById(R.id.listViewRiwayatDonasi);
        databaseHelper = new DatabaseHelper(this);
        riwayatDonasiList = new ArrayList<>();

        loadRiwayatDonasi();

        // Set adapter untuk ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, riwayatDonasiList);
        listViewRiwayatDonasi.setAdapter(adapter);

        // Set listener untuk item click
        listViewRiwayatDonasi.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            String selectedDonasi = riwayatDonasiList.get(position);
            Toast.makeText(RiwayatTransaksiActivity.this, "Selected: " + selectedDonasi, Toast.LENGTH_SHORT).show();
            // Anda dapat menambahkan aksi lainnya di sini, seperti menampilkan detail donasi
        });
    }

    private void loadRiwayatDonasi() {
        Cursor cursor = databaseHelper.getAllDonasi();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nama = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAMA));
                String jumlahDonasi = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_JUMLAH_DONASI));
                riwayatDonasiList.add(nama + " - Jumlah Donasi: " + jumlahDonasi);
            }
            cursor.close();
        }
    }
}

package com.example.polinelapeduli;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BencanaActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper databaseHelper;
    private ArrayList<Donasi> donasiList;
    private DonasiAdapter donasiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bencana);

        listView = findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);
        donasiList = new ArrayList<>();

        loadDonasi(); // Load data saat aktivitas pertama kali dibuat

        // Set event long click pada ListView
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            final Donasi selectedDonasi = donasiList.get(position);
            showOptionsDialog(selectedDonasi);
            return true;
        });
    }

    private void loadDonasi() {
        donasiList.clear(); // Bersihkan daftar sebelum memuat data baru
        Cursor cursor = databaseHelper.getAllDonasi();
        if (cursor.moveToFirst()) {
            do {
                String kategori = cursor.getString(cursor.getColumnIndexOrThrow("kategori"));
                if ("Bencana".equals(kategori)) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
                    String deskripsi = cursor.getString(cursor.getColumnIndexOrThrow("deskripsi"));
                    int target = cursor.getInt(cursor.getColumnIndexOrThrow("target"));
                    String gambar = cursor.getString(cursor.getColumnIndexOrThrow("gambar"));

                    Donasi donasi = new Donasi(id, nama, deskripsi, kategori, target, gambar);
                    donasiList.add(donasi);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        donasiAdapter = new DonasiAdapter(this, donasiList);
        listView.setAdapter(donasiAdapter);
        donasiAdapter.notifyDataSetChanged(); // Perbarui tampilan setelah memuat data
    }

    private void showOptionsDialog(Donasi selectedDonasi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BencanaActivity.this);
        builder.setTitle("Pilih Opsi");
        String[] options = {"Edit", "Hapus"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                editDonasi(selectedDonasi);
            } else if (which == 1) {
                hapusDonasi(selectedDonasi.getId());
            }
        });
        builder.show();
    }

    private void editDonasi(Donasi donasi) {
        Intent intent = new Intent(BencanaActivity.this, EditDonasiActivity.class);
        intent.putExtra("id", donasi.getId());
        intent.putExtra("nama", donasi.getNama());
        intent.putExtra("deskripsi", donasi.getDeskripsi());
        intent.putExtra("target", donasi.getTarget());
        startActivity(intent);
    }

    private void hapusDonasi(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BencanaActivity.this);
        builder.setTitle("Konfirmasi Hapus");
        builder.setMessage("Apakah Anda yakin ingin menghapus donasi ini?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            boolean deleted = databaseHelper.hapusDonasi(id);
            if (deleted) {
                Toast.makeText(BencanaActivity.this, "Donasi berhasil dihapus", Toast.LENGTH_SHORT).show();
                loadDonasi();  // Refresh ListView setelah penghapusan
            } else {
                Toast.makeText(BencanaActivity.this, "Gagal menghapus donasi", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDonasi(); // Muat ulang data dari database ketika aktivitas dilanjutkan
    }
}

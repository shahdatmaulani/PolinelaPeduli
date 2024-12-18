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

public class PendidikanActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper databaseHelper;
    private ArrayList<Donasi> donasiList;
    private DonasiAdapter donasiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendidikan);

        listView = findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);
        donasiList = new ArrayList<>();

        View headerView = getLayoutInflater().inflate(R.layout.activity_pendidikan_header, null);
        listView.addHeaderView(headerView);

        loadDonasi();

        // Set event long click pada ListView
        listView.setOnItemLongClickListener((parent, view, position, id) -> {  if (position > 0) {
            final Donasi selectedDonasi = donasiList.get(position - 1);
            showOptionsDialog(selectedDonasi);
        }
            return true;
        });
    }

    private void loadDonasi() {
        Cursor cursor = databaseHelper.getAllDonasi();
        donasiList.clear();
        if (cursor.moveToFirst()) {
            do {
                String kategori = cursor.getString(cursor.getColumnIndexOrThrow("kategori"));
                if ("Pendidikan".equals(kategori)) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
                    String deskripsi = cursor.getString(cursor.getColumnIndexOrThrow("deskripsi"));
                    int target = cursor.getInt(cursor.getColumnIndexOrThrow("target"));
                    String gambar = cursor.getString(cursor.getColumnIndexOrThrow("gambar"));

                    // Mengambil email dari sumber yang relevan (misalnya, database Firebase atau kosong jika tidak tersedia)
                    String email = ""; // Ganti dengan nilai email yang sesuai

                    Donasi donasi = new Donasi(id, nama, deskripsi, kategori, target, gambar, email);
                    donasiList.add(donasi);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        donasiAdapter = new DonasiAdapter(this, donasiList);
        listView.setAdapter(donasiAdapter);
    }

    private void showOptionsDialog(Donasi selectedDonasi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PendidikanActivity.this);
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
        Intent intent = new Intent(PendidikanActivity.this, EditDonasiActivity.class);
        intent.putExtra("id", donasi.getId());
        intent.putExtra("nama", donasi.getNama());
        intent.putExtra("deskripsi", donasi.getDeskripsi());
        intent.putExtra("target", donasi.getTarget());
        startActivity(intent);
    }

    private void hapusDonasi(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PendidikanActivity.this);
        builder.setTitle("Konfirmasi Hapus");
        builder.setMessage("Apakah Anda yakin ingin menghapus donasi ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean deleted = databaseHelper.hapusDonasi(id);
                if (deleted) {
                    Toast.makeText(PendidikanActivity.this, "Donasi berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadDonasi();
                } else {
                    Toast.makeText(PendidikanActivity.this, "Gagal menghapus donasi", Toast.LENGTH_SHORT).show();
                }
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

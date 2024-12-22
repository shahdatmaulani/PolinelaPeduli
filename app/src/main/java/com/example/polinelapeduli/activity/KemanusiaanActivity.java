package com.example.polinelapeduli.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.R;
import com.example.polinelapeduli.model.Donasi;

import java.util.ArrayList;

public class KemanusiaanActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Donasi> donasiList;
    private DonasiAdapter donasiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kemanusiaan);

        listView = findViewById(R.id.listView);
        donasiList = new ArrayList<>();

        View headerView = getLayoutInflater().inflate(R.layout.activity_kemanusiaan_header, null);
        listView.addHeaderView(headerView);

        loadDonasiDummy();

        // Set event long click pada ListView
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position > 0) {
                final Donasi selectedDonasi = donasiList.get(position - 1);
                showOptionsDialog(selectedDonasi);
            }
            return true;
        });
    }

    private void loadDonasiDummy() {
        // Data dummy untuk mengisi tampilan
        donasiList.clear();
        donasiList.add(new Donasi(1, "Donasi Kemanusiaan A", "Deskripsi donasi A", "Kemanusiaan", 500000, "image_a", "email@example.com"));
        donasiList.add(new Donasi(2, "Donasi Kemanusiaan B", "Deskripsi donasi B", "Kemanusiaan", 1000000, "image_b", "email@example.com"));

        donasiAdapter = new DonasiAdapter(this, donasiList);
        listView.setAdapter(donasiAdapter);
    }

    private void showOptionsDialog(Donasi selectedDonasi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(KemanusiaanActivity.this);
        builder.setTitle("Pilih Opsi");
        String[] options = {"Edit", "Hapus"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                editDonasi(selectedDonasi);
            } else if (which == 1) {
                hapusDonasi(selectedDonasi);
            }
        });
        builder.show();
    }

    private void editDonasi(Donasi donasi) {
        Intent intent = new Intent(KemanusiaanActivity.this, EditDonasiActivity.class);
        intent.putExtra("id", donasi.getId());
        intent.putExtra("nama", donasi.getNama());
        intent.putExtra("deskripsi", donasi.getDeskripsi());
        intent.putExtra("target", donasi.getTarget());
        startActivity(intent);
    }

    private void hapusDonasi(Donasi donasi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(KemanusiaanActivity.this);
        builder.setTitle("Konfirmasi Hapus");
        builder.setMessage("Apakah Anda yakin ingin menghapus donasi ini?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            // Hapus data dari list dummy
            donasiList.remove(donasi);
            donasiAdapter.notifyDataSetChanged();
            Toast.makeText(KemanusiaanActivity.this, "Donasi berhasil dihapus (simulasi)", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDonasiDummy(); // Muat ulang data dummy ketika aktivitas dilanjutkan
    }
}

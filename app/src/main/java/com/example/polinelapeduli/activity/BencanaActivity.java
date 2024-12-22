package com.example.polinelapeduli.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.R;
import com.example.polinelapeduli.model.Donasi;

import java.util.ArrayList;

public class BencanaActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Donasi> donasiList;
    private DonasiAdapter donasiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bencana);

        listView = findViewById(R.id.listView);
        donasiList = new ArrayList<>();

        View headerView = getLayoutInflater().inflate(R.layout.activity_bencana_header, null);
        listView.addHeaderView(headerView);

        // Placeholder untuk data dummy
        loadDonasiDummy(); // Data sementara untuk tampilan

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
        donasiList.add(new Donasi(1, "Bencana Alam A", "Deskripsi bencana A", "Bencana", 1000000, "image_a", "email_a"));
        donasiList.add(new Donasi(2, "Bencana Alam B", "Deskripsi bencana B", "Bencana", 2000000, "image_b", "email_b"));

        donasiAdapter = new DonasiAdapter(this, donasiList);
        listView.setAdapter(donasiAdapter);
        donasiAdapter.notifyDataSetChanged();
    }

    private void showOptionsDialog(Donasi selectedDonasi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BencanaActivity.this);
        builder.setTitle("Pilih Opsi");
        String[] options = {"Edit", "Hapus"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                editDonasi(selectedDonasi);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadDonasiDummy(); // Muat ulang data dummy ketika aktivitas dilanjutkan
    }
}

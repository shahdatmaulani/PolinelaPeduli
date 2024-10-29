package com.example.polinelapeduli;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide; // Pastikan Anda sudah menambahkan Glide di gradle

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DonasiAdapter extends ArrayAdapter<Donasi> {

    private final Context context;
    private final ArrayList<Donasi> donasiList;

    public DonasiAdapter(Context context, ArrayList<Donasi> donasiList) {
        super(context, 0, donasiList);
        this.context = context;
        this.donasiList = donasiList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Donasi donasi = getItem(position);

        // Memeriksa apakah ada tampilan yang dapat digunakan kembali
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_donasi, parent, false);
        }

        // Menemukan tampilan untuk ditampilkan
        TextView tvNamaDonasi = convertView.findViewById(R.id.tvNamaDonasi);
        TextView tvDeskripsiDonasi = convertView.findViewById(R.id.tvDeskripsiDonasi);
        TextView tvTargetDonasi = convertView.findViewById(R.id.tvTargetDonasi);
        ImageView imageViewDonasi = convertView.findViewById(R.id.imageViewDonasi);
        Button btnDonasiSekarang = convertView.findViewById(R.id.btnDonasiSekarang);

        // Mengatur teks pada TextViews
        tvNamaDonasi.setText(donasi.getNama());
        tvDeskripsiDonasi.setText(donasi.getDeskripsi());

        // Memformat target donasi
        String formattedTarget = formatCurrency(donasi.getTarget());
        tvTargetDonasi.setText(formattedTarget);

        // Memuat gambar ke dalam ImageView menggunakan Glide
        Glide.with(context)
                .load(donasi.getGambar()) // Gambar bisa berupa URL atau path lokal
                .placeholder(R.drawable.placeholder) // Placeholder jika gambar tidak tersedia
                .error(R.drawable.error) // Gambar untuk error
                .into(imageViewDonasi);

        // listener untuk tombol Donasi Sekarang
        btnDonasiSekarang.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra("id", donasi.getId());
            intent.putExtra("nama", donasi.getNama());
            intent.putExtra("target", donasi.getTarget());
            intent.putExtra("deskripsi", donasi.getDeskripsi());
            intent.putExtra("kategori", donasi.getKategori());
            context.startActivity(intent);
        });

        // Tambahkan listener untuk klik lama pada item donasi
        convertView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Pilih Opsi");
            String[] options = {"Edit", "Hapus"};
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // Edit dipilih
                    Intent intent = new Intent(context, EditDonasiActivity.class);
                    intent.putExtra("id", donasi.getId());
                    intent.putExtra("nama", donasi.getNama());
                    intent.putExtra("deskripsi", donasi.getDeskripsi());
                    intent.putExtra("kategori", donasi.getKategori());
                    intent.putExtra("target", donasi.getTarget());
                    intent.putExtra("gambar", donasi.getGambar());
                    context.startActivity(intent);
                } else if (which == 1) {
                    // Hapus dipilih
                    AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
                    confirmBuilder.setTitle("Konfirmasi Hapus");
                    confirmBuilder.setMessage("Apakah Anda yakin ingin menghapus donasi ini?");
                    confirmBuilder.setPositiveButton("Ya", (confirmDialog, confirmWhich) -> {
                        DatabaseHelper dbHelper = new DatabaseHelper(context);
                        boolean deleted = dbHelper.hapusDonasi(donasi.getId());
                        if (deleted) {
                            Toast.makeText(context, "Donasi berhasil dihapus", Toast.LENGTH_SHORT).show();
                            donasiList.remove(donasi);  // Hapus dari list dan refresh adapter
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Gagal menghapus donasi", Toast.LENGTH_SHORT).show();
                        }
                    });
                    confirmBuilder.setNegativeButton("Tidak", null);
                    confirmBuilder.show();
                }
            });
            builder.show();
            return true;
        });


        return convertView;
    }


    // Fungsi untuk memformat angka sebagai mata uang Rupiah
    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID")); // Mengatur locale untuk Indonesia
        return format.format(amount); // Mengembalikan formatted string
    }
}


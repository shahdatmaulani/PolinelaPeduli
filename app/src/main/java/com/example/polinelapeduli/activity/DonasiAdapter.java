package com.example.polinelapeduli.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.polinelapeduli.R;
import com.example.polinelapeduli.model.Donasi;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DonasiAdapter extends ArrayAdapter<Donasi> {

    private final Context context;
    private final ArrayList<Donasi> donasiList;
    private final String userRole; // Menyimpan peran pengguna

    public DonasiAdapter(Context context, ArrayList<Donasi> donasiList) {
        super(context, 0, donasiList);
        this.context = context;
        this.donasiList = donasiList;

        // Default user role
        userRole = "user";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Donasi donasi = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_donasi, parent, false);
        }

        TextView tvNamaDonasi = convertView.findViewById(R.id.tvNamaDonasi);
        TextView tvDeskripsiDonasi = convertView.findViewById(R.id.tvDeskripsiDonasi);
        TextView tvTargetDonasi = convertView.findViewById(R.id.tvTargetDonasi);
        ImageView imageViewDonasi = convertView.findViewById(R.id.imageViewDonasi);
        Button btnDonasiSekarang = convertView.findViewById(R.id.btnDonasiSekarang);
        EditText etJumlahDonasi = convertView.findViewById(R.id.etJumlahDonasi);

        tvNamaDonasi.setText(donasi.getNama());
        tvDeskripsiDonasi.setText(donasi.getDeskripsi());
        String formattedTarget = formatCurrency(donasi.getTarget());
        tvTargetDonasi.setText(formattedTarget);

        Glide.with(context)
                .load(donasi.getGambar())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageViewDonasi);

        btnDonasiSekarang.setOnClickListener(v -> {
            String jumlahDonasiStr = etJumlahDonasi.getText().toString();
            if (jumlahDonasiStr.isEmpty()) {
                Toast.makeText(context, "Masukkan jumlah donasi terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            int jumlahDonasi = Integer.parseInt(jumlahDonasiStr);
            if (jumlahDonasi < 1000) {
                Toast.makeText(context, "Jumlah donasi minimal Rp 1.000", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hanya memberikan notifikasi tanpa database
            Toast.makeText(context, "Terima kasih atas donasi sebesar Rp " + jumlahDonasi, Toast.LENGTH_SHORT).show();
        });

        convertView.setOnLongClickListener(v -> {
            if (userRole.equals("admin")) { // Hanya admin yang bisa mengedit atau menghapus
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Pilih Opsi");
                String[] options = {"Edit", "Hapus"};
                builder.setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(context, EditDonasiActivity.class);
                        intent.putExtra("id", donasi.getId());
                        intent.putExtra("nama", donasi.getNama());
                        intent.putExtra("deskripsi", donasi.getDeskripsi());
                        intent.putExtra("kategori", donasi.getKategori());
                        intent.putExtra("target", donasi.getTarget());
                        intent.putExtra("gambar", donasi.getGambar());
                        context.startActivity(intent);
                    } else if (which == 1) {
                        Toast.makeText(context, "Donasi telah dihapus (simulasi)", Toast.LENGTH_SHORT).show();
                        donasiList.remove(donasi);
                        notifyDataSetChanged();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(context, "Ayo Donasi Sekarang dengan cara klik tombol Donasi Sekarang", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        return convertView;
    }

    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount);
    }
}

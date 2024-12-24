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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.polinelapeduli.R;
import com.example.polinelapeduli.model.Donation;
import com.example.polinelapeduli.repository.DonationRepository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DonasiAdapter extends ArrayAdapter<Donation> {

    private final Context context;
    private final ArrayList<Donation> donationList;
    private final String userRole;
    private final DonationRepository donationRepository;

    public DonasiAdapter(@NonNull Context context, @NonNull ArrayList<Donation> donationList) {
        super(context, 0, donationList);
        this.context = context;
        this.donationList = donationList;
        this.userRole = "USER"; // Default user role
        this.donationRepository = new DonationRepository(context); // Initialize DonationRepository
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Donation donation = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_donasi, parent, false);
        }

        TextView tvNamaDonasi = convertView.findViewById(R.id.tvNamaDonasi);
        TextView tvDeskripsiDonasi = convertView.findViewById(R.id.tvDeskripsiDonasi);
        TextView tvTargetDonasi = convertView.findViewById(R.id.tvTargetDonasi);
        ImageView imageViewDonasi = convertView.findViewById(R.id.imageViewDonasi);
        Button btnDonasiSekarang = convertView.findViewById(R.id.btnDonasiSekarang);
        EditText etJumlahDonasi = convertView.findViewById(R.id.etJumlahDonasi);

        if (donation != null) {
            // Set data ke UI
            tvNamaDonasi.setText(donation.getName());
            tvDeskripsiDonasi.setText(donation.getDescription());
            tvTargetDonasi.setText(formatCurrency(donation.getTarget()));

            // Load gambar dengan Glide
            Glide.with(context)
                    .load(donation.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(imageViewDonasi);

            // Tombol Donasi Sekarang
            btnDonasiSekarang.setOnClickListener(v -> handleDonationClick(etJumlahDonasi));

            // Long click untuk admin
            convertView.setOnLongClickListener(v -> handleLongClick(donation));
        }

        return convertView;
    }

    private void handleDonationClick(EditText etJumlahDonasi) {
        String jumlahDonasiStr = etJumlahDonasi.getText().toString();
        if (jumlahDonasiStr.isEmpty()) {
            Toast.makeText(context, "Masukkan jumlah donasi terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        int jumlahDonasi = Integer.parseInt(jumlahDonasiStr);
        if (jumlahDonasi < 1000) {
            Toast.makeText(context, "Jumlah donasi minimal Rp 1.000", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Terima kasih atas donasi sebesar Rp " + jumlahDonasi, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean handleLongClick(Donation donation) {
        if ("ADMIN".equals(userRole)) {
            showAdminOptionsDialog(donation);
        } else {
            Toast.makeText(context, "Ayo Donasi Sekarang dengan cara klik tombol Donasi Sekarang", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void showAdminOptionsDialog(Donation donation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pilih Opsi");
        String[] options = {"Edit", "Hapus"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                startEditDonationIntent(donation);
            } else if (which == 1) {
                deleteDonation(donation);
            }
        });
        builder.show();
    }

    private void startEditDonationIntent(Donation donation) {
        Intent intent = new Intent(context, EditDonasiActivity.class);
        intent.putExtra("id", donation.getDonationId());
        intent.putExtra("nama", donation.getName());
        intent.putExtra("deskripsi", donation.getDescription());
        intent.putExtra("kategori", donation.getCategoryId());
        intent.putExtra("target", donation.getTarget());
        intent.putExtra("gambar", donation.getImage());
        context.startActivity(intent);
    }

    private void deleteDonation(Donation donation) {
        boolean isDeleted = donationRepository.softDeleteDonation(donation.getDonationId());
        if (isDeleted) {
            donationList.remove(donation);
            notifyDataSetChanged();
            Toast.makeText(context, "Donasi berhasil dihapus.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Gagal menghapus donasi.", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount);
    }
}

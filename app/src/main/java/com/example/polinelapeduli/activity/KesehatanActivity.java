package com.example.polinelapeduli.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.polinelapeduli.R;
import com.example.polinelapeduli.model.Donation;
import com.example.polinelapeduli.model.User;
import com.example.polinelapeduli.repository.DonationRepository;
import com.example.polinelapeduli.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class KesehatanActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Donation> donationList;
    private DonasiAdapter donasiAdapter;
    private DonationRepository donationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kesehatan);

        // Inisialisasi Firebase Auth dan UserRepository
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        UserRepository userRepository = new UserRepository(this);

        // Cek apakah data pengguna ada, sudah login, dan aktif
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        // Pastikan pengguna sudah login
        if (firebaseUser == null) {
            redirectToSignIn();
            return;
        }

        // Ambil data pengguna berdasarkan email
        User userLogin = userRepository.getUserByEmail(firebaseUser.getEmail());

        // Pastikan data pengguna ditemukan dan pengguna aktif
        if (userLogin == null || !userLogin.isActive()) {
            redirectToSignIn();
            return;
        }

        // Inisialisasi komponen
        listView = findViewById(R.id.listView);
        donationList = new ArrayList<>();
        donationRepository = new DonationRepository(this);

        // Tambahkan header jika ada
        @SuppressLint("InflateParams") View headerView = getLayoutInflater().inflate(R.layout.activity_kesehatan_header, null);
        listView.addHeaderView(headerView);

        // Muat data donasi berdasarkan kategori
        loadDonationsByCategory();

        // Set event long click pada ListView
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position > 0) { // Abaikan header
                final Donation selectedDonation = donationList.get(position - 1);
                showOptionsDialog(selectedDonation);
            }
            return true;
        });
    }

    // Metode untuk memuat data donasi berdasarkan kategori
    private void loadDonationsByCategory() {
        donationList.clear();
        String category = "Kesehatan";
        donationList.addAll(donationRepository.getAllDonationsWithCategory(category));

        donasiAdapter = new DonasiAdapter(this, donationList);
        listView.setAdapter(donasiAdapter);
    }
    private void redirectToSignIn() {
        Intent intent = new Intent(KesehatanActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    // Tampilkan dialog opsi edit atau hapus
    private void showOptionsDialog(Donation selectedDonation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(KesehatanActivity.this);
        builder.setTitle("Pilih Opsi");
        String[] options = {"Edit", "Hapus"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                editDonation(selectedDonation);
            } else if (which == 1) {
                deleteDonation(selectedDonation);
            }
        });
        builder.show();
    }

    // Metode untuk mengedit donasi
    private void editDonation(Donation donation) {
        Intent intent = new Intent(KesehatanActivity.this, EditDonasiActivity.class);
        intent.putExtra("id", donation.getDonationId());
        intent.putExtra("nama", donation.getName());
        intent.putExtra("deskripsi", donation.getDescription());
        intent.putExtra("target", donation.getTarget());
        intent.putExtra("gambar", donation.getImage());
        startActivity(intent);
    }

    // Metode untuk menghapus donasi
    private void deleteDonation(Donation donation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(KesehatanActivity.this);
        builder.setTitle("Konfirmasi Hapus");
        builder.setMessage("Apakah Anda yakin ingin menghapus donasi ini?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            boolean isDeleted = donationRepository.softDeleteDonation(donation.getDonationId());
            if (isDeleted) {
                donationList.remove(donation);
                donasiAdapter.notifyDataSetChanged();
                Toast.makeText(KesehatanActivity.this, "Donasi berhasil dihapus.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(KesehatanActivity.this, "Gagal menghapus donasi.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDonationsByCategory(); // Refresh data saat kembali ke aktivitas
    }
}

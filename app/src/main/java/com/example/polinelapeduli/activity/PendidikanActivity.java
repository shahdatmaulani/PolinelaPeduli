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
import com.example.polinelapeduli.utils.UserValidator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PendidikanActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Donation> donationList;
    private DonasiAdapter donasiAdapter;
    private DonationRepository donationRepository;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendidikan);

        User userLogin = UserValidator.validateUser(this);
        if (userLogin == null) {
            finish();
            return;
        }

        userRole = userLogin.getRole().toString();

        listView = findViewById(R.id.listView);
        donationList = new ArrayList<>();
        donationRepository = new DonationRepository(this);

        @SuppressLint("InflateParams") View headerView = getLayoutInflater().inflate(R.layout.activity_pendidikan_header, null);
        listView.addHeaderView(headerView);

        loadDonationsByCategory();


        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position > 0) { //Ignore Header
                final Donation selectedDonation = donationList.get(position - 1);
                showOptionsDialog(selectedDonation);
            }
            return true;
        });
    }

    private void loadDonationsByCategory() {
        donationList.clear();
        donationList.addAll(donationRepository.getAllDonationsWithCategory("Pendidikan"));

        donasiAdapter = new DonasiAdapter(this, donationList, userRole);
        listView.setAdapter(donasiAdapter);
    }

    private void showOptionsDialog(Donation selectedDonation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PendidikanActivity.this);
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

    private void editDonation(Donation donation) {
        Intent intent = new Intent(PendidikanActivity.this, EditDonasiActivity.class);
        intent.putExtra("id", donation.getDonationId());
        intent.putExtra("nama", donation.getName());
        intent.putExtra("deskripsi", donation.getDescription());
        intent.putExtra("target", donation.getTarget());
        intent.putExtra("gambar", donation.getImage());
        startActivity(intent);
    }

    private void deleteDonation(Donation donation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PendidikanActivity.this);
        builder.setTitle("Konfirmasi Hapus");
        builder.setMessage("Apakah Anda yakin ingin menghapus donasi ini?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            boolean isDeleted = donationRepository.softDeleteDonation(donation.getDonationId());
            if (isDeleted) {
                donationList.remove(donation);
                donasiAdapter.notifyDataSetChanged();
                Toast.makeText(PendidikanActivity.this, "Donasi berhasil dihapus.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PendidikanActivity.this, "Gagal menghapus donasi.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDonationsByCategory();
    }
}

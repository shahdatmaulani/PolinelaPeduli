package com.example.polinelapeduli.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.polinelapeduli.R;
import com.example.polinelapeduli.repository.DonationRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

public class EditDonasiActivity extends AppCompatActivity {

    private EditText etNamaDonasi, etDeskripsiDonasi, etTargetDonasi;
    private Spinner spinnerStatusDonation;
    private ImageView imageViewDonasi;
    private TextView tvStatusGambar;
    private DonationRepository donationRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private static final int PERMISSION_REQUEST_READ_IMAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donasi);

        donationRepository = new DonationRepository(this);

        initializeUI();
        setupImagePickerLauncher();
        setupStatusSpinner();
        loadDataFromIntent();
    }

    private void initializeUI() {
        etNamaDonasi = findViewById(R.id.etNamaDonasi);
        etDeskripsiDonasi = findViewById(R.id.etDeskripsiDonasi);
        etTargetDonasi = findViewById(R.id.etTargetDonasi);
        spinnerStatusDonation = findViewById(R.id.spinnerStatusDonation);
        imageViewDonasi = findViewById(R.id.imageViewDonasi);
        tvStatusGambar = findViewById(R.id.tvStatusGambar);
        Button btnPilihGambar = findViewById(R.id.btnPilihGambar);
        Button btnUpdateDonasi = findViewById(R.id.btnUpdate);

        btnPilihGambar.setOnClickListener(v -> chooseImage());
        btnUpdateDonasi.setOnClickListener(v -> updateDonation());
    }

    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        handleSelectedImage(selectedImageUri);
                    }
                }
        );
    }

    private void setupStatusSpinner() {
        String[] statuses = {"AKTIF", "KOMPLET", "DITUTUP"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statuses
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusDonation.setAdapter(adapter);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String name = intent.getStringExtra("nama");
        String description = intent.getStringExtra("deskripsi");
        int target = intent.getIntExtra("target", 0);
        String status = intent.getStringExtra("status");
        String imagePath = intent.getStringExtra("gambar");

        Log.d("EditDonasiActivity", "Received data: " + id + ", " + name + ", " + status);

        etNamaDonasi.setText(name);
        etDeskripsiDonasi.setText(description);
        etTargetDonasi.setText(String.valueOf(target));
        if (imagePath != null) {
            tvStatusGambar.setText(imagePath);
            imageViewDonasi.setImageURI(Uri.fromFile(new File(imagePath)));
        }

        if (spinnerStatusDonation.getAdapter() instanceof ArrayAdapter) {
            @SuppressWarnings("unchecked")
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerStatusDonation.getAdapter();
            int position = adapter.getPosition(status);
            if (position != -1) {
                spinnerStatusDonation.setSelection(position);
            }
        } else {
            Toast.makeText(this, "Adapter tidak kompatibel", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateDonation() {
        String nama = etNamaDonasi.getText().toString().trim();
        String deskripsi = etDeskripsiDonasi.getText().toString().trim();
        String imagePath = tvStatusGambar.getText().toString().trim();
        int target = Integer.parseInt(etTargetDonasi.getText().toString().trim());
        String selectedStatus = spinnerStatusDonation.getSelectedItem().toString();

        if (nama.isEmpty() || deskripsi.isEmpty() || imagePath.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = donationRepository.updateDonation(
                nama, deskripsi, target, selectedStatus, imagePath
        );

        if (isUpdated) {
            Toast.makeText(this, "Donasi berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal memperbarui donasi", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImage() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        checkPermission(permission);
    }

    private void checkPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(this, "Izin diperlukan untuk memilih gambar", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_READ_IMAGES);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Pilih Gambar"));
    }

    @SuppressLint("SetTextI18n")
    private void handleSelectedImage(Uri uri) {
        String imagePath = saveImageToInternalStorage(uri);
        if (imagePath != null) {
            imageViewDonasi.setImageURI(uri);
            tvStatusGambar.setText(imagePath);
            Log.d("EditDonasiActivity", "Image Path: " + imagePath);
        } else {
            Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(new File(getFilesDir(), getFileName(uri)))) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = Objects.requireNonNull(inputStream).read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            return getFilesDir() + File.separator + getFileName(uri);

        } catch (Exception e) {
            Log.e("EditDonasiActivity", "Error", e);
            return null;
        }
    }

    private String getFileName(Uri uri) {
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        String path = uri.getPath();
        return (path != null && path.lastIndexOf('/') != -1)
                ? path.substring(path.lastIndexOf('/') + 1)
                : "Unknown File";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Izin akses gambar ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

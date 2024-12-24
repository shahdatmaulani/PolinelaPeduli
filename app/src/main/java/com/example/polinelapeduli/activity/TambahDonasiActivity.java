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
import com.example.polinelapeduli.model.Category;
import com.example.polinelapeduli.model.Donation;
import com.example.polinelapeduli.repository.CategoryRepository;
import com.example.polinelapeduli.repository.DonationRepository;
import com.example.polinelapeduli.utils.CurrentTime;
import com.example.polinelapeduli.utils.Enum.EStatus;
import com.example.polinelapeduli.utils.InputValidator;
import com.example.polinelapeduli.utils.UserValidator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class TambahDonasiActivity extends AppCompatActivity {

    private EditText etNamaDonasi, etDeskripsiDonasi, etTargetDonasi;
    private Spinner spinnerKategori;
    private ImageView imageViewDonasi;
    private TextView tvStatusGambar;
    private DonationRepository donationRepository;
    private CategoryRepository categoryRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private static final int PERMISSION_REQUEST_READ_IMAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_donasi);

        if (UserValidator.validateUser(this) == null) {
            finish();
            return;
        }

        donationRepository = new DonationRepository(this);
        categoryRepository = new CategoryRepository(this);

        initializeUI();
        setupImagePickerLauncher();
        loadCategoriesIntoSpinner();
    }

    private void initializeUI() {
        etNamaDonasi = findViewById(R.id.etNamaDonasi);
        etDeskripsiDonasi = findViewById(R.id.etDeskripsiDonasi);
        etTargetDonasi = findViewById(R.id.etTargetDonasi);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        imageViewDonasi = findViewById(R.id.imageViewDonasi);
        tvStatusGambar = findViewById(R.id.tvStatusGambar);
        Button btnPilihGambar = findViewById(R.id.btnPilihGambar);
        Button btnSimpanDonasi = findViewById(R.id.btnSimpanDonasi);

        btnPilihGambar.setOnClickListener(v -> chooseImage());
        btnSimpanDonasi.setOnClickListener(v -> saveDonation());
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

    private void loadCategoriesIntoSpinner() {
        List<Category> categoryList = categoryRepository.getAllCategories();
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategori.setAdapter(adapter);
    }

    private void saveDonation() {
        String nama = InputValidator.getValidatedText(etNamaDonasi, "Nama donasi tidak boleh kosong");
        String deskripsi = InputValidator.getValidatedText(etDeskripsiDonasi, "Deskripsi donasi tidak boleh kosong");
        Integer target = InputValidator.getValidatedNumberWithMinValue(
                etTargetDonasi, "Masukkan target tidak boleh kosong", "Jumlah target minimal Rp 1.000", 1000
        );

        if (nama == null || deskripsi == null || target == null) return;

        Category selectedCategory = (Category) spinnerKategori.getSelectedItem();
        String imagePath = tvStatusGambar.getText().toString().trim();

        if (imagePath.isEmpty() || imagePath.equalsIgnoreCase("No File Chosen")) {
            Toast.makeText(this, "Harap pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        Donation donation = createDonation(nama, deskripsi, target, imagePath, selectedCategory);

        if (donationRepository.insertDonation(donation)) {
            navigateToCategoryActivity(selectedCategory);
        } else {
            Toast.makeText(this, "Gagal menyimpan donasi", Toast.LENGTH_SHORT).show();
        }
    }

    private Donation createDonation(String nama, String deskripsi, int target, String imagePath, Category selectedCategory) {
        Donation donation = new Donation();
        donation.setName(nama);
        donation.setDescription(deskripsi);
        donation.setCategoryId(selectedCategory.getCategoryId());
        donation.setCategoryName(selectedCategory.getName());
        donation.setTarget(target);
        donation.setImage(imagePath);
        donation.setStatus(EStatus.AKTIF);
        donation.setActive(true);
        donation.setCreatedAt(CurrentTime.getCurrentTime());
        donation.setUpdatedAt("");
        return donation;
    }

    private void navigateToCategoryActivity(Category selectedCategory) {
        Intent intent;
        switch (selectedCategory.getName()) {
            case "Bencana":
                intent = new Intent(this, BencanaActivity.class);
                break;
            case "Pendidikan":
                intent = new Intent(this, PendidikanActivity.class);
                break;
            case "Kesehatan":
                intent = new Intent(this, KesehatanActivity.class);
                break;
            case "Kemanusiaan":
                intent = new Intent(this, KemanusiaanActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
        Toast.makeText(this, "Donasi berhasil disimpan", Toast.LENGTH_SHORT).show();
        finish();
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
            Log.d("TambahDonasiActivity", "Image Path: " + imagePath);
        } else {
            Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
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
            Log.e("TambahDonasiActivity", "Error", e);
            return null;
        }
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

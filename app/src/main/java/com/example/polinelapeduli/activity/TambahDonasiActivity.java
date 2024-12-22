package com.example.polinelapeduli.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.polinelapeduli.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class TambahDonasiActivity extends AppCompatActivity {

    private EditText etNamaDonasi, etDeskripsiDonasi, etTargetDonasi;
    private RadioGroup radioGroupKategori;
    private ImageView imageViewDonasi;
    private TextView tvStatusGambar;
    private Button btnPilihGambar, btnSimpanDonasi;
    private String gambarPath;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_donasi);

        etNamaDonasi = findViewById(R.id.etNamaDonasi);
        etDeskripsiDonasi = findViewById(R.id.etDeskripsiDonasi);
        etTargetDonasi = findViewById(R.id.etTargetDonasi);
        radioGroupKategori = findViewById(R.id.radioGroupKategori);
        imageViewDonasi = findViewById(R.id.imageViewDonasi);
        tvStatusGambar = findViewById(R.id.tvStatusGambar);
        btnPilihGambar = findViewById(R.id.btnPilihGambar);
        btnSimpanDonasi = findViewById(R.id.btnSimpanDonasi);

        btnPilihGambar.setOnClickListener(v -> chooseImage());

        btnSimpanDonasi.setOnClickListener(v -> saveDonation());
    }

    private void chooseImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES);
            } else {
                openImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            gambarPath = saveImageToInternalStorage(imageUri);
            if (gambarPath != null) {
                imageViewDonasi.setImageURI(imageUri);
                tvStatusGambar.setText("File dipilih: " + getFileName(imageUri));
            } else {
                Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), getFileName(uri));
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveDonation() {
        String nama = etNamaDonasi.getText().toString();
        String deskripsi = etDeskripsiDonasi.getText().toString();
        String kategori = getSelectedCategory();
        int target = Integer.parseInt(etTargetDonasi.getText().toString());

        // Simulasi penyimpanan data
        Toast.makeText(this, "Donasi berhasil disimpan:\nNama: " + nama + "\nKategori: " + kategori + "\nTarget: Rp " + target, Toast.LENGTH_SHORT).show();
        finish();
    }

    private String getSelectedCategory() {
        int selectedId = radioGroupKategori.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        return radioButton.getText().toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Izin akses gambar ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

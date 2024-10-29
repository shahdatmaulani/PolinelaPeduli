package com.example.polinelapeduli;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditDonasiActivity extends AppCompatActivity {

    private EditText editNama, editDeskripsi, editTarget;
    private Button btnUpdate, btnPilihGambar;
    private ImageView imageViewDonasi;
    private TextView tvStatusGambar;
    private String gambarPath;
    private DatabaseHelper databaseHelper;
    private int donasiId;
    private String kategoriDonasi;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donasi);

        initViews();
        databaseHelper = new DatabaseHelper(this);
        loadDataFromIntent();

        btnPilihGambar.setOnClickListener(v -> chooseImage());
        btnUpdate.setOnClickListener(v -> updateDonasi());
    }

    private void initViews() {
        editNama = findViewById(R.id.editNama);
        editDeskripsi = findViewById(R.id.editDeskripsi);
        editTarget = findViewById(R.id.editTarget);
        btnUpdate = findViewById(R.id.btnUpdate);
        imageViewDonasi = findViewById(R.id.imageViewDonasi);
        tvStatusGambar = findViewById(R.id.tvStatusGambar);
        btnPilihGambar = findViewById(R.id.btnPilihGambar);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        donasiId = intent.getIntExtra("id", -1);
        String nama = intent.getStringExtra("nama");
        String deskripsi = intent.getStringExtra("deskripsi");
        int target = intent.getIntExtra("target", 0);
        kategoriDonasi = intent.getStringExtra("kategori");

        editNama.setText(nama);
        editDeskripsi.setText(deskripsi);
        editTarget.setText(String.valueOf(target));
        setRadioButtonByCategory(kategoriDonasi);
    }

    private void setRadioButtonByCategory(String kategori) {
        switch (kategori) {
            case "Bencana":
                ((RadioButton) findViewById(R.id.rbBencanaEdit)).setChecked(true);
                break;
            case "Pendidikan":
                ((RadioButton) findViewById(R.id.rbPendidikanEdit)).setChecked(true);
                break;
            case "Kesehatan":
                ((RadioButton) findViewById(R.id.rbKesehatanEdit)).setChecked(true);
                break;
            case "Kemanusiaan":
                ((RadioButton) findViewById(R.id.rbKemanusiaanEdit)).setChecked(true);
                break;
        }
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
        if ("content".equals(uri.getScheme())) {
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

    private void updateDonasi() {
        String updatedNama = editNama.getText().toString();
        String updatedDeskripsi = editDeskripsi.getText().toString();
        int updatedTarget = Integer.parseInt(editTarget.getText().toString());

        // Pertahankan gambar lama jika tidak dipilih gambar baru
        if (gambarPath == null) {
            Cursor cursor = databaseHelper.getDonasiById(donasiId); // Mendapatkan data donasi lama berdasarkan id
            if (cursor.moveToFirst()) {
                gambarPath = cursor.getString(cursor.getColumnIndexOrThrow("gambar")); // Gambar lama dari database
            }
            cursor.close();
        }

        // Memastikan gambarPath tidak null sebelum memperbarui
        if (gambarPath == null) {
            Toast.makeText(this, "Gambar tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil kategori yang dipilih
        RadioGroup radioGroup = findViewById(R.id.radioGroupKategoriEdit);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            kategoriDonasi = selectedRadioButton.getText().toString();
        } else {
            Toast.makeText(this, "Silakan pilih kategori", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = databaseHelper.updateDonasi(donasiId, updatedNama, updatedDeskripsi, kategoriDonasi, updatedTarget, gambarPath);
        if (isUpdated) {
            Toast.makeText(EditDonasiActivity.this, "Donasi berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(EditDonasiActivity.this, "Gagal memperbarui donasi", Toast.LENGTH_SHORT).show();
        }
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

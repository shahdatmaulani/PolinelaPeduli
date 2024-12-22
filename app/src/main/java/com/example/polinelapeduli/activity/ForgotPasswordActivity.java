package com.example.polinelapeduli.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.polinelapeduli.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Menghubungkan elemen UI
        emailInput = findViewById(R.id.emailInput);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        TextView kembaliLogin = findViewById(R.id.kembaliLogin);

        // Mengatur listener untuk tombol Reset Password
        btnResetPassword.setOnClickListener(v -> resetPassword());

        // Mengatur listener untuk teks kembali ke login
        kembaliLogin.setOnClickListener(v -> {
            // Logika untuk kembali ke halaman login
            finish(); // Mengakhiri activity ini dan kembali ke activity sebelumnya
        });
    }

    private void resetPassword() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            // Menampilkan pesan jika email tidak diisi
            Toast.makeText(this, "Silakan masukkan email Anda", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logika untuk mengirim email reset password
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Instruksi reset password telah dikirim ke " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Gagal mengirim instruksi reset password", Toast.LENGTH_SHORT).show();
                    }
                });

        // Kosongkan input setelah berhasil
        emailInput.setText("");
    }
}

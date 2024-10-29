package com.example.polinelapeduli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button btnResetPassword;
    private TextView kembaliLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Menghubungkan elemen UI
        emailInput = findViewById(R.id.emailInput);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        kembaliLogin = findViewById(R.id.kembaliLogin);

        // Mengatur listener untuk tombol Reset Password
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        // Mengatur listener untuk teks kembali ke login
        kembaliLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logika untuk kembali ke halaman login
                finish(); // Mengakhiri activity ini dan kembali ke activity sebelumnya
            }
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
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Instruksi reset password telah dikirim ke " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Gagal mengirim instruksi reset password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Kosongkan input setelah berhasil
        emailInput.setText("");
    }
}

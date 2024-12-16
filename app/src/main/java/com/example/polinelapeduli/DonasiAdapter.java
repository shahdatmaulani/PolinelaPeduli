package com.example.polinelapeduli;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DonasiAdapter extends ArrayAdapter<Donasi> {

    private final Context context;
    private final ArrayList<Donasi> donasiList;
    private final String userRole; // Menyimpan peran pengguna

    public DonasiAdapter(Context context, ArrayList<Donasi> donasiList) {
        super(context, 0, donasiList);
        this.context = context;
        this.donasiList = donasiList;

        // Mengambil peran pengguna dari SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userRole = preferences.getString("role", "user"); // Default ke user jika tidak ada
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

            // Simpan jumlah donasi ke database
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            ContentValues values = new ContentValues();
            values.put("jumlah_donasi", jumlahDonasi);
            dbHelper.updateDonasi(donasi.getId(), donasi.getNama(), donasi.getDeskripsi(), donasi.getKategori(), donasi.getTarget(), donasi.getGambar(), jumlahDonasi);

            String orderId = "donation" + System.currentTimeMillis();
            String itemName = donasi.getNama();

            JSONObject paymentData = new JSONObject();
            try {
                JSONObject transactionDetails = new JSONObject();
                transactionDetails.put("order_id", orderId);
                transactionDetails.put("gross_amount", jumlahDonasi);

                JSONArray itemDetails = new JSONArray();
                JSONObject item = new JSONObject();
                item.put("id", "D01");
                item.put("price", jumlahDonasi);
                item.put("quantity", 1);
                item.put("name", itemName);
                itemDetails.put(item);

                JSONObject customerDetails = new JSONObject();
                customerDetails.put("email", "donor@example.com");
                customerDetails.put("first_name", "Andi");
                customerDetails.put("last_name", "Setiawan");
                customerDetails.put("phone", "628112345678");

                paymentData.put("transaction_details", transactionDetails);
                paymentData.put("item_details", itemDetails);
                paymentData.put("customer_details", customerDetails);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            sendPaymentRequest(paymentData);
        });

        // Modifikasi untuk menampilkan opsi Edit dan Hapus hanya jika user adalah admin
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
                        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
                        confirmBuilder.setTitle("Konfirmasi Hapus");
                        confirmBuilder.setMessage("Apakah Anda yakin ingin menghapus donasi ini?");
                        confirmBuilder.setPositiveButton("Ya", (confirmDialog, confirmWhich) -> {
                            DatabaseHelper dbHelper = new DatabaseHelper(context);
                            boolean deleted = dbHelper.hapusDonasi(donasi.getId());
                            if (deleted) {
                                Toast.makeText(context, "Donasi berhasil dihapus", Toast.LENGTH_SHORT).show();
                                donasiList.remove(donasi);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, "Gagal menghapus donasi", Toast.LENGTH_SHORT).show();
                            }
                        });
                        confirmBuilder.setNegativeButton("Tidak", null);
                        confirmBuilder.show();
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

    private void sendPaymentRequest(JSONObject paymentData) {
        String url = "https://merchantserver-mobile-d42ad1760920.herokuapp.com/api/charge";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, paymentData,
                response -> {
                    try {
                        String redirectUrl = response.getString("redirect_url");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
                        context.startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount);
    }
}

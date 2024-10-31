package com.example.polinelapeduli;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "polinela_peduli.db";
    private static final int DATABASE_VERSION = 2; // Hanya ada satu versi database

    private static final String TABLE_DONASI = "donasi";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAMA = "nama";
    public static final String COLUMN_DESKRIPSI = "deskripsi";
    private static final String COLUMN_KATEGORI = "kategori";
    public static final String COLUMN_TARGET = "target";
    public static final String COLUMN_GAMBAR = "gambar";
    public static final String COLUMN_JUMLAH_DONASI = "jumlah_donasi";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_DONASI = "CREATE TABLE " + TABLE_DONASI + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAMA + " TEXT, "
                + COLUMN_DESKRIPSI + " TEXT, "
                + COLUMN_KATEGORI + " TEXT, "
                + COLUMN_TARGET + " INTEGER, "
                + COLUMN_GAMBAR + " TEXT, "
                + COLUMN_JUMLAH_DONASI + " INTEGER DEFAULT 0)"; // Tambahkan kolom jumlah_donasi dengan default
        db.execSQL(CREATE_TABLE_DONASI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_DONASI + " ADD COLUMN " + COLUMN_JUMLAH_DONASI + " INTEGER DEFAULT 0");
        }
    }

    public boolean tambahDonasi(String nama, String deskripsi, String kategori, int target, String gambar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, nama);
        values.put(COLUMN_DESKRIPSI, deskripsi);
        values.put(COLUMN_KATEGORI, kategori);
        values.put(COLUMN_TARGET, target);
        values.put(COLUMN_GAMBAR, gambar); // Simpan path gambar
        values.put(COLUMN_JUMLAH_DONASI, 0); // Inisialisasi jumlah_donasi dengan 0

        long result = db.insert(TABLE_DONASI, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateDonasi(int id, String nama, String deskripsi, String kategori, int target, String gambar, int jumlahDonasi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, nama);
        values.put(COLUMN_DESKRIPSI, deskripsi);
        values.put(COLUMN_KATEGORI, kategori);
        values.put(COLUMN_TARGET, target);
        values.put(COLUMN_GAMBAR, gambar);
        values.put(COLUMN_JUMLAH_DONASI, jumlahDonasi);

        int result = db.update(TABLE_DONASI, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close(); // Pastikan untuk menutup database setelah update
        return result > 0;
    }

    public boolean hapusDonasi(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DONASI, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public Cursor getAllDonasi() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DONASI, null);
    }

    public Cursor getDonasiById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DONASI + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getDonasiByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DONASI + " WHERE email = ?", new String[]{email});
    }


    public String[] getDonasiDataById(int id) {
        String[] donasiData = null;
        Cursor cursor = null;
        try {
            cursor = getDonasiById(id);
            if (cursor != null && cursor.moveToFirst()) {
                donasiData = new String[]{
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESKRIPSI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KATEGORI)),
                        String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TARGET))), // Pastikan konversi tipe data
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR))
                };
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Menutup cursor di akhir
            }
        }
        return donasiData;
    }
}

package com.example.polinelapeduli;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "polinela_peduli.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_DONASI = "donasi";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAMA = "nama";
    private static final String COLUMN_DESKRIPSI = "deskripsi";
    private static final String COLUMN_KATEGORI = "kategori";
    private static final String COLUMN_TARGET = "target";
    private static final String COLUMN_GAMBAR = "gambar";

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
                + COLUMN_GAMBAR + " TEXT)"; // Ubah BLOB ke TEXT
        db.execSQL(CREATE_TABLE_DONASI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONASI);
        onCreate(db);
    }

    public boolean tambahDonasi(String nama, String deskripsi, String kategori, int target, String gambar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, nama);
        values.put(COLUMN_DESKRIPSI, deskripsi);
        values.put(COLUMN_KATEGORI, kategori);
        values.put(COLUMN_TARGET, target);
        values.put(COLUMN_GAMBAR, gambar); // Simpan path gambar

        long result = db.insert(TABLE_DONASI, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateDonasi(int id, String nama, String deskripsi, String kategori, int target, String gambar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, nama);
        values.put(COLUMN_DESKRIPSI, deskripsi);
        values.put(COLUMN_KATEGORI, kategori);
        values.put(COLUMN_TARGET, target);
        values.put(COLUMN_GAMBAR, gambar); // Memperbarui path gambar

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
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TARGET)),
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

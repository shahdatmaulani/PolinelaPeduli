package com.example.polinelapeduli;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "polinela_peduli.db";
    private static final int DATABASE_VERSION = 3; // Versi database terbaru

    // Tabel donasi
    public static final String TABLE_DONASI = "donasi";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAMA = "nama";
    public static final String COLUMN_DESKRIPSI = "deskripsi";
    public static final String COLUMN_KATEGORI = "kategori";
    public static final String COLUMN_TARGET = "target";
    public static final String COLUMN_GAMBAR = "gambar";
    public static final String COLUMN_JUMLAH_DONASI = "jumlah_donasi";

    // Tabel users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel donasi
        String CREATE_TABLE_DONASI = "CREATE TABLE " + TABLE_DONASI + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAMA + " TEXT, "
                + COLUMN_DESKRIPSI + " TEXT, "
                + COLUMN_KATEGORI + " TEXT, "
                + COLUMN_TARGET + " INTEGER, "
                + COLUMN_GAMBAR + " TEXT, "
                + COLUMN_JUMLAH_DONASI + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE_DONASI);

        // Membuat tabel users
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " TEXT PRIMARY KEY, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_DONASI + " ADD COLUMN " + COLUMN_JUMLAH_DONASI + " INTEGER DEFAULT 0");
        }
        if (oldVersion < 3) {
            String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
                    + COLUMN_USER_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_EMAIL + " TEXT, "
                    + COLUMN_PASSWORD + " TEXT)";
            db.execSQL(CREATE_TABLE_USERS);
        }
    }

    // Metode CRUD untuk tabel donasi
    public boolean tambahDonasi(String nama, String deskripsi, String kategori, int target, String gambar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, nama);
        values.put(COLUMN_DESKRIPSI, deskripsi);
        values.put(COLUMN_KATEGORI, kategori);
        values.put(COLUMN_TARGET, target);
        values.put(COLUMN_GAMBAR, gambar);
        values.put(COLUMN_JUMLAH_DONASI, 0);

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
        db.close();
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

    // Metode CRUD untuk tabel users
    public boolean tambahUser(String userId, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USER_ID + "=?", new String[]{userId}, null, null, null);
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
    }

    public boolean updateUser(String userId, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        int result = db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{userId});
        db.close();
        return result > 0;
    }

    public boolean hapusUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, COLUMN_USER_ID + "=?", new String[]{userId});
        db.close();
        return result > 0;
    }
}
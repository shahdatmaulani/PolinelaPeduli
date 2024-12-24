package com.example.polinelapeduli.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.example.polinelapeduli.utils.CurrentTime;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "polinela_peduli.db";
    private static final int DATABASE_VERSION = 3; // Versi database terbaru

    //User
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FULLNAME = "fullname";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_LOGIN_METHOD = "login_method";
    public static final String COLUMN_PROFILE_PICTURE = "profile_picture";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // Donation
    public static final String TABLE_DONATIONS = "donations";
    public static final String COLUMN_DONATION_ID = "donation_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TARGET = "target";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_STATUS = "status";

    // Category
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORY_NAME = "name";

    // Transaction
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_TRANSACTION_ID = "transaction_id";
    public static final String COLUMN_AMOUNT = "amount";

    // Payment
    public static final String TABLE_PAYMENTS = "payments";
    public static final String COLUMN_PAYMENT_ID = "payment_id";
    public static final String COLUMN_METHOD = "method";
    public static final String COLUMN_PAID_AT = "paid_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabel users
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FULLNAME + " TEXT NOT NULL, "
                + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COLUMN_LOGIN_METHOD + " TEXT CHECK(" + COLUMN_LOGIN_METHOD + " IN ('EMAIL', 'GOOGLE')) NOT NULL, "
                + COLUMN_ROLE + " TEXT CHECK(" + COLUMN_ROLE + " IN ('ADMIN', 'USER')) NOT NULL, "
                + COLUMN_PROFILE_PICTURE + " TEXT, "
                + COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1 CHECK(" + COLUMN_IS_ACTIVE + " IN (0,1)), "
                + COLUMN_CREATED_AT + " TEXT, "
                + COLUMN_UPDATED_AT + " TEXT)";
        db.execSQL(CREATE_TABLE_USERS);

        // Tabel categories
        String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + " ("
                + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CATEGORY_NAME + " TEXT UNIQUE NOT NULL)";
        db.execSQL(CREATE_TABLE_CATEGORIES);

        // Tabel donations
        String CREATE_TABLE_DONATIONS = "CREATE TABLE " + TABLE_DONATIONS + " ("
                + COLUMN_DONATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + COLUMN_CATEGORY_ID + " INTEGER NOT NULL, "
                + COLUMN_TARGET + " INTEGER NOT NULL, "
                + COLUMN_IMAGE + " TEXT NOT NULL, "
                + COLUMN_STATUS + " TEXT CHECK(" + COLUMN_STATUS + " IN ('AKTIF', 'KOMPLET', 'DITUTUP')) NOT NULL, "
                + COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1 CHECK(" + COLUMN_IS_ACTIVE + " IN (0,1)), "
                + COLUMN_CREATED_AT + " TEXT, "
                + COLUMN_UPDATED_AT + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + "))";
        db.execSQL(CREATE_TABLE_DONATIONS);

        // Tabel transactions
        String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER NOT NULL, "
                + COLUMN_DONATION_ID + " INTEGER NOT NULL, "
                + COLUMN_AMOUNT + " INTEGER NOT NULL, "
                + COLUMN_CREATED_AT + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_DONATION_ID + ") REFERENCES " + TABLE_DONATIONS + "(" + COLUMN_DONATION_ID + "))";
        db.execSQL(CREATE_TABLE_TRANSACTIONS);

        // Tabel payments
        String CREATE_TABLE_PAYMENTS = "CREATE TABLE " + TABLE_PAYMENTS + " ("
                + COLUMN_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TRANSACTION_ID + " INTEGER UNIQUE NOT NULL, "
                + COLUMN_AMOUNT + " INTEGER NOT NULL, "
                + COLUMN_METHOD + " TEXT NOT NULL, "
                + COLUMN_PAID_AT + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_TRANSACTION_ID + ") REFERENCES " + TABLE_TRANSACTIONS + "(" + COLUMN_TRANSACTION_ID + "))";
        db.execSQL(CREATE_TABLE_PAYMENTS);

        //Category Manual Input
        String[] categories = {"Bencana", "Kesehatan", "Kemanusiaan", "Pendidikan"};

        for (String category : categories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, category);
            db.insert(TABLE_CATEGORIES, null, values);
        }

        //Input Data Admin
        ContentValues adminValues = getContentValues(CurrentTime.getCurrentTime());
        db.insert(TABLE_USERS, null, adminValues);

    }

    @NonNull
    private static ContentValues getContentValues(String currentTime) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_FULLNAME, "Admin Polinela Peduli");
        adminValues.put(COLUMN_EMAIL, "adminpolinelapeduli@gmail.com");
        adminValues.put(COLUMN_LOGIN_METHOD, "EMAIL");
        adminValues.put(COLUMN_ROLE, "ADMIN");
        adminValues.put(COLUMN_PROFILE_PICTURE, "");
        adminValues.put(COLUMN_IS_ACTIVE, 1);
        adminValues.put(COLUMN_CREATED_AT, currentTime);
        adminValues.put(COLUMN_UPDATED_AT, currentTime);
        return adminValues;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }
}
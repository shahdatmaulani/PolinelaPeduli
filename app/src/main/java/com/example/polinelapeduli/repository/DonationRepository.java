package com.example.polinelapeduli.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.polinelapeduli.model.Donation;
import com.example.polinelapeduli.utils.Enum.EStatus;

import java.util.ArrayList;
import java.util.List;

public class DonationRepository {

    private static final String TAG = "DonationRepository";
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DonationRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private void openDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    // Insert Donation
    public boolean insertDonation(Donation donation) {
        openDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, donation.getName());
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, donation.getDescription());
            values.put(DatabaseHelper.COLUMN_CATEGORY_ID, donation.getCategoryId());
            values.put(DatabaseHelper.COLUMN_TARGET, donation.getTarget());
            values.put(DatabaseHelper.COLUMN_IMAGE, donation.getImage());
            values.put(DatabaseHelper.COLUMN_STATUS, donation.getStatus().toString());
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, donation.isActive() ? 1 : 0);
            values.put(DatabaseHelper.COLUMN_CREATED_AT, donation.getCreatedAt());
            values.put(DatabaseHelper.COLUMN_UPDATED_AT, donation.getUpdatedAt());

            long result = database.insert(DatabaseHelper.TABLE_DONATIONS, null, values);
            if (result != -1) {
                database.setTransactionSuccessful();
                Log.i(TAG, "Donation inserted successfully: " + donation);
                return true;
            } else {
                Log.w(TAG, "Failed to insert donation.");
                return false;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting donation: ", e);
            return false;
        } finally {
            database.endTransaction();
        }
    }

    // Get All Donations
    public List<Donation> getAllDonations() {
        openDatabase();
        List<Donation> donations = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_DONATIONS + " WHERE " + DatabaseHelper.COLUMN_IS_ACTIVE + " = 1";
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    donations.add(mapCursorToDonation(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching donations: ", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return donations;
    }

    // Get All Donations with Category (NEW)
    public List<Donation> getAllDonationsWithCategory() {
        openDatabase();
        List<Donation> donations = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT d.*, c." + DatabaseHelper.COLUMN_CATEGORY_NAME +
                    " FROM " + DatabaseHelper.TABLE_DONATIONS + " d" +
                    " LEFT JOIN " + DatabaseHelper.TABLE_CATEGORIES + " c" +
                    " ON d." + DatabaseHelper.COLUMN_CATEGORY_ID + " = c." + DatabaseHelper.COLUMN_CATEGORY_ID +
                    " WHERE d." + DatabaseHelper.COLUMN_IS_ACTIVE + " = 1";

            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    donations.add(mapCursorToDonationWithCategory(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching donations with category: ", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return donations;
    }

    // Get Donation by ID
    public Donation getDonationById(int donationId) {
        openDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_DONATIONS + " WHERE " + DatabaseHelper.COLUMN_DONATION_ID + " = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(donationId)});

            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToDonation(cursor);
            } else {
                Log.w(TAG, "No donation found with ID: " + donationId);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching donation with ID: " + donationId, e);
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Update Donation
    public boolean updateDonation(Donation donation) {
        openDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, donation.getName());
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, donation.getDescription());
            values.put(DatabaseHelper.COLUMN_CATEGORY_ID, donation.getCategoryId());
            values.put(DatabaseHelper.COLUMN_TARGET, donation.getTarget());
            values.put(DatabaseHelper.COLUMN_IMAGE, donation.getImage());
            values.put(DatabaseHelper.COLUMN_STATUS, donation.getStatus().toString());
            values.put(DatabaseHelper.COLUMN_UPDATED_AT, donation.getUpdatedAt());

            int rowsAffected = database.update(DatabaseHelper.TABLE_DONATIONS, values, DatabaseHelper.COLUMN_DONATION_ID + " = ?", new String[]{String.valueOf(donation.getDonationId())});
            if (rowsAffected > 0) {
                database.setTransactionSuccessful();
                Log.i(TAG, "Donation updated successfully: ID " + donation);
                return true;
            } else {
                Log.w(TAG, "No donation found with ID: " + donation.getDonationId());
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating donation with ID: " + donation.getDonationId(), e);
            return false;
        } finally {
            database.endTransaction();
        }
    }

    // Soft Delete Donation
    public boolean softDeleteDonation(int donationId) {
        openDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, 0);

            int rowsAffected = database.update(DatabaseHelper.TABLE_DONATIONS, values, DatabaseHelper.COLUMN_DONATION_ID + " = ?", new String[]{String.valueOf(donationId)});
            if (rowsAffected > 0) {
                database.setTransactionSuccessful();
                Log.i(TAG, "Donation with ID " + donationId + " successfully soft deleted.");
                return true;
            } else {
                Log.w(TAG, "No donation found with ID: " + donationId);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error soft deleting donation with ID: " + donationId, e);
            return false;
        } finally {
            database.endTransaction();
        }
    }

    // Helper: Map Cursor to Donation
    private Donation mapCursorToDonation(Cursor cursor) {
        return new Donation(
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DONATION_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE)),
                EStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS))),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_ACTIVE)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT))
        );
    }

    // Helper: Map Cursor to Donation with Category (NEW)
    private Donation mapCursorToDonationWithCategory(Cursor cursor) {
        Donation donation = mapCursorToDonation(cursor);

        // Tambahkan nama kategori
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
        donation.setCategoryName(categoryName);

        return donation;
    }
}

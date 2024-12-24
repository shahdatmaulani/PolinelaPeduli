package com.example.polinelapeduli.repository;

import static com.example.polinelapeduli.repository.DatabaseHelper.COLUMN_EMAIL;
import static com.example.polinelapeduli.repository.DatabaseHelper.COLUMN_ROLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.polinelapeduli.model.User;
import com.example.polinelapeduli.utils.Enum.ELoginMethod;
import com.example.polinelapeduli.utils.Enum.ERole;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private final DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private User mapCursorToUser(Cursor cursor) {
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULLNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                ELoginMethod.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOGIN_METHOD))),
                ERole.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_PICTURE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_ACTIVE)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT))
        );
    }

    public boolean insertUser(User user) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_FULLNAME, user.getFullName());
            values.put(COLUMN_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_LOGIN_METHOD, user.getLoginMethod().toString());
            values.put(COLUMN_ROLE, user.getRole().toString());
            values.put(DatabaseHelper.COLUMN_PROFILE_PICTURE, user.getProfilePicture());
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, user.isActive() ? 1 : 0);
            values.put(DatabaseHelper.COLUMN_CREATED_AT, user.getCreatedAt());
            values.put(DatabaseHelper.COLUMN_UPDATED_AT, user.getUpdatedAt());

            long result = database.insert(DatabaseHelper.TABLE_USERS, null, values);
            return result != -1;
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting user: ", e);
            return false;
        }
    }

    public User getUserById(int userId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE " + DatabaseHelper.COLUMN_USER_ID + " = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToUser(cursor);
            } else {
                Log.w(TAG, "No user found with ID: " + userId);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user with ID: " + userId, e);
            return null;
        } finally {
            if (cursor != null) cursor.close();
            database.close();
        }
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE " + DatabaseHelper.COLUMN_EMAIL + " = ?";
            cursor = database.rawQuery(query, new String[]{email});

            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToUser(cursor);
            } else {
                Log.w(TAG, "No user found with email: " + email);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user with email: " + email, e);
            return null;
        } finally {
            if (cursor != null) cursor.close();
            database.close();
        }
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE " + DatabaseHelper.COLUMN_IS_ACTIVE + " = 1";
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    userList.add(mapCursorToUser(cursor));
                } while (cursor.moveToNext());
            } else {
                Log.w(TAG, "No active users found.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching all users: ", e);
        } finally {
            if (cursor != null) cursor.close();
            database.close();
        }
        return userList;
    }

    public void updateUser(User user) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_FULLNAME, user.getFullName());
            values.put(COLUMN_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_LOGIN_METHOD, user.getLoginMethod().toString());
            values.put(DatabaseHelper.COLUMN_PROFILE_PICTURE, user.getProfilePicture());
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, user.isActive() ? 1 : 0);
            values.put(DatabaseHelper.COLUMN_UPDATED_AT, user.getUpdatedAt());

            int rowsAffected = database.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getUserId())});
        } catch (Exception e) {
            Log.e(TAG, "Error updating user with ID: " + user.getUserId(), e);
        }
    }

    public boolean softDeleteUser(int userId) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, 0); // Set isActive ke 0 (false)

            int rowsAffected = database.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error soft deleting user with ID: " + userId, e);
            return false;
        }
    }
}

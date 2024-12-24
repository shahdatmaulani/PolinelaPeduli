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

    /**
     * Maps a Cursor to a User object.
     *
     * @param cursor The database cursor.
     * @return The mapped User object.
     */
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

    /**
     * Inserts a new user into the database.
     *
     * @param user The user object to insert.
     * @return True if insertion is successful, false otherwise.
     */
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

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user.
     * @return The User object, or null if not found.
     */
    public User getUserById(int userId) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE " + DatabaseHelper.COLUMN_USER_ID + " = ?";
        try (SQLiteDatabase database = dbHelper.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)})) {

            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToUser(cursor);
            } else {
                Log.w(TAG, "No user found with ID: " + userId);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user with ID: " + userId, e);
            return null;
        }
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address of the user.
     * @return The User object, or null if not found.
     */
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE " + DatabaseHelper.COLUMN_EMAIL + " = ?";
        try (SQLiteDatabase database = dbHelper.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, new String[]{email})) {

            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToUser(cursor);
            } else {
                Log.w(TAG, "No user found with email: " + email);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user with email: " + email, e);
            return null;
        }
    }

    /**
     * Retrieves all active users from the database.
     *
     * @return A list of active users.
     */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE " + DatabaseHelper.COLUMN_IS_ACTIVE + " = 1";
        try (SQLiteDatabase database = dbHelper.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, null)) {

            if (cursor.moveToFirst()) {
                do {
                    userList.add(mapCursorToUser(cursor));
                } while (cursor.moveToNext());
            } else {
                Log.w(TAG, "No active users found.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching all users: ", e);
        }
        return userList;
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user The updated user object.
     */
    public void updateUser(User user) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_FULLNAME, user.getFullName());
            values.put(COLUMN_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_LOGIN_METHOD, user.getLoginMethod().toString());
            values.put(DatabaseHelper.COLUMN_PROFILE_PICTURE, user.getProfilePicture());
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, user.isActive() ? 1 : 0);
            values.put(DatabaseHelper.COLUMN_UPDATED_AT, user.getUpdatedAt());

            database.update(DatabaseHelper.TABLE_USERS, values,
                    DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(user.getUserId())});
        } catch (Exception e) {
            Log.e(TAG, "Error updating user with ID: " + user.getUserId(), e);
        }
    }

    /**
     * Performs a soft delete on a user.
     *
     * @param userId The ID of the user to delete.
     * @return True if the deletion is successful, false otherwise.
     */
    public boolean softDeleteUser(int userId) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, 0);

            int rowsAffected = database.update(DatabaseHelper.TABLE_USERS, values,
                    DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error soft deleting user with ID: " + userId, e);
            return false;
        }
    }
}

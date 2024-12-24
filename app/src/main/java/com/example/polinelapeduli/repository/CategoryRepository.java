package com.example.polinelapeduli.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.polinelapeduli.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private static final String TAG = "CategoryRepository";
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public CategoryRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    private void openDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    // Insert Category
    public boolean insertCategory(String categoryName) {
        openDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, categoryName);
            long result = database.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);

            if (result != -1) {
                database.setTransactionSuccessful();
                Log.i(TAG, "Category inserted successfully: " + categoryName);
                return true;
            } else {
                Log.w(TAG, "Failed to insert category: " + categoryName);
                return false;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting category: ", e);
            return false;
        } finally {
            database.endTransaction();
        }
    }

    // Get All Categories
    public List<String> getAllCategories() {
        openDatabase();
        List<String> categories = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES;
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    categories.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching categories: ", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return categories;
    }

    public List<Category> getAllCategoryObjects() {
        openDatabase();
        List<Category> categories = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES;
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                    categories.add(new Category(id, name));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching categories: ", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return categories;
    }


    // Get Category by Id
    public String getCategoryById(int categoryId) {
        openDatabase();
        Cursor cursor = null;
        String categoryName = null;
        try {
            String query = "SELECT " + DatabaseHelper.COLUMN_CATEGORY_NAME + " FROM " + DatabaseHelper.TABLE_CATEGORIES +
                    " WHERE " + DatabaseHelper.COLUMN_CATEGORY_ID + " = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(categoryId)});

            if (cursor != null && cursor.moveToFirst()) {
                categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
            } else {
                Log.w(TAG, "No category found with ID: " + categoryId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching category by ID: ", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return categoryName;
    }

    // Update Category
    public boolean updateCategory(int categoryId, String newCategoryName) {
        openDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, newCategoryName);
            int rowsAffected = database.update(DatabaseHelper.TABLE_CATEGORIES, values, DatabaseHelper.COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});

            if (rowsAffected > 0) {
                database.setTransactionSuccessful();
                Log.i(TAG, "Category updated successfully: ID " + categoryId);
                return true;
            } else {
                Log.w(TAG, "No category found with ID: " + categoryId);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating category with ID: " + categoryId, e);
            return false;
        } finally {
            database.endTransaction();
        }
    }

    // Delete Category
    public boolean deleteCategory(int categoryId) {
        openDatabase();
        database.beginTransaction();
        try {
            int rowsAffected = database.delete(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});

            if (rowsAffected > 0) {
                database.setTransactionSuccessful();
                Log.i(TAG, "Category deleted successfully: ID " + categoryId);
                return true;
            } else {
                Log.w(TAG, "No category found with ID: " + categoryId);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category with ID: " + categoryId, e);
            return false;
        } finally {
            database.endTransaction();
        }
    }
}

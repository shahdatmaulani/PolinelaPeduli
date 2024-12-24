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

    public CategoryRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Inserts a new category into the database.
     *
     * @param categoryName The name of the category to insert.
     * @return True if insertion is successful, false otherwise.
     */
    public boolean insertCategory(String categoryName) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, categoryName);
            long result = database.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);

            if (result != -1) {
                Log.i(TAG, "Category inserted successfully: " + categoryName);
                return true;
            } else {
                Log.w(TAG, "Failed to insert category: " + categoryName);
                return false;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting category: ", e);
            return false;
        }
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return A list of categories.
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES;
        try (SQLiteDatabase database = dbHelper.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, null)) {

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                    categories.add(new Category(id, name));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching categories: ", e);
        }
        return categories;
    }

    /**
     * Retrieves the name of a category by its ID.
     *
     * @param categoryId The ID of the category.
     * @return The name of the category, or null if not found.
     */
    public String getCategoryById(int categoryId) {
        String query = "SELECT " + DatabaseHelper.COLUMN_CATEGORY_NAME +
                " FROM " + DatabaseHelper.TABLE_CATEGORIES +
                " WHERE " + DatabaseHelper.COLUMN_CATEGORY_ID + " = ?";
        try (SQLiteDatabase database = dbHelper.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(categoryId)})) {

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
            } else {
                Log.w(TAG, "No category found with ID: " + categoryId);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching category by ID: ", e);
            return null;
        }
    }

    /**
     * Updates an existing category in the database.
     *
     * @param categoryId      The ID of the category to update.
     * @param newCategoryName The new name for the category.
     * @return True if the update is successful, false otherwise.
     */
    public boolean updateCategory(int categoryId, String newCategoryName) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, newCategoryName);
            int rowsAffected = database.update(DatabaseHelper.TABLE_CATEGORIES, values,
                    DatabaseHelper.COLUMN_CATEGORY_ID + " = ?",
                    new String[]{String.valueOf(categoryId)});

            if (rowsAffected > 0) {
                Log.i(TAG, "Category updated successfully: ID " + categoryId);
                return true;
            } else {
                Log.w(TAG, "No category found with ID: " + categoryId);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating category with ID: " + categoryId, e);
            return false;
        }
    }

    /**
     * Deletes a category from the database.
     *
     * @param categoryId The ID of the category to delete.
     * @return True if the deletion is successful, false otherwise.
     */
    public boolean deleteCategory(int categoryId) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            int rowsAffected = database.delete(DatabaseHelper.TABLE_CATEGORIES,
                    DatabaseHelper.COLUMN_CATEGORY_ID + " = ?",
                    new String[]{String.valueOf(categoryId)});

            if (rowsAffected > 0) {
                Log.i(TAG, "Category deleted successfully: ID " + categoryId);
                return true;
            } else {
                Log.w(TAG, "No category found with ID: " + categoryId);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category with ID: " + categoryId, e);
            return false;
        }
    }
}

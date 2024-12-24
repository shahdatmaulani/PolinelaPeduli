package com.example.polinelapeduli.utils;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

public class InputValidator {

    public static String getValidatedText(EditText editText, String errorMessage) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            setErrorAndFocus(editText, errorMessage);
            return null;
        }
        return text;
    }

    public static Integer getValidatedNumberWithMinValue(EditText editText, String emptyErrorMessage, String minErrorMessage, int minValue) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            setErrorAndFocus(editText, emptyErrorMessage);
            return null;
        }
        try {
            int value = Integer.parseInt(text);
            if (value < minValue) {
                setErrorAndFocus(editText, minErrorMessage);
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            setErrorAndFocus(editText, "Input must be a valid number");
            return null;
        }
    }

    public static String getValidatedEmail(EditText editText) {
        String email = editText.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setErrorAndFocus(editText, "Valid email is required");
            return null;
        }
        return email;
    }

    public static boolean validatePassword(EditText editText) {
        String password = editText.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            setErrorAndFocus(editText, "Password must be at least 6 characters");
            return true;
        }
        return false;
    }

    public static boolean validateConfirmPassword(EditText editText, EditText etPassword) {
        String password = etPassword.getText().toString().trim();
        String confirmPassword = editText.getText().toString().trim();
        if (!password.equals(confirmPassword)) {
            setErrorAndFocus(editText, "Passwords do not match");
            return false;
        }
        return true;
    }

    private static void setErrorAndFocus(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        editText.requestFocus();
    }
}

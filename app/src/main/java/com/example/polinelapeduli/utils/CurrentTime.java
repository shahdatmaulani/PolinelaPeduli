package com.example.polinelapeduli.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CurrentTime {
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(System.currentTimeMillis());
    }
}

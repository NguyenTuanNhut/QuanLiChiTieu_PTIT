package com.example.finalpj.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat MONTH_FORMAT =
        new SimpleDateFormat("MM/yyyy", Locale.getDefault());

    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatMonth(long timestamp) {
        return MONTH_FORMAT.format(new Date(timestamp));
    }

    public static String getMonthYear(int month, int year) {
        return String.format(Locale.getDefault(), "%02d-%d", month, year);
    }
}

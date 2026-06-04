package com.example.finalpj.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {
    private static final NumberFormat FORMATTER =
        NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public static String format(double amount) {
        return FORMATTER.format((long) amount) + " ₫";
    }

    public static String formatWithSign(double amount) {
        String sign = amount >= 0 ? "+" : "";
        return sign + format(amount);
    }
}

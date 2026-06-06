package com.example.finalpj.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Lớp tiện ích dùng để xử lý và định dạng thời gian.
 */
public class DateUtils {
    // Định dạng ngày/tháng/năm (vd: 25/12/2024)
    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        
    // Định dạng tháng/năm (vd: 12/2024)
    private static final SimpleDateFormat MONTH_FORMAT =
        new SimpleDateFormat("MM/yyyy", Locale.getDefault());

    /**
     * Chuyển đổi timestamp (miliseconds) sang chuỗi ngày hiển thị.
     */
    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    /**
     * Chuyển đổi timestamp sang chuỗi tháng/năm.
     */
    public static String formatMonth(long timestamp) {
        return MONTH_FORMAT.format(new Date(timestamp));
    }

    /**
     * Trả về chuỗi định dạng MM-YYYY dùng cho các câu truy vấn Database.
     */
    public static String getMonthYear(int month, int year) {
        return String.format(Locale.getDefault(), "%02d-%d", month, year);
    }
}

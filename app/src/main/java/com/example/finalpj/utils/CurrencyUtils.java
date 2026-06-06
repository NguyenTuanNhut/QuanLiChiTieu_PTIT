package com.example.finalpj.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Lớp tiện ích dùng để định dạng tiền tệ.
 */
public class CurrencyUtils {
    // Sử dụng Locale Việt Nam để hiển thị dấu phân cách hàng nghìn chuẩn (vd: 1.000.000)
    private static final NumberFormat FORMATTER =
        NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    /**
     * Định dạng số thực thành chuỗi tiền tệ VNĐ.
     * @param amount Số tiền (kiểu double)
     * @return Chuỗi đã định dạng kèm đơn vị ₫ (vd: 50.000 ₫)
     */
    public static String format(double amount) {
        return FORMATTER.format((long) amount) + " ₫";
    }

    /**
     * Định dạng kèm dấu (+/-).
     */
    public static String formatWithSign(double amount) {
        String sign = amount >= 0 ? "+" : "";
        return sign + format(amount);
    }
}

package com.example.finalpj.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SearchUtils {
    public static String removeAccents(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll("đ", "d");
    }

    public static boolean matches(String target, String query) {
        if (target == null || query == null) return false;
        String normalizedTarget = removeAccents(target);
        String normalizedQuery = removeAccents(query);
        return normalizedTarget.contains(normalizedQuery);
    }
}

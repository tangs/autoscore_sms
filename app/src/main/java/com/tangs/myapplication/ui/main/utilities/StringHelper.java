package com.tangs.myapplication.ui.main.utilities;

public class StringHelper {
    public static boolean equals(String str1, String str2) {
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }

    public static boolean checkNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean checksNullOrEmpty(String ...strings) {
        for (String str: strings) {
            if (checkNullOrEmpty(str)) return true;
        }
        return false;
    }
}

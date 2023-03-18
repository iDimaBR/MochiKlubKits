package com.github.idimabr.mochiklubkits.util;

public class MoneyUtil {

    private static final String[] SUFFIXES = {"K", "M", "B", "T", "Q", "Qq", "Sx", "Sp", "Oc", "N", "D", "Un", "DD"};

    public static String format(double number) {
        StringBuilder builder = new StringBuilder();
        int suffix = -1;
        if (number >= 1000) {
            double n = number - number % 1;
            long length = length(n);

            long p = length%3;
            if (p == 0) p = 3;

            number = n / Math.pow(10, length-p);
            suffix = (int)((length - 1) / 3 - 1);
        }

        int base = (int) number;
        int decimals = (int)((number - base)*100);

        builder.append(base).append(".");
        if (decimals < 10) builder.append("0");
        builder.append(decimals);
        if (suffix != -1 && suffix < SUFFIXES.length) builder.append(SUFFIXES[suffix]);
        return builder.toString();
    }

    private static long length(double number) {
        for (long i = 0;;i++) {
            if (number / Math.pow(10, i) < 10) return i+1;
        }
    }

    public static boolean isInvalid(double value) {
        return (value < 0.0D || Double.isNaN(value) || Double.isInfinite(value));
    }

    public static boolean isNumber(String str) {
        return str != null && str.matches("[0-9]*");
    }
}

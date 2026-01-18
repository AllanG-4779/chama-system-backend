package com.allang.chamasystem.utils;

public class GeneralUtils {

    public static String generateFTP(int length){
        StringBuilder ftp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = (int) (Math.random() * 10);
            ftp.append(digit);
        }
        return ftp.toString();

    }
}

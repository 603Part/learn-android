package com.mlearn.Encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {
    public static String getSecretCode(String number, String password) {
        MessageDigest encryp = null;
        try {
            encryp = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String secret = "";
        byte[] key = number.getBytes();
        byte[] buffer = encryp.digest(password.getBytes());
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (buffer[i] ^ key[i % key.length]);
            buffer[i] = (byte) (buffer[i] ^ key[key.length - 1]);
            String hex = Integer.toHexString(0xff & buffer[i]);
            if (hex.length() == 1) {
                secret += "0";
            }
            secret += hex;
        }
        return secret;
    }

    public static boolean isEquals(String number, String password, String secretcode) {
        return secretcode.equals(Encrypt.getSecretCode(number, password));
    }

    public static void main(String[] args) {
        System.out.println(Encrypt.getSecretCode("153329", "123"));
    }
}

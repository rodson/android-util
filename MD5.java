package com.cvte.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final String TAG = "MD5";

    public static boolean checkMD5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Log.e(TAG, "MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        Log.v(TAG, "Calculated digest: " + calculatedDigest);
        Log.v(TAG, "Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    public static String calculateMD5(String content) {
        if (content == null) {
            return null;
        }

        try {
            byte[] contentByteArray = content.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(contentByteArray);
            byte[] resultByteArray = messageDigest.digest();
            StringBuffer result = new StringBuffer();
            int length = resultByteArray.length;
            for (int i = 0; i < length; i++) {
                result.append(
                        String.format("%02X", new Object[] { Byte.valueOf(resultByteArray[i]) }));
            }

            return result.toString();
        } catch (Exception e) {
            LogUtil.e("Calculate md5 error");
        }

        return content.replaceAll("[^[a-z][A-Z][0-9][.][_]]", "");
    }

    public static String calculateMD5Hex(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(content.getBytes());
            byte[] md5Byte = messageDigest.digest();

            StringBuffer resultBuffer = new StringBuffer();
            for (int i = 0; i < md5Byte.length; i++) {
                int j = 0xFF & md5Byte[i];
                resultBuffer.append(Integer.toHexString(j));
            }
            String result = resultBuffer.toString();

            // Make sure the length of the digest is 32
            int length = result.length();
            if (length < 32) {
                for (int i = 0; i < 32 - length; i++) {
                    result = result + "f";
                }
            } else if (length > 32) {
                result = result.substring(0, 32);
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e("Get MD5 error");
        }

        return "";
    }
}

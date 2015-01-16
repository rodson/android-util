package com.cvte.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * @description: Api for io operations.
 * @author: Saul
 * @date: 14-11-26
 * @version: 1.0
 */
public class IOUtil {

    public static String readStream(InputStream inputStream) {
        final char[] buffer = new char[1024];
        final StringBuilder out = new StringBuilder();
        try {
            final Reader reader = new InputStreamReader(inputStream, "UTF-8");
            try {
                for (;;) {
                    int rsz = reader.read(buffer, 0, buffer.length);
                    if (rsz < 0) {
                        break;
                    }
                    out.append(buffer, 0, rsz);
                }
            } finally {
                reader.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }
}

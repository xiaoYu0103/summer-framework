package org.yxw.utils;

import org.yxw.io.InputStreamCallback;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class ClassPathUtils {

    public static <T> T readInputStream(String path, InputStreamCallback<T> inputStreamCallback) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        try (InputStream input = getContextClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new FileNotFoundException("File not found in classpath: " + path);
            }
            return inputStreamCallback.doWithInputStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    public static String readString(String path) {
        return readInputStream(path, (input) -> {
            byte[] data = readAllBytes(input);
            return new String(data, StandardCharsets.UTF_8);
        });
    }

    public static ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassPathUtils.class.getClassLoader();
        }
        return cl;
    }

    public static byte[] readAllBytes(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096]; // 使用4KB的缓冲区

        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }
}
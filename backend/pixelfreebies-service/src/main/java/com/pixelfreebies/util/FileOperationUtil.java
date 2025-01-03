package com.pixelfreebies.util;

import java.io.IOException;
import java.io.InputStream;

public class FileOperationUtil {

    public static long getFileSize(InputStream inputStream) throws IOException {
        long size = 0;
        byte[] buffer = new byte[1024]; // 1KB buffer
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            size += bytesRead;
        }

        return size;
    }

}

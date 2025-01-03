package com.pixelfreebies.service.image.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessingService {

    public void convertImageInChunks(InputStream inputStream, OutputStream outputStream, String targetFormat) throws IOException {
        Mat image = null;
        try (ByteArrayOutputStream tempStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                log.debug("Reading chunk of size: {}", bytesRead);
                tempStream.write(buffer, 0, bytesRead);
            }
            log.debug("Chunk processed successfully. Chunk size: {}", tempStream.size());

            image = Imgcodecs.imdecode(new MatOfByte(tempStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
            MatOfByte resultBuffer = new MatOfByte();
            Imgcodecs.imencode("." + targetFormat, image, resultBuffer);
            outputStream.write(resultBuffer.toArray());
        } finally {
            if (image != null) image.release();
        }
    }

}

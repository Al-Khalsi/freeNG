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

    private static final int BUFFER_SIZE_FOR_CHUNK_READING = 8192;

    // Converts an image in chunks to minimize memory usage.
    public void convertImageInChunks(InputStream inputStream, OutputStream outputStream, String targetFormat) throws IOException {
        Mat image = null;
        try (ByteArrayOutputStream tempStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE_FOR_CHUNK_READING];
            int bytesRead;

            // Read the input stream in chunks and store it in a temporary stream.
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                log.debug("Reading chunk of size: {}", bytesRead);
                tempStream.write(buffer, 0, bytesRead);
            }
            log.debug("Chunk processed successfully. Chunk size: {}", tempStream.size());

            // Decode the collected bytes into an OpenCV Mat object.
            image = Imgcodecs.imdecode(new MatOfByte(tempStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);

            // Encode the Mat object into the target format.
            MatOfByte resultBuffer = new MatOfByte();
            Imgcodecs.imencode("." + targetFormat, image, resultBuffer);

            // Write the converted bytes to the output stream.
            outputStream.write(resultBuffer.toArray());
        } finally {
            // Release native resources used by OpenCV.
            if (image != null) image.release();
        }
    }

}

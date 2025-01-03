package com.pixelfreebies.service.image.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessingService {

    public InputStream convertImage(InputStream inputStream, String targetFormat) throws IOException {
        Mat image = null;
        MatOfByte buffer = null;
        try {
            // Step 1: Read the input image into an OpenCV Mat object
            // Explanation:
            // - `inputStream.readAllBytes()`: Reads the entire image into memory as a byte array.
            // - `new MatOfByte()`: Wraps the byte array to allow OpenCV to decode it.
            // - `Imgcodecs.imdecode()`: Converts the byte array into an OpenCV Mat object.
            // - `IMREAD_UNCHANGED`: Reads the image without altering its alpha channel or color depth.
            // **Performance Concern**: This step can consume a lot of memory if the image is large, as the entire file is loaded into RAM.
            // **Improvement**: Streaming-based processing might be better for extremely large files, but OpenCV's APIs are designed to work with Mat objects, which reside in RAM.
            image = Imgcodecs.imdecode(new MatOfByte(inputStream.readAllBytes()), Imgcodecs.IMREAD_UNCHANGED);

            // Step 2: Convert the Mat object to the target format
            // Explanation:
            // - `Imgcodecs.imencode`: Encodes the Mat object into a byte array for the specified format.
            // - `"." + targetFormat`: Specifies the target file format, e.g., ".png", ".jpg".
            buffer = new MatOfByte(); // Create a buffer to store encoded image bytes
            Imgcodecs.imencode("." + targetFormat, image, buffer);

            // Step 3: Return the converted image as an InputStream
            // Explanation:
            // - `buffer.toArray()`: Converts the MatOfByte to a regular byte array.
            // - `new ByteArrayInputStream`: Wraps the byte array to allow streaming back to the caller.
            // **Performance Concern**: While this avoids writing to disk, it still keeps the entire image in RAM.
            // **Improvement**: If processing multiple large images, ensure you release unused Mat objects to free native memory:
            //     `image.release();`
            //     `buffer.release();`
            return new ByteArrayInputStream(buffer.toArray());
        } finally {
            // Release OpenCV resources to free native memory; since OpenCV's Mat objects use native memory, which is not managed by the JVM garbage collector.
            if (image != null) image.release();
            if (buffer != null) buffer.release();
        }
    }

}

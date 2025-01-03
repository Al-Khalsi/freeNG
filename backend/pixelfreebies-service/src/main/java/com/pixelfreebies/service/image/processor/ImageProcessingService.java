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
        // Read the input image
        Mat image = Imgcodecs.imdecode(new MatOfByte(inputStream.readAllBytes()), Imgcodecs.IMREAD_UNCHANGED);

        // Convert the Mat back to byte[] for the specified format
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode("." + targetFormat, image, buffer);

        return new ByteArrayInputStream(buffer.toArray());
    }

}

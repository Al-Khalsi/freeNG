package com.pixelfreebies.service.image.conversion;

import com.pixelfreebies.exception.PixelfreebiesException;

import java.io.IOException;

public interface ImageConversionService {

    String convertAndUploadImage(String objectName, String targetFormat) throws PixelfreebiesException, IOException;

}

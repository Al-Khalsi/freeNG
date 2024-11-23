package com.imalchemy.service;

import com.imalchemy.model.dto.ImageDTO;
import com.imalchemy.model.dto.UpdateImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    ImageDTO storeImage(MultipartFile multipartFile, String fileName, String parentCategoryName,
                        List<String> subCategoryNames, List<String> dominantColors,
                        String style, boolean lightMode) throws IOException;

    Resource loadImageAsResource(String fileId) throws IOException;

    List<ImageDTO> listAllImages();

    List<ImageDTO> searchImages(String query);

    void deleteImageById(String fileId);

    ImageDTO updateImage(String imageId, UpdateImageDTO updateImageDTO);

}

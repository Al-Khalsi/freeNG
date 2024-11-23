package com.imalchemy.service;

import com.imalchemy.model.dto.ImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    ImageDTO storeFile(MultipartFile multipartFile, String fileName, String parentCategoryName,
                       List<String> subCategoryNames, List<String> dominantColors,
                       String style, boolean lightMode) throws IOException;

    Resource loadFileAsResource(String fileId) throws IOException;

    List<ImageDTO> listAllFiles();

    List<ImageDTO> searchFiles(String query);

}

package com.imalchemy.service;

import com.imalchemy.model.dto.FileDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    FileDTO storeFile(MultipartFile multipartFile, String fileName, String parentCategoryName, List<String> sucCategoryNames, List<String> dominantColors, String style) throws IOException;

    Resource loadFileAsResource(String fileId) throws IOException;

    List<FileDTO> listAllFiles();

    List<FileDTO> searchFiles(String query);

}

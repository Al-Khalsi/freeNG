package com.imalchemy.service;

import com.imalchemy.model.domain.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    File storeFile(MultipartFile multipartFile) throws IOException;

}

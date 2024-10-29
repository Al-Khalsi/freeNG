package com.imalchemy.service.impl;

import com.imalchemy.model.domain.File;
import com.imalchemy.repository.FileRepository;
import com.imalchemy.service.FileService;
import com.imalchemy.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@Profile("!prod")
public class FileServiceImpl implements FileService {

    private @Value("${file.storage.location}") String fileLocation;

    private final FileRepository fileRepository;
    private final Path fileStorageLocation;
    private final SecurityUtil securityUtil;

    public FileServiceImpl(FileRepository fileRepository, SecurityUtil securityUtil) throws IOException {
        this.fileRepository = fileRepository;
        this.securityUtil = securityUtil;

        // Set up the directory where files will be stored
        if (this.fileLocation == null) this.fileLocation = "src/main/resources/uploads";
        this.fileStorageLocation = Paths.get(this.fileLocation).toAbsolutePath().normalize();
        // Create the directory if it doesn't exist
        Files.createDirectories(this.fileStorageLocation);
    }

    @Override
    public File storeFile(MultipartFile multipartFile) throws IOException {
        // Clean the filename to remove any potential security risks
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        // Resolve the path where the file will be saved
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        // Copy the file to the target location, replacing if it already exists
        Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        // Create a new File to store in the database
        File file = createFileDomain(multipartFile, fileName, targetLocation);

        // Save the file metadata to the database and return the entity
        return this.fileRepository.save(file);
    }

    private File createFileDomain(MultipartFile multipartFile, String fileName, Path targetLocation) {
        File file = new File();
        file.setFileTitle(fileName);
        file.setFilePath(targetLocation.toString());
        file.setContentType(multipartFile.getContentType());
        file.setSize(multipartFile.getSize());
        file.setActive(true);
        file.setAverageRating(BigDecimal.ZERO);
        file.setDownloadCount(0);
        file.setUploadedBy(this.securityUtil.getAuthenticatedUser());
        file.setHeight(0);
        file.setWidth(0);

        return file;
    }

}

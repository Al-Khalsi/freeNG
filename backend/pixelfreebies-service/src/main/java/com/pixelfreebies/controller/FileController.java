package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.UpdateImageDTO;
import com.pixelfreebies.model.payload.response.PaginatedResult;
import com.pixelfreebies.model.payload.response.Result;
import com.pixelfreebies.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/file")
@Tag(name = "File API", description = "Endpoints for file operations")
@SecurityRequirement(name = "BearerToken")
public class FileController {

    private final FileService fileService;

    // Endpoint for uploading a file
    @Operation(
            summary = "Upload a file",
            description = "Upload a file to the server"
    )
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
    public ResponseEntity<Result> uploadFile(@RequestParam(name = "file") MultipartFile multipartFile,
                                             @RequestParam String fileName,
                                             @RequestParam List<String> keywords,
                                             @RequestParam List<String> dominantColors,
                                             @RequestParam String style,
                                             @RequestParam boolean lightMode) {
        try {

            ImageDTO imageDTO = this.fileService.storeImage(multipartFile, fileName, keywords, dominantColors, style, lightMode);
            return ResponseEntity.ok(Result.success("File uploaded successfully", imageDTO));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for file upload: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(BAD_REQUEST, "Invalid input: " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Result.error(INTERNAL_SERVER_ERROR, "Failed to upload file: " + e.getMessage()));
        }
    }

    // Endpoint for downloading a file
    @Operation(
            summary = "Download a file",
            description = "Download a file from the server using its ID"
    )
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        try {
            Resource resource = this.fileService.loadImageAsResource(fileId);

            // Get the file's MIME type
            String contentType;
            try {

                contentType = Files.probeContentType(Paths.get(resource.getFile().getAbsolutePath()));

            } catch (IOException e) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint for fetching files
    @Operation(
            summary = "List all files",
            description = "Fetches all the files from the database."
    )
    @GetMapping("/list")
    public ResponseEntity<Result> fetchFiles() {
        List<ImageDTO> fileDTOs = this.fileService.listAllImages();

        return ResponseEntity.ok(Result.success("List files.", fileDTOs));
    }

    // Endpoint for fetching files
    @Operation(
            summary = "Fetch paginated list of files.",
            description = "Retrieves a paginated list of image files along with metadata."
    )
    @GetMapping("/list/paginated")
    public ResponseEntity<PaginatedResult<ImageDTO>> fetchFiles(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ImageDTO> imageDTOs = this.fileService.listAllImages(pageable);

        return ResponseEntity.ok(PaginatedResult.success("List files.", imageDTOs));
    }

    // Endpoint for searching files
    @Operation(
            summary = "Search files",
            description = "Searches the files from the database."
    )
    @GetMapping("/search")
    public ResponseEntity<Result> searchFiles(@RequestParam String query) {
        List<ImageDTO> searchedFiles = this.fileService.searchImages(query);

        if (searchedFiles.isEmpty()) {
            return ResponseEntity.ok(Result.success("No exact matches found. Here are similar results:", searchedFiles));
        }

        return ResponseEntity.ok(Result.success("Search files result.", searchedFiles));
    }

    // Endpoint for deleting files
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public ResponseEntity<Result> deleteImage(@PathVariable String imageId) {
        this.fileService.deleteImageById(imageId);

        return ResponseEntity.ok(Result.success("Deleted file successfully.", null));
    }

    // Endpoint for updating files
    @PutMapping("/{imageId}")
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public ResponseEntity<Result> updateImage(@PathVariable String imageId, @RequestBody UpdateImageDTO updateImageDTO) {
        ImageDTO result = this.fileService.updateImage(imageId, updateImageDTO);

        return ResponseEntity.ok(Result.success("Uploaded file successfully.", result));
    }

}

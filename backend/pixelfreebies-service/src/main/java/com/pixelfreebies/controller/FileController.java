package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.payload.request.ImageUploadRequest;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

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
                                             @ModelAttribute ImageUploadRequest imageUploadRequest) {
        ImageDTO imageDTO = this.fileService.saveImage(multipartFile, imageUploadRequest);
        return ResponseEntity.ok(Result.success("File uploaded successfully", imageDTO));
    }

    // Endpoint for downloading a file
    @Operation(
            summary = "Download a file",
            description = "Download a file from the server using its ID"
    )
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        ImageDTO image = this.fileService.findImageById(UUID.fromString(fileId));

        // Redirect to the S3 URL
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(image.getFilePath()))
                .build();
    }

    // Endpoint for fetching files
    @Operation(
            summary = "Fetch paginated list of files.",
            description = "Retrieves a paginated list of image files along with metadata."
    )
    @GetMapping("/list/paginated")
    public ResponseEntity<PaginatedResult<ImageDTO>> fetchFiles(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ImageDTO> imageDTOs = this.fileService.listAllImages(pageable);

        return ResponseEntity.ok(PaginatedResult.success("List files.", true, imageDTOs));
    }

    // Endpoint for deleting files
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public ResponseEntity<Result> deleteImage(@PathVariable String imageId) {
        this.fileService.deleteImageById(imageId);

        return ResponseEntity.ok(Result.success("Deleted file successfully.", null));
    }

    // Endpoint for fetching images based on keyword
    @Operation(
            summary = "Fetch paginated list of images based on provided keyword.",
            description = "Retrieves a paginated list of image files along with metadata based on the keywordId provided."
    )
    @GetMapping("/keyword/{keywordId}")
    public ResponseEntity<PaginatedResult<ImageDTO>> fetchImagesByKeywordId(@PathVariable long keywordId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ImageDTO> imageDTOs = this.fileService.listAllImagesByKeywordId(keywordId, pageable);

        return ResponseEntity.ok(PaginatedResult.success("List found images by keywordId.", true, imageDTOs));
    }

}

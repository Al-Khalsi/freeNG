package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.ImageRemoveDominantColorDTO;
import com.pixelfreebies.model.dto.ImageRemoveKeywordsDTO;
import com.pixelfreebies.model.dto.ImageRemoveStyleDTO;
import com.pixelfreebies.model.payload.request.ImageOperationRequest;
import com.pixelfreebies.model.payload.response.PaginatedResult;
import com.pixelfreebies.model.payload.response.Result;
import com.pixelfreebies.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/file")
@Tag(name = "File API", description = "Endpoints for file operations")
@SecurityRequirement(name = "BearerToken")
public class ImageController {

    private final ImageService imageService;

    // Endpoint for uploading a file
    @Operation(
            summary = "Upload a file",
            description = "Upload a file to the server"
    )
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
    public ResponseEntity<Result> uploadImage(@RequestParam(name = "file") MultipartFile multipartFile,
                                              @ModelAttribute ImageOperationRequest imageOperationRequest) {
        ImageDTO imageDTO = this.imageService.saveImage(multipartFile, imageOperationRequest);
        return ResponseEntity.ok(Result.success("File uploaded successfully", imageDTO));
    }

    // New method for batch uploading with virtual threads
    @Operation(
            summary = "Batch upload files with virtual threads",
            description = "Upload multiple files to the server using Java virtual threads for performance"
    )
    @PostMapping("/upload/virtual-batch")
    @PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
    public ResponseEntity<Result> uploadImagesWithVirtualThreads(
            @RequestParam("files") List<MultipartFile> multipartFiles,
            @ModelAttribute ImageOperationRequest imageOperationRequest) {

        // TODO: implement multi-threading
        /*ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        List<CompletableFuture<ImageDTO>> futures = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    return this.imageService.saveImage(file, imageOperationRequest);
                } catch (Exception e) {
                    log.error("Error processing file: {}", e.getMessage(), e);
                    throw new CompletionException(e);
                }
            }, executorService));
        }

        List<ImageDTO> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (CompletableFuture<ImageDTO> future : futures) {
            try {
                results.add(future.join());
            } catch (CompletionException e) {
                errors.add(e.getCause().getMessage());
                log.error("Error during batch processing: {}", e.getCause().getMessage(), e);
            }
        }

        executorService.shutdown();

        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(Result.partialSuccess("Some files failed to upload", results, errors));
        }

        return ResponseEntity.ok(Result.success("All files uploaded successfully", results));*/
        List<ImageDTO> results = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
                try {
                    ImageDTO imageDTO = this.imageService.saveImage(file, imageOperationRequest);
                    results.add(imageDTO);
                } catch (Exception e) {
                    log.error("Error processing file: {}", e.getMessage(), e);
                    throw new CompletionException(e);
                }
        }

        return ResponseEntity.ok(Result.success("All files uploaded successfully", results));
    }

    // Endpoint for downloading a file
    @Operation(
            summary = "Download a file",
            description = "Download a file from the server using its ID"
    )
    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileId) {
        ImageDTO image = this.imageService.findImageById(UUID.fromString(fileId));
        URI location = URI.create(image.getFilePath());
        log.debug("Download request for image: {}", image);
        log.debug("Download location URI: {}", location);

        // Redirect to the S3 URL
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(location)
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
        Page<ImageDTO> imageDTOs = this.imageService.listAllImages(pageable);

        return ResponseEntity.ok(PaginatedResult.success("List files.", true, imageDTOs));
    }

    // Endpoint for deleting files
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public ResponseEntity<Result> deleteImage(@PathVariable String imageId) {
        this.imageService.deleteImageById(imageId);

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
        Page<ImageDTO> imageDTOs = this.imageService.listAllImagesByKeywordId(keywordId, pageable);

        return ResponseEntity.ok(PaginatedResult.success("List found images by keywordId.", true, imageDTOs));
    }

    // Endpoint for updating an image
    @Operation(
            summary = "Update an Image.",
            description = "Update an image from the server using its ID"
    )
    @PatchMapping("/{imageId}")
    public ResponseEntity<Result> updateImage(@PathVariable String imageId, @RequestBody ImageOperationRequest imageOperationRequest) {
        ImageDTO image = this.imageService.updateImage(UUID.fromString(imageId), imageOperationRequest);

        return ResponseEntity.ok(Result.success("Image updated successfully.", image));
    }

    // Endpoint for removing style from image
    @Operation(
            summary = "Remove style from an Image.",
            description = "Update an image; remove style from image from the server using its ID"
    )
    @PatchMapping("/style/{imageId}")
    public ResponseEntity<Result> removeStylesFromImage(@PathVariable String imageId, @RequestBody ImageRemoveStyleDTO removeStyleDTO) {
        ImageDTO image = this.imageService.removeStylesFromImage(UUID.fromString(imageId), removeStyleDTO);

        return ResponseEntity.ok(Result.success("Styles removed from image successfully.", image));
    }

    // Endpoint for removing dominant colors from an image
    @Operation(
            summary = "Remove dominant colors from an Image.",
            description = "Update an image; remove dominant colors from the image on the server using its ID"
    )
    @PatchMapping("/dominant-color/{imageId}")
    public ResponseEntity<Result> removeDominantColorsFromImage(@PathVariable String imageId, @RequestBody ImageRemoveDominantColorDTO removeColorDTO) {
        ImageDTO image = this.imageService.removeDominantColorsFromImage(UUID.fromString(imageId), removeColorDTO);
        return ResponseEntity.ok(Result.success("Dominant colors removed from image successfully.", image));
    }

    // Endpoint for removing keywords from an image
    @Operation(
            summary = "Remove keywords from an Image.",
            description = "Update an image; remove keywords from the image on the server using its ID"
    )
    @PatchMapping("/keywords/{imageId}")
    public ResponseEntity<Result> removeKeywordsFromImage(@PathVariable String imageId, @RequestBody ImageRemoveKeywordsDTO removeKeywordsDTO) {
        ImageDTO image = this.imageService.removeKeywordsFromImage(UUID.fromString(imageId), removeKeywordsDTO);
        return ResponseEntity.ok(Result.success("Keywords removed from image successfully.", image));
    }

}

package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.payload.request.ImageOperationRequest;
import com.pixelfreebies.model.payload.response.Result;
import com.pixelfreebies.service.image.storage.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/file")
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
@SecurityRequirement(name = "BearerToken")
@Tag(name = "Image Storage API", description = "Endpoints for Image storage operations")
public class ImageStorageController {

    private final ImageStorageService imageStorageService;

    @Operation(
            summary = "Upload an Image",
            description = "Upload an Image to the server"
    )
    @PostMapping("/upload")
    public ResponseEntity<Result> uploadImage(@RequestParam(name = "file") MultipartFile multipartFile,
                                              @ModelAttribute ImageOperationRequest imageOperationRequest) {
        ImageDTO imageDTO = this.imageStorageService.saveImage(multipartFile, imageOperationRequest);

        return ResponseEntity.ok(Result.success("File uploaded successfully", imageDTO));
    }

    @Operation(
            summary = "Batch upload files with virtual threads",
            description = "Upload multiple files to the server using Java virtual threads for performance"
    )
    @PostMapping("/upload/virtual-batch")
    public ResponseEntity<Result> uploadImagesWithVirtualThreads(@RequestParam("files") List<MultipartFile> multipartFiles,
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
                ImageDTO imageDTO = this.imageStorageService.saveImage(file, imageOperationRequest);
                results.add(imageDTO);
            } catch (Exception e) {
                log.error("Error processing file: {}", e.getMessage(), e);
                throw new CompletionException(e);
            }
        }

        return ResponseEntity.ok(Result.success("All files uploaded successfully", results));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Result> deleteImage(@PathVariable String imageId) {
        this.imageStorageService.deleteImage(imageId);

        return ResponseEntity.ok(Result.success("Deleted file successfully.", null));
    }

}

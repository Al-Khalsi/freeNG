package com.imalchemy.controller;

import com.imalchemy.model.dto.FileDTO;
import com.imalchemy.model.payload.response.Result;
import com.imalchemy.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File successfully uploaded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Result.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Failed to upload file"
            )
    })
    @PostMapping("/upload")
    public ResponseEntity<Result> uploadFile(@RequestParam(name = "file") MultipartFile multipartFile,
                                             @RequestParam String fileName,
                                             @RequestParam String parentCategoryName,
                                             @RequestParam List<String> subCategoryNames,
                                             @RequestParam List<String> dominantColors,
                                             @RequestParam String style,
                                             @RequestParam boolean lightMode) {
        try {

            FileDTO fileDTO = this.fileService.storeFile(multipartFile, fileName, parentCategoryName, subCategoryNames, dominantColors, style, lightMode);
            return ResponseEntity.ok(Result.success("File uploaded successfully", fileDTO));

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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File successfully downloaded",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary") // Indicating binary data
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found"
            )
    })
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        try {
            Resource resource = this.fileService.loadFileAsResource(fileId);

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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List all files.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Result.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found"
            )
    })
    @GetMapping("/list")
    public ResponseEntity<Result> fetchFiles() {
        List<FileDTO> fileDTOs = this.fileService.listAllFiles();

        return ResponseEntity.ok(Result.success("List files.", fileDTOs));
    }

    // Endpoint for searching files
    @Operation(
            summary = "Search files",
            description = "Searches the files from the database."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search files.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Result.class)
                    )
            )
    })
    @GetMapping("/search")
    public ResponseEntity<Result> searchFiles(@RequestParam String query) {
        List<FileDTO> searchedFiles = this.fileService.searchFiles(query);

        if (searchedFiles.isEmpty()) {
            return ResponseEntity.ok(Result.success("No exact matches found. Here are similar results:", searchedFiles));
        }

        return ResponseEntity.ok(Result.success("Search files result.", searchedFiles));
    }

}

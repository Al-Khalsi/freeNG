package com.imalchemy.controller;

import com.imalchemy.model.domain.File;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public ResponseEntity<Result> uploadFile(@RequestParam(name = "file") MultipartFile multipartFile) {
        try {
            File file = this.fileService.storeFile(multipartFile);
            return ResponseEntity.ok(Result.builder()
                    .flag(true)
                    .code(HttpStatus.CREATED)
                    .message("File uploaded")
                    .data(file) //TODO: file response is OK and no json response is getting populated
                    .build()
            );
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Result.builder()
                    .flag(false)
                    .code(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .data(null)
                    .build()
            );
        }
    }

}

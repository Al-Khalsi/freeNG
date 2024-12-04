package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.payload.response.Result;
import com.pixelfreebies.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/file/search")
@Tag(name = "File API", description = "Endpoints for file operations")
@SecurityRequirement(name = "BearerToken")
public class SearchController {

    private final FileService fileService;

    // Endpoint for searching files
    @Operation(
            summary = "Search files",
            description = "Searches the files from the database."
    )
    @GetMapping
    public ResponseEntity<Result> searchFiles(@RequestParam String query) {
        List<ImageDTO> searchedFiles = this.fileService.searchImages(query);

        if (searchedFiles.isEmpty()) {
            return ResponseEntity.ok(Result.success("No exact matches found. Here are similar results:", searchedFiles));
        }

        return ResponseEntity.ok(Result.success("Search files result.", searchedFiles));
    }

    // Endpoint for searching keywords
    @Operation(
            summary = "Fetch keywords list.",
            description = "Retrieves a paginated list of keywords."
    )
    @GetMapping("/keywords/paginated")
    public ResponseEntity<Result> searchKeywords(@RequestParam String query) {
        List<String> searchedKeywords = this.fileService.searchKeywords(query);

        if (searchedKeywords.isEmpty()) {
            return ResponseEntity.ok(Result.success("No exact matches found. Here are similar results:", searchedKeywords));
        }

        return ResponseEntity.ok(Result.success("Search files result.", searchedKeywords));
    }

}

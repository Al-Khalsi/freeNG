package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.ImageDTO;
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

    private final ImageService imageService;

    // Endpoint for searching files
    @Operation(
            summary = "Search files paginated",
            description = "Searches the files from the database and return a paginated result."
    )
    @GetMapping("/paginated")
    public ResponseEntity<PaginatedResult<ImageDTO>> searchFilesPaginated(@RequestParam String query,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "50") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ImageDTO> searchedFiles = this.imageService.searchImages(query, pageRequest);

        if (searchedFiles.isEmpty()) {
            return ResponseEntity.ok(PaginatedResult.success("No exact matches found. Here are similar results paginated:", true, searchedFiles));
        }

        return ResponseEntity.ok(PaginatedResult.success("Search files result paginated.", true, searchedFiles));
    }


    // Endpoint for searching keywords
    @Operation(
            summary = "Fetch keywords list.",
            description = "Retrieves a paginated list of keywords."
    )
    @GetMapping("/keywords/paginated")
    public ResponseEntity<Result> searchKeywords(@RequestParam String query,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        List<String> searchedKeywords = this.imageService.searchKeywords(query, page, size);

        if (searchedKeywords.isEmpty()) {
            return ResponseEntity.ok(Result.success("No exact matches found. Here are similar results:", searchedKeywords));
        }

        return ResponseEntity.ok(Result.success("Search files result.", searchedKeywords));
    }

}

package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.KeywordsDTO;
import com.pixelfreebies.model.payload.response.PaginatedResult;
import com.pixelfreebies.model.payload.response.Result;
import com.pixelfreebies.service.KeywordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/keywords")
@Tag(name = "Keywords API", description = "Endpoints for keywords operations")
@SecurityRequirement(name = "BearerToken")
public class KeywordsController {

    private final KeywordsService keywordsService;

    @Operation(
            summary = "Fetch paginated list of keywords.",
            description = "Retrieves a paginated list of keywords."
    )
    @GetMapping("/list/paginated")
    public ResponseEntity<PaginatedResult<KeywordsDTO>> listPaginatedKeywords(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<KeywordsDTO> keywordsDTOsPage = this.keywordsService.keywordsPage(pageRequest);

        return ResponseEntity.ok(PaginatedResult.success("List pagination of keywords.", true, keywordsDTOsPage));
    }

    @Operation(
            summary = "Create a keyword."
    )
    @PostMapping
    public ResponseEntity<Result> createKeyword(@RequestBody KeywordsDTO keywordsDTO) {
        KeywordsDTO result = this.keywordsService.createKeyword(keywordsDTO);

        return ResponseEntity.status(CREATED).body(Result.success("Keyword created successfully.", CREATED, result));
    }

    @Operation(
            summary = "Update a keyword."
    )
    @PutMapping("/{keywordId}")
    public ResponseEntity<Result> updateKeyword(@PathVariable long keywordId, @RequestBody KeywordsDTO keywordsDTO) {
        KeywordsDTO result = this.keywordsService.updateKeyword(keywordId, keywordsDTO);

        return ResponseEntity.status(OK).body(Result.success("Keyword updated successfully.", result));
    }

    @Operation(
            summary = "Delete a keyword."
    )
    @DeleteMapping("/{keywordId}")
    public ResponseEntity<Result> deleteKeyword(@PathVariable long keywordId) {
        this.keywordsService.deleteKeyword(keywordId);

        return ResponseEntity.status(OK).body(Result.success("Keyword deleted successfully.", null));
    }

}

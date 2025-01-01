package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.payload.response.PaginatedResult;
import com.pixelfreebies.service.ImageQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/file")
@Tag(name = "Image Query API", description = "Endpoints for Image querying operations")
@SecurityRequirement(name = "BearerToken")
public class ImageQueryController {

    private final ImageQueryService imageQueryService;

    @Operation(
            summary = "Fetch paginated list of image.",
            description = "Retrieves a paginated list of image files along with metadata."
    )
    @GetMapping("/list/paginated")
    public ResponseEntity<PaginatedResult<ImageDTO>> fetchFiles(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ImageDTO> imageDTOs = this.imageQueryService.listAllImages(pageable);

        return ResponseEntity.ok(PaginatedResult.success("List files.", true, imageDTOs));
    }

    @Operation(
            summary = "Fetch paginated list of images based on provided keyword.",
            description = "Retrieves a paginated list of image files along with metadata based on the keywordId provided."
    )
    @GetMapping("/keyword/{keywordId}")
    public ResponseEntity<PaginatedResult<ImageDTO>> fetchImagesByKeywordId(@PathVariable long keywordId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ImageDTO> imageDTOs = this.imageQueryService.listAllImagesByKeywordId(keywordId, pageable);

        return ResponseEntity.ok(PaginatedResult.success("List found images by keywordId.", true, imageDTOs));
    }

}

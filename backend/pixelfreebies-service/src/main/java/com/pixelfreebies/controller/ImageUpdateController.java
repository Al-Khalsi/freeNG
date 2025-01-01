package com.pixelfreebies.controller;

import com.pixelfreebies.model.dto.ImageDTO;
import com.pixelfreebies.model.dto.ImageRemoveDominantColorDTO;
import com.pixelfreebies.model.dto.ImageRemoveKeywordsDTO;
import com.pixelfreebies.model.dto.ImageRemoveStyleDTO;
import com.pixelfreebies.model.payload.request.ImageOperationRequest;
import com.pixelfreebies.model.payload.response.Result;
import com.pixelfreebies.service.ImageUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/file")
@PreAuthorize("hasAnyRole('ROLE_MASTER', 'ROLE_ADMIN')")
@SecurityRequirement(name = "BearerToken")
@Tag(name = "Image Update API", description = "Endpoints for updating Image operations")
public class ImageUpdateController {
    private final ImageUpdateService imageUpdateService;

    @Operation(
            summary = "Update an Image.",
            description = "Update an image from the server using its ID"
    )
    @PatchMapping("/{imageId}")
    public ResponseEntity<Result> updateImage(@PathVariable String imageId, @RequestBody ImageOperationRequest imageOperationRequest) {
        ImageDTO image = this.imageUpdateService.updateImage(UUID.fromString(imageId), imageOperationRequest);

        return ResponseEntity.ok(Result.success("Image updated successfully.", image));
    }

    @Operation(
            summary = "Remove style from an Image.",
            description = "Update an image; remove style from image from the server using its ID"
    )
    @PatchMapping("/style/{imageId}")
    public ResponseEntity<Result> removeStylesFromImage(@PathVariable String imageId, @RequestBody ImageRemoveStyleDTO removeStyleDTO) {
        ImageDTO image = this.imageUpdateService.removeStylesFromImage(UUID.fromString(imageId), removeStyleDTO);

        return ResponseEntity.ok(Result.success("Styles removed from image successfully.", image));
    }

    @Operation(
            summary = "Remove dominant colors from an Image.",
            description = "Update an image; remove dominant colors from the image on the server using its ID"
    )
    @PatchMapping("/dominant-color/{imageId}")
    public ResponseEntity<Result> removeDominantColorsFromImage(@PathVariable String imageId, @RequestBody ImageRemoveDominantColorDTO removeColorDTO) {
        ImageDTO image = this.imageUpdateService.removeDominantColorsFromImage(UUID.fromString(imageId), removeColorDTO);
        return ResponseEntity.ok(Result.success("Dominant colors removed from image successfully.", image));
    }

    @Operation(
            summary = "Remove keywords from an Image.",
            description = "Update an image; remove keywords from the image on the server using its ID"
    )
    @PatchMapping("/keywords/{imageId}")
    public ResponseEntity<Result> removeKeywordsFromImage(@PathVariable String imageId, @RequestBody ImageRemoveKeywordsDTO removeKeywordsDTO) {
        ImageDTO image = this.imageUpdateService.removeKeywordsFromImage(UUID.fromString(imageId), removeKeywordsDTO);
        return ResponseEntity.ok(Result.success("Keywords removed from image successfully.", image));
    }

}

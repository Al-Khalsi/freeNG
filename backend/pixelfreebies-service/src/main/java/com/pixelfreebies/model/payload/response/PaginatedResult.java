package com.pixelfreebies.model.payload.response;

import com.pixelfreebies.model.dto.ImageDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Paginated result of a query.")
public class PaginatedResult<T> {

    @Schema(description = "Response message.", example = "List files.")
    private String message;

    @Schema(description = "List of data items.")
    @ArraySchema(
            schema = @Schema(implementation = ImageDTO.class),
            arraySchema = @Schema(description = "List of image DTO objects.")
    )
    private T data;

    @Schema(description = "Current page number.", example = "0")
    private int currentPage;

    @Schema(description = "Total number of pages.", example = "16")
    private int totalPages;

    @Schema(description = "Total number of elements.", example = "16")
    private long totalElements;

    @Schema(description = "Indicates if this is the last page.", example = "false")
    private boolean last;

    public static <T> PaginatedResult<T> success(String message, Page<T> page) {
        PaginatedResult<T> result = new PaginatedResult<>();
        result.message = message;
        result.data = (T) page.getContent();
        result.currentPage = page.getNumber();
        result.totalPages = page.getTotalPages();
        result.totalElements = page.getTotalElements();
        result.last = page.isLast();
        return result;
    }

}
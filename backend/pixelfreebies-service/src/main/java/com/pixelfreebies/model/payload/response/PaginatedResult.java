package com.pixelfreebies.model.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Paginated result of a query.")
public class PaginatedResult<T> {

    private String message;
    private T data;
    private boolean flag;
    private HttpStatus code;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean last;

    public static <T> PaginatedResult<T> success(String message, boolean flag, Page<T> page) {
        PaginatedResult<T> result = new PaginatedResult<>();
        result.message = message;
        result.flag = flag;
        result.data = (T) page.getContent();
        result.currentPage = page.getNumber();
        result.totalPages = page.getTotalPages();
        result.totalElements = page.getTotalElements();
        result.last = page.isLast();
        return result;
    }

}
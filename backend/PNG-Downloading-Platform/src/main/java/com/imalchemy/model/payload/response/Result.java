package com.imalchemy.model.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@Schema(description = "API Response wrapper")
public class Result {

    @Schema(description = "Indicates if the operation was successful")
    private boolean flag;

    @Schema(description = "HTTP status code")
    private HttpStatus code;

    @Schema(description = "Response message")
    private String message;

    @Schema(description = "Response payload", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Object data;

    public Result(boolean flag, HttpStatus code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    public Result(boolean flag, HttpStatus code, String message, Object data) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success(String message, Object data) {
        return Result.builder()
                .flag(true)
                .code(HttpStatus.OK)
                .message(message)
                .data(data)
                .build();
    }

    public static Result error(HttpStatus status, String message) {
        return Result.builder()
                .flag(false)
                .code(status)
                .message(message)
                .data(null)
                .build();
    }

}

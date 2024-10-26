package com.imalchemy.model.payload.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
public class Result {

    private boolean flag;
    private HttpStatus code;
    private String message;
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

}

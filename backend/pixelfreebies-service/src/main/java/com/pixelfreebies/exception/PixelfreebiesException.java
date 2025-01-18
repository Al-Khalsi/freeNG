package com.pixelfreebies.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PixelfreebiesException extends RuntimeException {

    private final HttpStatus httpStatus;

    public PixelfreebiesException(String msg, HttpStatus httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public PixelfreebiesException(String msg) {
        super(msg);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

}

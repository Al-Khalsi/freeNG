package com.pixelfreebies.exception;

import com.pixelfreebies.model.payload.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({AlreadyExistsException.class})
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<Result> handleAlreadyExistException(AlreadyExistsException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new Result(false, BAD_REQUEST, e.getMessage(), null));
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<Result> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(new Result(false, NOT_FOUND, "the object requested was not found", e.getMessage()));
    }

    @ExceptionHandler({IncorrectResultSizeDataAccessException.class})
    @ResponseStatus(CONFLICT)
    public ResponseEntity<Result> handleIncorrectResultSizeDataAccessException(IncorrectResultSizeDataAccessException e) {
        return ResponseEntity.status(CONFLICT).body(new Result(false, CONFLICT, "query did not return a unique result", e.getMessage()));
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(CONFLICT)
    public ResponseEntity<Result> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.status(CONFLICT).body(new Result(false, CONFLICT, "duplicate key value violates unique constraint", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<Result> handleMethodValidationException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        Map<String, String> map = new HashMap<>(errors.size());

        errors.forEach(error -> {
            String key = ((FieldError) error).getField();
            String value = error.getDefaultMessage();
            map.put(key, value);
        });

        return ResponseEntity.status(BAD_REQUEST).body(new Result(
                false,
                BAD_REQUEST,
                "Provided arguments are invalid, see data for details.",
                map
        ));
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<Result> handleAuthenticationException(Exception e) {
        return ResponseEntity.status(UNAUTHORIZED).body(new Result(false, UNAUTHORIZED, "username or password is incorrect.", e.getMessage()));
    }

    @ExceptionHandler({AccountStatusException.class})
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<Result> handleAccountStatusException(AccountStatusException e) {
        return ResponseEntity.status(UNAUTHORIZED).body(new Result(false, UNAUTHORIZED, "User account is abnormal.", e.getMessage()));
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(FORBIDDEN)
    public ResponseEntity<Result> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(FORBIDDEN).body(new Result(false, FORBIDDEN, "No permission.", e.getMessage()));
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<Result> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new Result(false, INTERNAL_SERVER_ERROR, "Null pointer exception occurred.", e.getMessage()));
    }

    @ExceptionHandler({PixelfreebiesException.class})
    public ResponseEntity<Result> handlePixelfreebiesException(PixelfreebiesException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new Result(false, e.getHttpStatus(), "error occurred in application", e.getMessage()));
    }

    /**
     * Fallback handles any unhandled exceptions.
     *
     * @param e exception object
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<Result> handleOtherException(Exception e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new Result(false, INTERNAL_SERVER_ERROR, "A server internal error occurs.", e.getMessage()));
    }

}

package org.peepol.advice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private void logException(Exception ex) {
        logger.error("Unable to perform the operation.", ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMissingBody(HttpRequestMethodNotSupportedException ex) {
        logException(ex);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "Provided request is invalid."
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMissingBody(HttpMessageNotReadableException ex) {
        logException(ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "Provided request is invalid."
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        logException(ex);

        var detail = "Provided request is invalid. " + ex.getBindingResult().getFieldErrors().stream()
                .map((error) -> "[" + error.getField() + "]: " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail)
        );

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex) {
        logException(ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized"
                )
        );

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        logException(ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage()
                )
        );

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception ex) {
        logException(ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unable to perform the operation."
                )
        );
    }
}

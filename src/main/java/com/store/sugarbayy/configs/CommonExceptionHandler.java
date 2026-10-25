package com.store.sugarbayy.configs;

import com.resend.core.exception.ResendException;
import com.store.sugarbayy.dtos.ErrorResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {
    // handle async timeout
    @ExceptionHandler({AsyncRequestTimeoutException.class})
    public ResponseEntity<ErrorResponse> handleAsyncRequestTimeoutException(Exception ex) {
        log.error(ex.getMessage(), ex);
        var errorResponse = new ErrorResponse(ex.getMessage() != null ? ex.getMessage() : "Request timed out");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    // handle not found
    @ExceptionHandler({
            EntityNotFoundException.class,
            UsernameNotFoundException.class,
            NoSuchElementException.class,
    })
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
        log.error(ex.getMessage(), ex);
        var errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // handle invalid endpoint
    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleInvalidEndpoint(Exception ex) {
        log.error(ex.getMessage(), ex);
        var errorResponse = new ErrorResponse("Invalid endpoint");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // handle invalid auth
    @ExceptionHandler({
            BadCredentialsException.class,
            JwtException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorized(Exception ex) {
        log.error(ex.getMessage(), ex);
        var errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // handle invalid input / validation
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> handleValidation(Exception ex) {
        String errorMessage;
        if (ex instanceof MethodArgumentNotValidException | ex instanceof BindException) {
            errorMessage = ((BindException) ex).getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> Optional.ofNullable(error.getDefaultMessage())
                            .orElse("Request Validation Error, Bad Request"))
                    .findFirst()
                    .orElse("Request Validation Error, Bad Request");
        } else {
            errorMessage = "Request Validation Error, Bad Request";
        }
        log.error(ex.getMessage(), ex);
        var errorResponse = new ErrorResponse(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // handle custom exception
    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        log.error(ex.getMessage(), ex);
        var errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

    // General
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        log.error(ex.getMessage(), ex);
        var errorResponse = new ErrorResponse("An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

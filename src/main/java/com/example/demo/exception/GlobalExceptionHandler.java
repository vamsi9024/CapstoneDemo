package com.example.demo.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


//@RestControllerAdvice
public class GlobalExceptionHandler {
//    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
//
//    // Handle invalid credentials (401)
//    @ExceptionHandler(InvalidCredentialsException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
//            InvalidCredentialsException ex,
//            HttpServletRequest request) {
//
//        log.warn("Authentication failed: {}", ex.getMessage());
//
//        ErrorResponse error = new ErrorResponse(
//                LocalDateTime.now(),
//                HttpStatus.UNAUTHORIZED.value(),
//                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(error);
//    }
//
//    // Fallback for any other unhandled exceptions (500)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleAllExceptions(
//            Exception ex,
//            HttpServletRequest request) {
//
//        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
//
//        ErrorResponse error = new ErrorResponse(
//                LocalDateTime.now(),
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                "An unexpected error occurred. Please try again later.",
//                request.getRequestURI()
//        );
//
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(error);
//    }
//
//
////     logout
//    // Handle missing/invalid Authorization header (400)
//    @ExceptionHandler(MissingAuthorizationHeaderException.class)
//    public ResponseEntity<ErrorResponse> handleMissingHeader(
//            MissingAuthorizationHeaderException ex,
//            HttpServletRequest request) {
//
//        log.warn("Bad request: {}", ex.getMessage());
//        ErrorResponse body = new ErrorResponse(
//                LocalDateTime.now(),
//                HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.getReasonPhrase(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
//    }
//
//    // Handle invalid or expired token (401)
//    @ExceptionHandler(InvalidTokenException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidToken(
//            InvalidTokenException ex,
//            HttpServletRequest request) {
//
//        log.warn("Unauthorized: {}", ex.getMessage());
//        ErrorResponse body = new ErrorResponse(
//                LocalDateTime.now(),
//                HttpStatus.UNAUTHORIZED.value(),
//                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
//    }
//
////    validation
//    @ExceptionHandler(ApiException.class)
//    public ResponseEntity<ErrorResponse> handleApiException(
//            ApiException ex,
//            HttpServletRequest request) {
//
//        ErrorResponse body = new ErrorResponse(
//                LocalDateTime.now(),
//                ex.getStatus().value(),
//                ex.getStatus().getReasonPhrase(),
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return new ResponseEntity<>(body, ex.getStatus());
//    }
//
//
//
}

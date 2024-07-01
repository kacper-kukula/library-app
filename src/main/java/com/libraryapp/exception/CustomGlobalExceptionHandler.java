package com.libraryapp.exception;

import com.libraryapp.exception.custom.BookAlreadyLoanedException;
import com.libraryapp.exception.custom.EntityNotFoundException;
import com.libraryapp.exception.custom.LoanAlreadyReturnedException;
import com.libraryapp.exception.custom.RegistrationException;
import com.libraryapp.exception.custom.UnauthorizedViewException;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<String> mappedErrors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();

        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                mappedErrors);

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                List.of(ex.getMessage()));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookAlreadyLoanedException.class)
    public ResponseEntity<ErrorResponse> handleBookAlreadyLoanedException(
            BookAlreadyLoanedException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage()));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoanAlreadyReturnedException.class)
    public ResponseEntity<ErrorResponse> handleLoanAlreadyReturnedException(
            LoanAlreadyReturnedException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage()));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationException(RegistrationException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage()));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(
            ExpiredJwtException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED,
                List.of(ex.getMessage()));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedViewException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedViewException(
            UnauthorizedViewException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN,
                List.of(ex.getMessage()));

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    private String getErrorMessage(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError) {
            String field = fieldError.getField();
            String defaultMessage = objectError.getDefaultMessage();

            return field + " " + defaultMessage;
        }

        return objectError.getDefaultMessage();
    }
}

package com.example.leaveflow.exception;

import org.apache.coyote.Response;
import com.example.leaveflow.exception.ApiError;
import com.example.leaveflow.exception.ValidationException;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiError> handleEmployeeNotFound(EmployeeNotFoundException exception) {
        ApiError apiError = new ApiError(404,exception.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        ApiError apiError = new ApiError(400, "Validation failed.", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    // manual validation exception handler
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handlePatchValidationException(ValidationException exception) {
        ApiError apiError = new ApiError(400, exception.getMessage(), exception.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(LeaveRequestNotFoundException.class)
    public ResponseEntity<ApiError> handleLeaveRequestNotFound(
            LeaveRequestNotFoundException exception) {

        ApiError apiError = new ApiError(
                404,
                exception.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }

    @ExceptionHandler(UserAccountAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAccountAlreadyExistsException(UserAccountAlreadyExistsException exception) {
       ApiError apiError = new ApiError(
               409,
               exception.getMessage(),
               null
       );
       return ResponseEntity
               .status(HttpStatus.CONFLICT)
               .body(apiError);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbiddenException(
            ForbiddenException ex) {

        ApiError error = new ApiError(
                403,
                ex.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException exception) {

        ApiError apiError = new ApiError(
                401,
                exception.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(apiError);
    }

    @ExceptionHandler(ManagerAccountNotFoundException.class)
    public ResponseEntity<ApiError> handleManagerAccountNotFound(
            ManagerAccountNotFoundException exception) {

        ApiError apiError = new ApiError(
                500,
                exception.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

    @ExceptionHandler(EmployeeAccountNotLinkedException.class)
    public ResponseEntity<ApiError> handleEmployeeAccountNotLinked(
            EmployeeAccountNotLinkedException exception) {

        ApiError apiError = new ApiError(
                500,
                exception.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

}

package ru.ssau.tk.faible.coplatebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;

    public static ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus httpStatus, String message) {
        ErrorResponse error = new ErrorResponse(httpStatus.value(), message);
        return ResponseEntity.status(httpStatus.value()).body(error);
    }
}

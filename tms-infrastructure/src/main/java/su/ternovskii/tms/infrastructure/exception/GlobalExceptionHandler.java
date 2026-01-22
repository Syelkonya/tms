package su.ternovskii.tms.infrastructure.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleInternalServerException(Exception e) {
        log.error("Handle Internal server error {}", e.toString());

        var errorResponseDto = new ErrorResponseDto(
                "Internal server error",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDto);
    }


    @ExceptionHandler(exception = {
            EntityNotFoundException.class,
            NoSuchElementException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponseDto> handle(Exception e) {
        log.error("Handle Not found exception {}", e.toString());

        var errorResponseDto = new ErrorResponseDto(
                "Not found exception",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponseDto);
    }
}

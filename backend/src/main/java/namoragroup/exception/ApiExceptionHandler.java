package namoragroup.exception;

import java.time.Instant;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<Map<String, Object>> handleApi(ApiException error) {
        return ResponseEntity.status(error.getStatus()).body(Map.of(
                "timestamp", Instant.now().toString(), "code", error.getCode(), "message", error.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException error) {
        String message = error.getBindingResult().getFieldErrors().stream()
                .findFirst().map(field -> field.getField() + " : " + field.getDefaultMessage())
                .orElse("Les données envoyées sont invalides.");
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now().toString(), "code", "VALIDATION_ERROR", "message", message));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String, Object>> handleConflict(DataIntegrityViolationException error) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", Instant.now().toString(), "code", "SEAT_UNAVAILABLE",
                "message", "Ce siège vient d’être réservé. Choisissez un autre siège."));
    }
}

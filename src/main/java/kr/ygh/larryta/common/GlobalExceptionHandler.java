package kr.ygh.larryta.common;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    Map<String, String> fieldMessages =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    error -> Optional.ofNullable(error.getDefaultMessage()).orElse("")));
    detail.setProperty("fields", fieldMessages);

    return detail;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(NoHandlerFoundException.class)
  public ProblemDetail handleNoEndpoint() {
    return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  public ProblemDetail handle(RuntimeException ex) {
    log.error(ex.getMessage(), ex);
    return ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

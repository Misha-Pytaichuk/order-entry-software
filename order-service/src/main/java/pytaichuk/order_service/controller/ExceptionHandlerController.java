package pytaichuk.order_service.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ValidationException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pytaichuk.order_service.exception.FindException;
import pytaichuk.order_service.exception.ResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<ResponseException> personExceptionHandler(ValidationException validationException){
        ResponseException responseException = new ResponseException(
                validationException.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(responseException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FindException.class)
    private ResponseEntity<ResponseException> personExceptionHandler(FindException findException){
        ResponseException responseException = new ResponseException(
                findException.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(responseException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({WebClientResponseException.class, CallNotPermittedException.class})
    private ResponseEntity<ResponseException> personExceptionHandler(Exception exception){

        String message = null;

        if(exception instanceof WebClientResponseException){
            message = "Something wrong, try again...";
        } else if(exception instanceof CallNotPermittedException){
            message = "Service is currently not available due to circuit breaker policy.";
        } else if(exception instanceof TimeoutException){
            message = "Timeout... Retry..";
        }

        ResponseException responseException = new ResponseException(
                message,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(responseException, HttpStatus.SERVICE_UNAVAILABLE);
    }
}

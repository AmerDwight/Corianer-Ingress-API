package tw.amer.cia.core.component.structural.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataSourceAccessException.class)
    public ResponseEntity<Object> handleDataSourceAccessException(DataSourceAccessException e) {
        HttpStatus httpStatus = e.getHttpStatus() == null ? HttpStatus.SERVICE_UNAVAILABLE : e.getHttpStatus();

        return new ResponseEntity<>(e.getMessage(), httpStatus);
    }

    @ExceptionHandler(GatewayControllerException.class)
    public ResponseEntity<Object> handleGatewayControllerException(GatewayControllerException e) {
        HttpStatus httpStatus = e.getHttpStatus() == null ? HttpStatus.SERVICE_UNAVAILABLE : e.getHttpStatus();

        return new ResponseEntity<>(e.getMessage(), httpStatus);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException e) {
        String sb = e.getMessage() + "/n" +
                e.getLocalizedMessage();
        return new ResponseEntity<>(sb, e.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors.stream()
                .map(s -> s.getDefaultMessage()).collect(Collectors.joining(";"));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<Object> handleURISyntaxException(URISyntaxException e) {
        String sb = e.getClass().getName() + System.lineSeparator() +
                e.getMessage();
        return new ResponseEntity<>(sb, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<Object> handleJsonMappingException(JsonMappingException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error processing submitted data. Please verify your input.");

        if (e.getPath() != null && !e.getPath().isEmpty()) {
            sb.append(System.lineSeparator()).append("Issue details:");
            for (JsonMappingException.Reference reference : e.getPath()) {
                if (reference.getFieldName() != null) {
                    sb.append(System.lineSeparator()).append("- Field '")
                            .append(reference.getFieldName())
                            .append("' has an issue.");
                }
            }
        }
        sb.append(System.lineSeparator()).append("Common issues include incorrect data types, unexpected characters, or format errors. Please check the values you provided.");

        sb.append(System.lineSeparator()).append("Detailed error: ").append(e.getOriginalMessage());

        return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
    }
}


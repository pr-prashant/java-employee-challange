package com.reliaquest.api.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private ObjectMapper mapper;

    @ExceptionHandler({ HttpStatusCodeException.class })
    public ResponseEntity<Object> handleHttpStatusError(final HttpStatusCodeException e, final WebRequest request) {
        ApiResponse<String> response = new ApiResponse<>();
        if (HttpStatus.TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
            response.setError("Too many request. Please try after some time.");
            response.setStatus("FAILED");
            return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS);
        } else {
            if (!ObjectUtils.isEmpty(e.getResponseBodyAsString())) {
                ErrorResponse errorResponse = parseExceptionMessageByClass(e.getResponseBodyAsString(), ErrorResponse.class);
                if (errorResponse != null)
                    response.setError(errorResponse.getError());
                else
                    response.setError(e.getMessage());
            }
            response.setStatus("FAILED");
            return new ResponseEntity<>(response, new HttpHeaders(), e.getStatusCode());
        }
    }

    @ExceptionHandler({ ApiException.class })
    public ResponseEntity<Object> handleApiExceptionError(final ApiException e, final WebRequest request) {
        ApiResponse<String> response = new ApiResponse<>();
        response.setError(e.getMessage());
        response.setStatus("FAILED");
        return new ResponseEntity<>(response, new HttpHeaders(), e.getStatus());
    }

    private <T> T parseExceptionMessageByClass(String message, Class<T> className) {
        try {
            String finalMessage = message.substring(message.indexOf("{"), message.lastIndexOf("}") + 1);
            return mapper.readValue(finalMessage, className);
        } catch (Exception ex) {
            return null;
        }
    }
}
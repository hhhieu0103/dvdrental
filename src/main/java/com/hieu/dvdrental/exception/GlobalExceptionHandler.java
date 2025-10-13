package com.hieu.dvdrental.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Entity Not Found");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(EntityExistsException.class)
    public ProblemDetail handleEntityExistsException(EntityExistsException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Entity Exists");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("One or more fields are invalid. Check the properties for more details");
        problemDetail.setProperties(fieldErrors);

        return ResponseEntity.status(status).body(problemDetail);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> parameterErrors = new HashMap<>();

        Map<String, Object> bodyErrors = new HashMap<>();
        Map<String, Object> pathVariableErrors = new HashMap<>();
        Map<String, Object> requestParamErrors = new HashMap<>();

        ex.getParameterValidationResults().forEach(result -> {

            var parameter = result.getMethodParameter();
            boolean isBody = parameter.hasParameterAnnotation(RequestBody.class);
            boolean isPathVariable = parameter.hasParameterAnnotation(PathVariable.class);

            result.getResolvableErrors().forEach(err -> {
                if (isBody) {

                    String fieldName;
                    if (err instanceof FieldError fieldError) {
                        fieldName = fieldError.getField();
                    } else {
                        fieldName = parameter.getParameterName();
                    }

                    bodyErrors.put(fieldName, err.getDefaultMessage());

                } else if (isPathVariable) {
                    pathVariableErrors.put(result.getMethodParameter().getParameterName(), err.getDefaultMessage());
                } else {
                    requestParamErrors.put(result.getMethodParameter().getParameterName(), err.getDefaultMessage());
                }
            });
        });

        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("One or more fields are invalid. Check the properties for more details");

        if (!bodyErrors.isEmpty()) {
            parameterErrors.put("body", bodyErrors);
        }
        if (!pathVariableErrors.isEmpty()) {
            parameterErrors.put("pathVariable", pathVariableErrors);
        }
        if (!requestParamErrors.isEmpty()) {
            parameterErrors.put("requestParam", requestParamErrors);
        }
        problemDetail.setProperties(parameterErrors);

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("An unexpected error occurred");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }
}

package com.piseth.java.school.ownerservice.exceptions;



import java.net.URI;
import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


	@ExceptionHandler(OwnerNotFoundException.class)
    public Mono<ProblemDetail> handleNotFound(OwnerNotFoundException ex, ServerWebExchange exchange) {
        log.warn("Owner not found. path={}", exchange.getRequest().getPath(), ex);
        return Mono.just(problem(exchange, HttpStatus.NOT_FOUND, "Owner not found", ex.getMessage(), "/errors/owner-not-found"));
    }
    
    @ExceptionHandler(BadRequestException.class)
    public Mono<ProblemDetail> handleBadRequest(BadRequestException ex, ServerWebExchange exchange) {
        log.warn("Bad request. path={}", exchange.getRequest().getPath(), ex);
        return Mono.just(problem(exchange, HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage(), "/errors/bad-request"));
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ProblemDetail> handleConflict(DataIntegrityViolationException ex, ServerWebExchange exchange) {
        log.warn("Data integrity violation. path={}", exchange.getRequest().getPath(), ex);

        return Mono.just(problem(exchange, HttpStatus.CONFLICT,
            "Conflict", "Duplicate or invalid data.", "/errors/conflict"));
    }

    

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ProblemDetail> handleValidation(WebExchangeBindException ex, ServerWebExchange exchange) {
        log.warn("Validation error. path={}", exchange.getRequest().getPath(), ex);

        String message = "Validation failed.";
        if (!ex.getAllErrors().isEmpty() && ex.getAllErrors().get(0).getDefaultMessage() != null) {
            message = ex.getAllErrors().get(0).getDefaultMessage();
        }

        return Mono.just(problem(exchange, HttpStatus.BAD_REQUEST, "Validation error", message, "/errors/validation-error"));
    }
    
    

    @ExceptionHandler(Exception.class)
    public Mono<ProblemDetail> handleGeneric(Exception ex, ServerWebExchange exchange) {
        // IMPORTANT: log the stacktrace
        log.error("Unhandled exception. path={}", exchange.getRequest().getPath(), ex);

        return Mono.just(problem(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal error", "Unexpected error occurred.", "/errors/internal-error"));
    }

    private ProblemDetail problem(ServerWebExchange exchange, HttpStatus status, String title, String detail, String typePath) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(typePath));

        // instance = request path (nice for debugging)
        pd.setInstance(URI.create(exchange.getRequest().getPath().value()));

        // add timestamp + traceId
        pd.setProperty("timestamp", Instant.now().toString());
        pd.setProperty("traceId", exchange.getRequest().getId());

        return pd;
    }
}
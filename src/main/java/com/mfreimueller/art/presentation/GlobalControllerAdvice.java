package com.mfreimueller.art.presentation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {
    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleException(Throwable t) {
        log.error("Unhandled throwable: {}", t.getMessage(), t);

        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail(t.getMessage() != null ? t.getMessage() : "An internal error occurred. Sorry :(");

        return ResponseEntity.internalServerError().body(problemDetail);
    }
}

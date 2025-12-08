package com.mfreimueller.art.controller;

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
    public ResponseEntity<ProblemDetail> handleException(Throwable throwable) {
        log.error("Unexpected exception occurred.", throwable);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail("Unexpected error occurred.");
        return ResponseEntity.internalServerError().body(problemDetail);
    }

}

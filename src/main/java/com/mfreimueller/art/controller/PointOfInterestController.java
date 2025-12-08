package com.mfreimueller.art.controller;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.dto.PointOfInterestDto;
import com.mfreimueller.art.service.PointOfInterestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/pointsOfInterest")
@Slf4j
public class PointOfInterestController {

    private final PointOfInterestService pointOfInterestService;

    @GetMapping
    public ResponseEntity<List<PointOfInterestDto>> findAll() {
        var pois = pointOfInterestService.getAll();
        return (pois.isEmpty()) ?  ResponseEntity.noContent().build() : ResponseEntity.ok(pois);
    }

    @PostMapping
    public ResponseEntity<PointOfInterestDto> createNew(@RequestBody CreatePointOfInterestCommand cmd) {
        var poi = pointOfInterestService.create(cmd);
        // what makes sense here?
        return poi == null ? ResponseEntity.unprocessableEntity().build() : ResponseEntity.ok(poi);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleException(Throwable throwable) {
        log.error("Unexpected exception occurred in PointOfInterestController.", throwable);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail("Unexpected error occurred.");
        return ResponseEntity.internalServerError().body(problemDetail);
    }

}

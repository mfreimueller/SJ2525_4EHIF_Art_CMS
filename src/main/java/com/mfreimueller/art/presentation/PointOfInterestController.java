package com.mfreimueller.art.presentation;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.commands.UpdatePointOfInterestCommand;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.PointOfInterestDto;
import com.mfreimueller.art.mappers.PointOfInterestMapper;
import com.mfreimueller.art.service.PointOfInterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mfreimueller.art.util.LogHelper.logEnter;
import static com.mfreimueller.art.util.LogHelper.logExit;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Slf4j

@RestController
@RequestMapping("/api/pois")
public class PointOfInterestController {
    private final PointOfInterestService service;
    private final PointOfInterestMapper mapper;

    @PostMapping
    public ResponseEntity<PointOfInterestDto> createPointOfInterest(
            @Valid
            @RequestBody CreatePointOfInterestCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var poi = service.create(cmd);
        log.trace("Created PointOfInterest with id: {}", poi.getId());
        var location = linkTo(methodOn(PointOfInterestController.class).getPointOfInterest(poi.getId().id()))
                .withSelfRel();

        logExit(log);

        return ResponseEntity.created(location.toUri()).body(mapper.toDto(poi));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deletePointOfInterest(@PathVariable Long key) {
        logEnter(log);
        log.trace("Deleting PointOfInterest with key: {}", key);

        ResponseEntity<Void> result = service.delete(new PointOfInterest.PointOfInterestId(key)) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();

        logExit(log);
        return result;
    }

    @GetMapping("/{key}")
    public ResponseEntity<PointOfInterestDto> getPointOfInterest(@PathVariable Long key) {
        logEnter(log);
        log.trace("Getting PointOfInterest with key: {}", key);
        var result = service
                .getPointOfInterest(new PointOfInterest.PointOfInterestId(key))
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

        logExit(log);
        return result;
    }

    @GetMapping
    public ResponseEntity<List<PointOfInterestDto>> getPointsOfInterest() {
        logEnter(log);

        var pois = service.getPointsOfInterest();

        log.info("Retrieved {} PointOfInterest entities", pois.size());
        logExit(log);

        return pois.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pois.stream().map(mapper::toDto).toList());
    }

    @PutMapping("/{key}")
    public ResponseEntity<PointOfInterestDto> replacePointOfInterest(
            @PathVariable Long key, @Valid
            @RequestBody UpdatePointOfInterestCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);
        log.trace("Replacing PointOfInterest with key: {}", key);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var result = service.replace(new PointOfInterest.PointOfInterestId(key), cmd);
        if (result.isEmpty()) {
            log.info("PointOfInterest to replace not found");
            return ResponseEntity.notFound().build();
        }
        var poi = result.get();

        log.trace("Replaced PointOfInterest with id: {}", poi.getId());
        logExit(log);

        var location = linkTo(methodOn(PointOfInterestController.class).getPointOfInterest(poi.getId().id()))
                .withSelfRel();
        return ResponseEntity.ok().location(location.toUri()).body(mapper.toDto(poi));
    }

    @PatchMapping("/{key}")
    public ResponseEntity<PointOfInterestDto> patchPointOfInterest(
            @PathVariable Long key, @Valid
            @RequestBody UpdatePointOfInterestCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);
        log.trace("Patching PointOfInterest with key: {}", key);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var result = service.update(new PointOfInterest.PointOfInterestId(key), cmd);
        if (result.isEmpty()) {
            log.info("PointOfInterest to patch not found");
            return ResponseEntity.notFound().build();
        }
        var poi = result.get();

        log.trace("Patched PointOfInterest with id: {}", poi.getId());
        logExit(log);

        var location = linkTo(methodOn(PointOfInterestController.class).getPointOfInterest(poi.getId().id()))
                .withSelfRel();
        return ResponseEntity.ok().location(location.toUri()).body(mapper.toDto(poi));
    }
}

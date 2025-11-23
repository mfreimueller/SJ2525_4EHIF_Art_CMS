package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.commands.UpdatePointOfInterestCommand;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.PointOfInterestRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
@Slf4j
public class PointOfInterestService {

    private final PointOfInterestRepository pointOfInterestRepository;
    private final ContentService contentService;
    private final CreatorService creatorService;
    private final DateTimeFactory dateTimeFactory;

    @Transactional(readOnly = false)
    public PointOfInterest create(@NotNull @Valid CreatePointOfInterestCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());
        var content = cmd.content().stream().map(contentService::getByReference).toList();

        var poi = PointOfInterest.builder()
                .title(cmd.title())
                .description(cmd.description())
                .content(content)
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        var saved = pointOfInterestRepository.save(poi);
        log.debug("Created new point of interest with id {}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = false)
    public PointOfInterest update(@NotNull PointOfInterest.PointOfInterestId id, @NotNull @Valid UpdatePointOfInterestCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());
        var content = cmd.content().stream().map(contentService::getByReference).toList();

        var poi = pointOfInterestRepository.getReferenceById(id); // TODO: handle exception
        poi.setTitle(cmd.title());
        poi.setDescription(cmd.description());
        poi.setContent(content);
        poi.setUpdatedBy(creator);
        poi.setUpdatedAt(dateTimeFactory.now());

        var saved = pointOfInterestRepository.save(poi);
        log.debug("Updated point of interest with id {}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull PointOfInterest.PointOfInterestId id) {
        pointOfInterestRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
        log.debug("Deleted point of interest with id {}", id.id());
    }

    public PointOfInterest getByReference(@NotNull PointOfInterest.PointOfInterestId id) {
        return pointOfInterestRepository.getReferenceById(id);
    }

}

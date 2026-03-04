package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.commands.UpdatePointOfInterestCommand;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.PointOfInterestDto;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.PointOfInterestRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.mfreimueller.art.util.LogHelper.logEnter;
import static com.mfreimueller.art.util.LogHelper.logExit;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
@Slf4j
public class PointOfInterestService {

    private final PointOfInterestRepository pointOfInterestRepository;
    private final ContentService contentService;
    private final CreatorService creatorService;
    private final DateTimeFactory dateTimeFactory;

    @Transactional
    public PointOfInterest create(@NotNull @Valid CreatePointOfInterestCommand cmd) {
        logEnter(log);
        log.trace("PointOfInterest creation command: {}", cmd);

        var creator = creatorService.getByReference(cmd.creatorId());
        log.info("Creating PointOfInterest with creator id: {}", cmd.creatorId());

        if (creator == null) {
            log.error("Invalid creator of PointOfInterest");
            throw new IllegalArgumentException("Could not find user with id: " + cmd.creatorId());
        }

        var content = cmd.content().stream().map(contentService::getByReference).toList();

        var poi = PointOfInterest.builder()
                .title(cmd.title())
                .description(cmd.description())
                .content(content)
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        var saved = pointOfInterestRepository.save(poi);
        log.info("PointOfInterest created and saved with id {}", saved.getId());
        logExit(log);

        return saved;
    }

    @Transactional
    public Optional<PointOfInterest> update(@NotNull PointOfInterest.PointOfInterestId id, @NotNull @Valid UpdatePointOfInterestCommand cmd) {
        logEnter(log);
        log.trace("Updating PointOfInterest {} with {}", id, cmd);

        return pointOfInterestRepository.findById(id)
                .map(poi -> {
                    var creator = creatorService.getByReference(cmd.creatorId());
                    log.info("Updating PointOfInterest with creator id: {}", cmd.creatorId());

                    if (creator == null) {
                        log.error("Invalid creator of PointOfInterest");
                        throw new IllegalArgumentException("Could not find user with id: " + cmd.creatorId());
                    }

                    if (cmd.title() != null) {
                        poi.setTitle(cmd.title());
                    }

                    if (cmd.description() != null) {
                        poi.setDescription(cmd.description());
                    }

                    if (cmd.content() != null) {
                        var content = cmd.content().stream().map(contentService::getByReference).toList();
                        poi.setContent(content);
                    }

                    // this part is mandatory:
                    poi.setUpdatedBy(creator);
                    poi.setUpdatedAt(dateTimeFactory.now());

                    log.debug("Updated PointOfInterest {} successfully", poi.getId());
                    logExit(log);

                    return poi;
                });
    }

    @Transactional
    public Optional<PointOfInterest> replace(@NotNull PointOfInterest.PointOfInterestId id, @NotNull @Valid UpdatePointOfInterestCommand cmd) {
        logEnter(log);
        log.trace("Replacing PointOfInterest {} with {}", id, cmd);

        return pointOfInterestRepository.findById(id)
                .map(poi -> {
                    var creator = creatorService.getByReference(cmd.creatorId());
                    log.info("Replacing PointOfInterest with creator id: {}", cmd.creatorId());

                    if (creator == null) {
                        log.error("Invalid creator of PointOfInterest");
                        throw new IllegalArgumentException("Could not find user with id: " + cmd.creatorId());
                    }

                    var content = cmd.content().stream().map(contentService::getByReference).toList();

                    poi.setTitle(cmd.title());
                    poi.setDescription(cmd.description());
                    poi.setContent(content);
                    poi.setUpdatedBy(creator);
                    poi.setUpdatedAt(dateTimeFactory.now());

                    log.debug("Replaced PointOfInterest {} successfully", poi.getId());
                    logExit(log);

                   return poi;
                });
    }

    @Transactional
    public boolean delete(@NotNull PointOfInterest.PointOfInterestId id) {
        logEnter(log);
        log.trace("Deleting PointOfInterest with id: {}", id);

        if (!pointOfInterestRepository.existsById(id)) {
            log.info("PointOfInterest with id {} not found", id);
            return false;
        }

        pointOfInterestRepository.deleteById(id);

        log.debug("Deleted point of interest with id {}", id.id());
        logExit(log);

        return true;
    }

    public PointOfInterest getByReference(@NotNull PointOfInterest.PointOfInterestId id) {
        return pointOfInterestRepository.getReferenceById(id);
    }

    public Optional<PointOfInterest> getPointOfInterest(@NotNull PointOfInterest.PointOfInterestId id) {
        return pointOfInterestRepository.findById(id);
    }

    public List<PointOfInterest> getPointsOfInterest() {
        return pointOfInterestRepository.findAll();
    }

}

package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateVisitHistoryCommand;
import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.persistence.VisitHistoryRepository;
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
public class VisitHistoryService {

    private final VisitHistoryRepository visitHistoryRepository;
    private final VisitorService visitorService;

    @Transactional(readOnly = false)
    public VisitHistory create(@NotNull @Valid CreateVisitHistoryCommand cmd) {
        var visitor = visitorService.getByReference(cmd.visitorId());

        var visitHistory = VisitHistory.builder()
                .visitedOn(cmd.visitedOn())
                .duration(cmd.duration())
                .pointsOfInterest(cmd.pointsOfInterest())
                .visitor(visitor)
                .build();

        var saved = visitHistoryRepository.save(visitHistory);
        log.debug("Created new visit history of visitor {} with id {}", visitor.getId(), saved.getId());

        return saved;
    }

    public VisitHistory getByReference(@NotNull VisitHistory.VisitHistoryId id) {
        return visitHistoryRepository.getReferenceById(id);
    }

}

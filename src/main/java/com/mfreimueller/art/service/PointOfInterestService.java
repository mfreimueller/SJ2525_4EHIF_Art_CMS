package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.PointOfInterestRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class PointOfInterestService {

    private final PointOfInterestRepository pointOfInterestRepository;
    private final DateTimeFactory dateTimeFactory;

    @Transactional(readOnly = false)
    public PointOfInterest create(@NotNull @Valid CreatePointOfInterestCommand cmd) {
        var poi = PointOfInterest.builder()
                .title(cmd.title())
                .description(cmd.description())
                .createdAt(dateTimeFactory.now())
                .build();

        return pointOfInterestRepository.save(poi);
    }

}

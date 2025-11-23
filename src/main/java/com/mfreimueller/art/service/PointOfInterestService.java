package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.commands.UpdateExhibitionCommand;
import com.mfreimueller.art.commands.UpdatePointOfInterestCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Exhibition;
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

    @Transactional(readOnly = false)
    public PointOfInterest update(@NotNull PointOfInterest.PointOfInterestId id, @NotNull @Valid UpdatePointOfInterestCommand cmd) {
        var exhibition = pointOfInterestRepository.getReferenceById(id); // TODO: handle exception
        exhibition.setTitle(cmd.title());
        exhibition.setDescription(cmd.description());
        exhibition.setUpdatedAt(dateTimeFactory.now());

        return pointOfInterestRepository.save(exhibition);
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull PointOfInterest.PointOfInterestId id) {
        pointOfInterestRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
    }

    public PointOfInterest getByReference(@NotNull PointOfInterest.PointOfInterestId id) {
        return pointOfInterestRepository.getReferenceById(id);
    }

}

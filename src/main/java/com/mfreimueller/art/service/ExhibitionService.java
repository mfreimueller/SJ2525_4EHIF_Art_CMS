package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateExhibitionCommand;
import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.ExhibitionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class ExhibitionService {
    private final ExhibitionRepository exhibitionRepository;
    private final DateTimeFactory dateTimeFactory;

    @Transactional(readOnly = false)
    public Exhibition create(@NotNull @Valid CreateExhibitionCommand cmd) {
        var exhibition = Exhibition.builder()
                .title(cmd.title())
                .pointsOfInterest(cmd.pointsOfInterests())
                .subCollections(cmd.subCollections())
                .languages(cmd.languages())
                .createdAt(dateTimeFactory.now())
                .build();

        return exhibitionRepository.save(exhibition);
    }

    public Exhibition getByReference(Exhibition.CollectionId id) {
        return exhibitionRepository.getReferenceById(id);
    }
}

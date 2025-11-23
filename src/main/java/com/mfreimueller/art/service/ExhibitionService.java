package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateExhibitionCommand;
import com.mfreimueller.art.commands.UpdateExhibitionCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.ExhibitionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class ExhibitionService extends AbstractCollectionService<Exhibition> {
    private final ExhibitionRepository exhibitionRepository;
    private final DateTimeFactory dateTimeFactory;
    private final PointOfInterestService pointOfInterestService;
    private final CreatorService creatorService;

    @Transactional(readOnly = false)
    public Exhibition create(@NotNull @Valid CreateExhibitionCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var exhibition = Exhibition.builder()
                .title(cmd.title())
                .languages(cmd.languages())
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        return exhibitionRepository.save(exhibition);
    }

    @Transactional(readOnly = false)
    public Exhibition update(@NotNull Collection.CollectionId id, @NotNull @Valid UpdateExhibitionCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var exhibition = exhibitionRepository.getReferenceById(id); // TODO: handle exception
        exhibition.setTitle(cmd.title());
        exhibition.setLanguages(cmd.languages());

        exhibition.setUpdatedAt(dateTimeFactory.now());
        exhibition.setUpdatedBy(creator);

        return exhibitionRepository.save(exhibition);
    }

    @Override
    protected JpaRepository<Exhibition, Collection.CollectionId> getRepository() {
        return exhibitionRepository;
    }

    @Override
    public PointOfInterestService getPointOfInterestService() {
        return pointOfInterestService;
    }

    @Override
    public DateTimeFactory getDateTimeFactory() {
        return dateTimeFactory;
    }

    @Override
    public CreatorService getCreatorService() {
        return creatorService;
    }
}

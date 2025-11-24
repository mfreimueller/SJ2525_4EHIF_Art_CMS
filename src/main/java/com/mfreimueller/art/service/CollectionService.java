package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateCollectionCommand;
import com.mfreimueller.art.commands.UpdateCollectionCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.CollectionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class CollectionService extends AbstractCollectionService<Collection> {
    private final CollectionRepository collectionRepository;
    private final DateTimeFactory dateTimeFactory;
    private final PointOfInterestService pointOfInterestService;
    private final CreatorService creatorService;

    @Transactional(readOnly = false)
    public Collection create(@NotNull @Valid CreateCollectionCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var collection = Collection.builder()
                .title(cmd.title())
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        return collectionRepository.save(collection);
    }

    @Transactional(readOnly = false)
    public Collection update(@NotNull Collection.CollectionId id, @NotNull @Valid UpdateCollectionCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var collection = collectionRepository.getReferenceById(id);
        collection.setTitle(cmd.title());

        collection.setUpdatedAt(dateTimeFactory.now());
        collection.setUpdatedBy(creator);

        return collectionRepository.save(collection);
    }

    @Override
    protected JpaRepository<Collection, Collection.CollectionId> getRepository() {
        return collectionRepository;
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
    protected CreatorService getCreatorService() {
        return creatorService;
    }
}

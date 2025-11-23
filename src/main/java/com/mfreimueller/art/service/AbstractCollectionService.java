package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.AddPointOfInterestCommand;
import com.mfreimueller.art.commands.AddSubcollectionCommand;
import com.mfreimueller.art.commands.RemovePointOfInterestCommand;
import com.mfreimueller.art.commands.RemoveSubcollectionCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.foundation.DataConstraintException;
import com.mfreimueller.art.foundation.DateTimeFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractCollectionService<T extends Collection> {

    @Transactional(readOnly = false)
    public Collection addPointOfInterest(@NotNull Collection.CollectionId id, @NotNull @Valid AddPointOfInterestCommand cmd) {
        var collection = getRepository().getReferenceById(id); // TODO: handle exception
        var poi = getPointOfInterestService().getByReference(cmd.poiId()); // TODO: handle exception

        var pois = collection.getPointsOfInterest();
        if (pois.contains(poi)) {
            log.error("Attempted to add collection with id {} to itself, introducing a circular reference", id.id());
            throw DataConstraintException.forDuplicatedEntry(PointOfInterest.class, poi.getId().id());
        }

        var creator = getCreatorService().getByReference(cmd.creatorId()); // TODO: handle exception

        pois.add(poi);

        collection.setUpdatedAt(getDateTimeFactory().now());
        collection.setUpdatedBy(creator);

        var saved = getRepository().save(collection);
        log.debug("Added point of interest {} to collection {}", cmd.poiId(), id.id());

        return saved;
    }

    @Transactional(readOnly = false)
    public Collection removePointOfInterest(@NotNull Collection.CollectionId id, @NotNull @Valid RemovePointOfInterestCommand cmd) {
        var collection = getRepository().getReferenceById(id); // TODO: handle exception
        var poi = getPointOfInterestService().getByReference(cmd.poiId()); // TODO: handle exception

        var pois = collection.getPointsOfInterest();
        if (!pois.contains(poi)) {
            log.error("Attempted to remove a point of interest with id {} from collection {}, although they weren't connected", poi.getId(), id.id());
            throw DataConstraintException.forMissingEntry(PointOfInterest.class, poi.getId().id());
        }

        var creator = getCreatorService().getByReference(cmd.creatorId()); // TODO: handle exception

        pois.remove(poi);

        collection.setUpdatedAt(getDateTimeFactory().now());
        collection.setUpdatedBy(creator);

        var saved = getRepository().save(collection);
        log.debug("Removed point of interest {} from collection {}", cmd.poiId(), id.id());

        return saved;
    }

    @Transactional(readOnly = false)
    public Collection addSubcollection(@NotNull Collection.CollectionId id, @NotNull @Valid AddSubcollectionCommand cmd) {
        if (id.equals(cmd.subcollectionId())) {
            throw DataConstraintException.forCircularReference(Collection.class, id.id());
        }

        var collection = getRepository().getReferenceById(id); // TODO: handle exception
        var collectionToAdd = getRepository().getReferenceById(cmd.subcollectionId()); // TODO: handle exception

        var topCollection = collection.getTopCollection();
        if (topCollection == collectionToAdd || topCollection.contains(collectionToAdd)) {
            log.error("Attempted to add collection with id {} to already connected collection {}", collectionToAdd.getId(), id.id());
            throw DataConstraintException.forCircularReference(Collection.class, cmd.subcollectionId().id());
        }

        var subcollections = collection.getSubCollections();
        var creator = getCreatorService().getByReference(cmd.creatorId()); // TODO: handle exception

        subcollections.add(collectionToAdd);

        collection.setUpdatedAt(getDateTimeFactory().now());
        collection.setUpdatedBy(creator);

        var saved = getRepository().save(collection);
        log.debug("Added subcollection {} to collection {}", cmd.subcollectionId(), id.id());

        return saved;
    }

    @Transactional(readOnly = false)
    public Collection removeSubcollection(@NotNull Collection.CollectionId id, @NotNull @Valid RemoveSubcollectionCommand cmd) {
        var collection = getRepository().getReferenceById(id); // TODO: handle exception
        var collectionToRemove = getRepository().getReferenceById(cmd.collectionId()); // TODO: handle exception

        var collections = collection.getSubCollections();
        if (!collections.contains(collectionToRemove)) {
            log.error("Attempted to remove a collection {} from parent-collection {}, although they weren't connected", collection.getId(), id.id());
            throw DataConstraintException.forMissingEntry(Collection.class, collectionToRemove.getId().id());
        }

        var creator = getCreatorService().getByReference(cmd.creatorId()); // TODO: handle exception

        collections.remove(collectionToRemove);

        collection.setUpdatedAt(getDateTimeFactory().now());
        collection.setUpdatedBy(creator);

        var saved = getRepository().save(collection);
        log.debug("Removed subcollection {} from collection {}", cmd.collectionId(), id.id());

        return saved;
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Collection.CollectionId id) {
        getRepository().deleteById(id); // NOTE: this doesn't fail on entity not found
        log.debug("Deleted collection {}", id.id());
    }

    public T getByReference(Collection.CollectionId id) {
        return getRepository().getReferenceById(id);
    }

    protected abstract JpaRepository<T, Collection.CollectionId> getRepository();

    protected abstract PointOfInterestService getPointOfInterestService();

    protected abstract CreatorService getCreatorService();

    protected abstract DateTimeFactory getDateTimeFactory();
}

package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.AddPointOfInterestCommand;
import com.mfreimueller.art.commands.AddSubcollectionCommand;
import com.mfreimueller.art.commands.RemovePointOfInterestCommand;
import com.mfreimueller.art.commands.RemoveSubcollectionCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.foundation.DataConstraintException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public abstract class AbstractCollectionService<T extends Collection> {

    @Transactional(readOnly = false)
    public Collection addPointOfInterest(@NotNull Collection.CollectionId id, @NotNull @Valid AddPointOfInterestCommand cmd) {
        var collection = getRepository().getReferenceById(id); // TODO: handle exception
        var poi = getPointOfInterestService().getByReference(cmd.poiId()); // TODO: handle exception

        var pois = collection.getPointsOfInterest();
        if (pois.contains(poi)) {
            throw DataConstraintException.forDuplicatedEntry(PointOfInterest.class, poi.getId().id());
        }

        pois.add(poi);
        return getRepository().save(collection);
    }

    @Transactional(readOnly = false)
    public Collection removePointOfInterest(@NotNull Collection.CollectionId id, @NotNull @Valid RemovePointOfInterestCommand cmd) {
        var collection = getRepository().getReferenceById(id); // TODO: handle exception
        var poi = getPointOfInterestService().getByReference(cmd.poiId()); // TODO: handle exception

        var pois = collection.getPointsOfInterest();
        if (!pois.contains(poi)) {
            throw DataConstraintException.forMissingEntry(PointOfInterest.class, poi.getId().id());
        }

        pois.remove(poi);
        return getRepository().save(collection);
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
            throw DataConstraintException.forCircularReference(Collection.class, cmd.subcollectionId().id());
        }

        var subcollections = collection.getSubCollections();
        if (subcollections.contains(collectionToAdd)) {
            throw DataConstraintException.forDuplicatedEntry(Collection.class, collectionToAdd.getId().id());
        }

        subcollections.add(collectionToAdd);
        return getRepository().save(collection);
    }

    @Transactional(readOnly = false)
    public Collection removeSubcollection(@NotNull Collection.CollectionId id, @NotNull @Valid RemoveSubcollectionCommand cmd) {
        var collection = getRepository().getReferenceById(id); // TODO: handle exception
        var collectionToRemove = getRepository().getReferenceById(cmd.collectionId()); // TODO: handle exception

        var collections = collection.getSubCollections();
        if (!collections.contains(collectionToRemove)) {
            throw DataConstraintException.forMissingEntry(Collection.class, collectionToRemove.getId().id());
        }

        collections.remove(collectionToRemove);
        return getRepository().save(collection);
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Collection.CollectionId id) {
        getRepository().deleteById(id); // NOTE: this doesn't fail on entity not found
    }

    public T getByReference(Collection.CollectionId id) {
        return getRepository().getReferenceById(id);
    }

    protected abstract JpaRepository<T, Collection.CollectionId> getRepository();

    protected abstract PointOfInterestService getPointOfInterestService();
}

package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.*;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.domain.Language;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.foundation.DataConstraintException;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.CollectionRepository;
import com.mfreimueller.art.persistence.ExhibitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assumptions.assumeThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    private @InjectMocks CollectionService service;
    private @Mock CollectionRepository repository;
    private @Mock PointOfInterestService pointOfInterestService;
    private @Mock DateTimeFactory dateTimeFactory;

    @BeforeEach
    public void setup() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
        assumeThat(pointOfInterestService).isNotNull();
        assumeThat(dateTimeFactory).isNotNull();
    }

    @Test
    public void can_create_with_valid_data() {
        var en = new Language("en", "English");

        var cmd = CreateCollectionCommand.builder()
                .title(Map.of(en, "Dauerausstellung"))
                .build();

        when(dateTimeFactory.now()).thenReturn(ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()));
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var collection = service.create(cmd);

        assertNotNull(collection);
        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var collection = createCollection();
        var de = new Language("de", "Deutsch");

        var cmd = UpdateCollectionCommand.builder()
                .title(Map.of(de, "Dauerausstellung"))
                .build();

        var dateTime = ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault());

        when(repository.getReferenceById(any())).thenReturn(collection);
        when(dateTimeFactory.now()).thenReturn(dateTime);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var updateCollection = service.update(collection.getId(), cmd);

        assertNotNull(updateCollection);
        assertEquals(updateCollection.getUpdatedAt(), dateTime);
        assertTrue(updateCollection.getTitle().containsKey(de));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new Collection.CollectionId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var collection = createCollection();
        when(repository.getReferenceById(any())).thenReturn(collection);

        var returned = service.getByReference(new Collection.CollectionId(1L));

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(collection.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }

    @Test
    public void can_add_point_of_interest() {
        var collection = createCollection();
        when(repository.getReferenceById(any())).thenReturn(collection);

        var poi = createPointOfInterest();
        when(pointOfInterestService.getByReference(any())).thenReturn(poi);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var cmd = AddPointOfInterestCommand.builder().poiId(poi.getId()).build();

        var returned = service.addPointOfInterest(collection.getId(), cmd);

        assertThat(returned.getPointsOfInterest(), hasSize(1));
        assertThat(returned.getPointsOfInterest(), hasItem(poi));

        verify(repository, times(1)).getReferenceById(any());
    }

    @Test
    public void can_not_add_point_of_interest_twice() {
        var collection = createCollection();
        when(repository.getReferenceById(any())).thenReturn(collection);

        var poi = createPointOfInterest();
        collection.getPointsOfInterest().add(poi);

        var duplicate = createPointOfInterest();
        when(pointOfInterestService.getByReference(any())).thenReturn(duplicate);

        var cmd = AddPointOfInterestCommand.builder().poiId(poi.getId()).build();

        assertThrows(DataConstraintException.class, () -> service.addPointOfInterest(collection.getId(), cmd));
    }

    @Test
    public void can_remove_point_of_interest() {
        var collection = createCollection();
        when(repository.getReferenceById(any())).thenReturn(collection);

        var poi = createPointOfInterest();
        collection.getPointsOfInterest().add(poi);

        when(pointOfInterestService.getByReference(any())).thenReturn(poi);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var cmd = RemovePointOfInterestCommand.builder().poiId(poi.getId()).build();

        var returned = service.removePointOfInterest(collection.getId(), cmd);

        assertThat(returned.getPointsOfInterest(), hasSize(0));

        verify(repository, times(1)).getReferenceById(any());
    }

    @Test
    public void can_not_remove_non_existing_point_of_interest() {
        var collection = createCollection();
        when(repository.getReferenceById(any())).thenReturn(collection);

        var poi = createPointOfInterest();

        var duplicate = createPointOfInterest();
        when(pointOfInterestService.getByReference(any())).thenReturn(duplicate);

        var cmd = RemovePointOfInterestCommand.builder().poiId(poi.getId()).build();

        assertThrows(DataConstraintException.class, () -> service.removePointOfInterest(collection.getId(), cmd));
    }

    @Test
    public void can_add_subcollection() {
        var collection = createCollection();
        var subcollection = createSubcollection();

        when(repository.getReferenceById(collection.getId())).thenReturn(collection);
        when(repository.getReferenceById(subcollection.getId())).thenReturn(subcollection);

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var cmd = AddSubcollectionCommand.builder().subcollectionId(subcollection.getId()).build();

        var returned = service.addSubcollection(collection.getId(), cmd);

        assertThat(returned.getSubCollections(), hasSize(1));
        assertThat(returned.getSubCollections(), hasItem(subcollection));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_not_add_subcollection_twice() {
        var collection = createCollection();
        var subcollection = createSubcollection();

        when(repository.getReferenceById(collection.getId())).thenReturn(collection);
        when(repository.getReferenceById(subcollection.getId())).thenReturn(subcollection);

        collection.getSubCollections().add(subcollection);

        var cmd = AddSubcollectionCommand.builder().subcollectionId(subcollection.getId()).build();

        assertThrows(DataConstraintException.class, () -> service.addSubcollection(collection.getId(), cmd));
    }

    @Test
    public void can_remove_subcollection() {
        var collection = createCollection();
        var subcollection = createSubcollection();

        when(repository.getReferenceById(collection.getId())).thenReturn(collection);
        when(repository.getReferenceById(subcollection.getId())).thenReturn(subcollection);

        collection.getSubCollections().add(subcollection);

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var cmd = RemoveSubcollectionCommand.builder().collectionId(subcollection.getId()).build();

        var returned = service.removeSubcollection(collection.getId(), cmd);

        assertThat(returned.getSubCollections(), hasSize(0));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_not_remove_non_existing_subcollection() {
        var collection = createCollection();
        var subcollection = createSubcollection();

        when(repository.getReferenceById(collection.getId())).thenReturn(collection);
        when(repository.getReferenceById(subcollection.getId())).thenReturn(subcollection);

        var cmd = RemoveSubcollectionCommand.builder().collectionId(subcollection.getId()).build();

        assertThrows(DataConstraintException.class, () -> service.removeSubcollection(collection.getId(), cmd));
    }

    private Collection createCollection() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Dauerausstellung");

        return Collection.builder()
                .id(new Collection.CollectionId(1L))
                .title(title)
                .build();
    }

    private Collection createSubcollection() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Italian Artists");

        return Collection.builder()
                .id(new Collection.CollectionId(2L))
                .title(title)
                .build();
    }

    private PointOfInterest createPointOfInterest() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Mona Lisa");

        return PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(2L))
                .title(title)
                .build();
    }

}
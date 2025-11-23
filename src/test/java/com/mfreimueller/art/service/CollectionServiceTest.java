package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.*;
import com.mfreimueller.art.domain.*;
import com.mfreimueller.art.foundation.DataConstraintException;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.CollectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.mfreimueller.art.service.ServiceFixtures.*;
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
    private @Mock CreatorService creatorService;
    private @Mock DateTimeFactory dateTimeFactory;

    @BeforeEach
    public void setup() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
        assumeThat(pointOfInterestService).isNotNull();
        assumeThat(creatorService).isNotNull();
        assumeThat(dateTimeFactory).isNotNull();
    }

    @Test
    public void can_create_with_valid_data() {
        var dateTime = dateTime();
        var creator = createCreator();
        var en = new Language("en", "English");

        var cmd = CreateCollectionCommand.builder()
                .title(Map.of(en, "Dauerausstellung"))
                .creatorId(creator.getId())
                .build();

        when(dateTimeFactory.now()).thenReturn(dateTime);
        when(creatorService.getByReference(any())).thenReturn(creator);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var collection = service.create(cmd);

        assertNotNull(collection);
        assertEquals(collection.getCreatedAt(), dateTime);
        assertThat(collection.getCreatedBy(), equalTo(creator));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var dateTime = dateTime();
        var creator = createCreator();
        var collection = createCollection();
        var de = new Language("de", "Deutsch");

        var cmd = UpdateCollectionCommand.builder()
                .title(Map.of(de, "Dauerausstellung"))
                .creatorId(creator.getId())
                .build();


        when(repository.getReferenceById(any())).thenReturn(collection);
        when(creatorService.getByReference(any())).thenReturn(creator);
        when(dateTimeFactory.now()).thenReturn(dateTime);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var updateCollection = service.update(collection.getId(), cmd);

        assertNotNull(updateCollection);
        assertTrue(updateCollection.getTitle().containsKey(de));
        assertEquals(updateCollection.getUpdatedAt(), dateTime);
        assertThat(updateCollection.getUpdatedBy(), equalTo(creator));

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
        var dateTime = dateTime();
        var creator = createCreator();
        var collection = createCollection();
        when(repository.getReferenceById(any())).thenReturn(collection);

        var poi = createPointOfInterest();
        when(pointOfInterestService.getByReference(any())).thenReturn(poi);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        when(creatorService.getByReference(any())).thenReturn(creator);
        when(dateTimeFactory.now()).thenReturn(dateTime);

        var cmd = AddPointOfInterestCommand.builder()
                .poiId(poi.getId())
                .creatorId(creator.getId())
                .build();

        var returned = service.addPointOfInterest(collection.getId(), cmd);

        assertThat(returned.getPointsOfInterest(), hasSize(1));
        assertThat(returned.getPointsOfInterest(), hasItem(poi));
        assertThat(returned.getUpdatedAt(), equalTo(dateTime));
        assertThat(returned.getUpdatedBy(), equalTo(creator));

        verify(repository, times(1)).getReferenceById(any());
    }

    @Test
    public void can_not_add_point_of_interest_twice() {
        var collection = createCollection();
        when(repository.getReferenceById(any())).thenReturn(collection);

        var poi = createPointOfInterest();
        collection.getPointsOfInterest().add(poi);

        when(pointOfInterestService.getByReference(any())).thenReturn(poi);

        var cmd = AddPointOfInterestCommand.builder().poiId(poi.getId()).build();

        assertThrows(DataConstraintException.class, () -> service.addPointOfInterest(collection.getId(), cmd));
    }

    @Test
    public void can_remove_point_of_interest() {
        var dateTime = dateTime();
        var creator = createCreator();
        var collection = createCollection();
        when(repository.getReferenceById(any())).thenReturn(collection);

        var poi = createPointOfInterest();
        collection.getPointsOfInterest().add(poi);

        when(pointOfInterestService.getByReference(any())).thenReturn(poi);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        when(creatorService.getByReference(any())).thenReturn(creator);
        when(dateTimeFactory.now()).thenReturn(dateTime);

        var cmd = RemovePointOfInterestCommand.builder()
                .poiId(poi.getId())
                .creatorId(creator.getId())
                .build();

        var returned = service.removePointOfInterest(collection.getId(), cmd);

        assertThat(returned.getPointsOfInterest(), hasSize(0));
        assertThat(returned.getUpdatedAt(), equalTo(dateTime));
        assertThat(returned.getUpdatedBy(), equalTo(creator));

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
        var dateTime = dateTime();
        var creator = createCreator();
        var collection = createCollection();
        var subcollection = createSubcollection();

        when(repository.getReferenceById(collection.getId())).thenReturn(collection);
        when(repository.getReferenceById(subcollection.getId())).thenReturn(subcollection);

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        when(creatorService.getByReference(any())).thenReturn(creator);
        when(dateTimeFactory.now()).thenReturn(dateTime);

        var cmd = AddSubcollectionCommand.builder()
                .subcollectionId(subcollection.getId())
                .creatorId(creator.getId())
                .build();

        var returned = service.addSubcollection(collection.getId(), cmd);

        assertThat(returned.getSubCollections(), hasSize(1));
        assertThat(returned.getSubCollections(), hasItem(subcollection));
        assertThat(returned.getUpdatedAt(), equalTo(dateTime));
        assertThat(returned.getUpdatedBy(), equalTo(creator));

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
    public void can_not_add_itself_as_subcollection() {
        var collection = createCollection();

        var cmd = AddSubcollectionCommand.builder().subcollectionId(collection.getId()).build();

        assertThrows(DataConstraintException.class, () -> service.addSubcollection(collection.getId(), cmd));
    }

    @Test
    public void detects_circular_subcollections() {
        var collection = createCollection();
        var subcollection = createSubcollection();

        when(repository.getReferenceById(collection.getId())).thenReturn(collection);
        when(repository.getReferenceById(subcollection.getId())).thenReturn(subcollection);

        subcollection.setParentCollection(collection);
        collection.getSubCollections().add(subcollection);

        var cmd = AddSubcollectionCommand.builder().subcollectionId(collection.getId()).build();

        assertThrows(DataConstraintException.class, () -> service.addSubcollection(subcollection.getId(), cmd));
    }

    @Test
    public void can_remove_subcollection() {
        var dateTime = dateTime();
        var creator = createCreator();
        var collection = createCollection();
        var subcollection = createSubcollection();

        when(repository.getReferenceById(collection.getId())).thenReturn(collection);
        when(repository.getReferenceById(subcollection.getId())).thenReturn(subcollection);

        collection.getSubCollections().add(subcollection);

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        when(creatorService.getByReference(any())).thenReturn(creator);
        when(dateTimeFactory.now()).thenReturn(dateTime);

        var cmd = RemoveSubcollectionCommand.builder()
                .collectionId(subcollection.getId())
                .creatorId(creator.getId())
                .build();

        var returned = service.removeSubcollection(collection.getId(), cmd);

        assertThat(returned.getSubCollections(), hasSize(0));
        assertThat(returned.getUpdatedAt(), equalTo(dateTime));
        assertThat(returned.getUpdatedBy(), equalTo(creator));

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
}
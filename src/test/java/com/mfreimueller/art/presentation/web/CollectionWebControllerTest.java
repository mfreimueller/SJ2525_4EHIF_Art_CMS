package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.service.CollectionService;
import com.mfreimueller.art.service.PointOfInterestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CollectionWebControllerTest extends AbstractWebControllerTest {

    @Autowired
    private CollectionWebController controller;

    @MockitoBean
    private CollectionService collectionService;

    @MockitoBean
    private PointOfInterestService poiService;

    @Test
    void list_shows_all_collections() {
        when(collectionService.getCollections(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of()));

        Model model = new BindingAwareModelMap();
        String viewName = controller.list(model);

        assertThat(viewName).isEqualTo("collection/list");
        assertThat(model.getAttribute("collections")).isNotNull();
    }

    @Test
    void detail_shows_collection() {
        var collection = Collection.builder()
                .id(new Collection.CollectionId(1L))
                .title(Map.of("en", "Test Collection"))
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .build();

        when(collectionService.getByReference(any())).thenReturn(collection);
        when(poiService.getPointsOfInterest()).thenReturn(List.of());
        when(collectionService.getCollections(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of()));

        Model model = new BindingAwareModelMap();
        String viewName = controller.detail(1L, model);

        assertThat(viewName).isEqualTo("collection/detail");
        assertThat(model.getAttribute("collection")).isNotNull();
    }

    @Test
    void create_form_returns_create_view() {
        String viewName = controller.createForm();
        assertThat(viewName).isEqualTo("collection/create");
    }

    @Test
    void create_creates_collection_and_redirects() {
        var collection = Collection.builder()
                .id(new Collection.CollectionId(1L))
                .title(Map.of("en", "New Collection"))
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .build();

        when(collectionService.create(any())).thenReturn(collection);

        String viewName = controller.create("New Collection", null, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/collections/1");
        verify(collectionService).create(any());
    }

    @Test
    void edit_form_shows_collection() {
        var collection = Collection.builder()
                .id(new Collection.CollectionId(1L))
                .title(Map.of("en", "Edit Collection"))
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .build();

        when(collectionService.getByReference(any())).thenReturn(collection);

        Model model = new BindingAwareModelMap();
        String viewName = controller.editForm(1L, model);

        assertThat(viewName).isEqualTo("collection/edit");
        assertThat(model.getAttribute("collection")).isNotNull();
    }

    @Test
    void edit_updates_collection_and_redirects() {
        String viewName = controller.edit(1L, "Updated", null, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/collections/1");
        verify(collectionService).update(any(), any());
    }

    @Test
    void delete_confirm_shows_collection() {
        var collection = Collection.builder()
                .id(new Collection.CollectionId(1L))
                .title(Map.of("en", "Delete Collection"))
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .build();

        when(collectionService.getByReference(any())).thenReturn(collection);

        Model model = new BindingAwareModelMap();
        String viewName = controller.deleteConfirm(1L, model);

        assertThat(viewName).isEqualTo("collection/confirm-delete");
        assertThat(model.getAttribute("collection")).isNotNull();
    }

    @Test
    void delete_removes_collection_and_redirects() {
        String viewName = controller.delete(1L);

        assertThat(viewName).isEqualTo("redirect:/web/collections");
        verify(collectionService).delete(any());
    }

    @Test
    void add_poi_redirects_to_detail() {
        String viewName = controller.addPoi(1L, 2L, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/collections/1");
        verify(collectionService).addPointOfInterest(any(), any());
    }

    @Test
    void remove_poi_redirects_to_detail() {
        String viewName = controller.removePoi(1L, 2L, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/collections/1");
        verify(collectionService).removePointOfInterest(any(), any());
    }

    @Test
    void add_subcollection_redirects_to_detail() {
        String viewName = controller.addSubcollection(1L, 3L, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/collections/1");
        verify(collectionService).addSubcollection(any(), any());
    }

    @Test
    void remove_subcollection_redirects_to_detail() {
        String viewName = controller.removeSubcollection(1L, 4L, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/collections/1");
        verify(collectionService).removeSubcollection(any(), any());
    }
}

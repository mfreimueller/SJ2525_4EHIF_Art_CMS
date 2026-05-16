package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.service.ExhibitionService;
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

class ExhibitionWebControllerTest extends AbstractWebControllerTest {

    @Autowired
    private ExhibitionWebController controller;

    @MockitoBean
    private ExhibitionService exhibitionService;

    @MockitoBean
    private PointOfInterestService poiService;

    @Test
    void list_shows_all_exhibitions() {
        when(exhibitionService.getExhibitions(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of()));

        Model model = new BindingAwareModelMap();
        String viewName = controller.list(model);

        assertThat(viewName).isEqualTo("exhibition/list");
        assertThat(model.getAttribute("exhibitions")).isNotNull();
    }

    @Test
    void detail_shows_exhibition() {
        var exhibition = Exhibition.builder()
                .id(new Collection.CollectionId(1L))
                .title(Map.of("en", "Test Exhibition"))
                .languages(Set.of("en"))
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .build();

        when(exhibitionService.getByReference(any())).thenReturn(exhibition);
        when(poiService.getPointsOfInterest()).thenReturn(List.of());
        when(exhibitionService.getExhibitions(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of()));

        Model model = new BindingAwareModelMap();
        String viewName = controller.detail(1L, model);

        assertThat(viewName).isEqualTo("exhibition/detail");
        assertThat(model.getAttribute("exhibition")).isNotNull();
    }

    @Test
    void create_form_returns_create_view() {
        String viewName = controller.createForm();
        assertThat(viewName).isEqualTo("exhibition/create");
    }

    @Test
    void create_creates_exhibition_and_redirects() {
        var exhibition = Exhibition.builder()
                .id(new Collection.CollectionId(1L))
                .title(Map.of("en", "New Exhibition"))
                .languages(Set.of("en"))
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .build();

        when(exhibitionService.create(any())).thenReturn(exhibition);

        String viewName = controller.create("New Exhibition", null, Set.of("en"), adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/exhibitions/1");
        verify(exhibitionService).create(any());
    }

    @Test
    void edit_form_shows_exhibition() {
        var exhibition = Exhibition.builder()
                .id(new Collection.CollectionId(1L))
                .title(Map.of("en", "Edit Exhibition"))
                .languages(Set.of("en"))
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .build();

        when(exhibitionService.getByReference(any())).thenReturn(exhibition);

        Model model = new BindingAwareModelMap();
        String viewName = controller.editForm(1L, model);

        assertThat(viewName).isEqualTo("exhibition/edit");
        assertThat(model.getAttribute("exhibition")).isNotNull();
    }

    @Test
    void edit_updates_exhibition_and_redirects() {
        String viewName = controller.edit(1L, "Updated", null, Set.of("en", "de"), adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/exhibitions/1");
        verify(exhibitionService).update(any(), any());
    }

    @Test
    void delete_confirm_shows_exhibition() {
        var exhibition = Exhibition.builder()
                .id(new Collection.CollectionId(1L))
                .title(Map.of("en", "Delete Exhibition"))
                .languages(Set.of("en"))
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .build();

        when(exhibitionService.getByReference(any())).thenReturn(exhibition);

        Model model = new BindingAwareModelMap();
        String viewName = controller.deleteConfirm(1L, model);

        assertThat(viewName).isEqualTo("exhibition/confirm-delete");
        assertThat(model.getAttribute("exhibition")).isNotNull();
    }

    @Test
    void delete_removes_exhibition_and_redirects() {
        String viewName = controller.delete(1L);

        assertThat(viewName).isEqualTo("redirect:/web/exhibitions");
        verify(exhibitionService).delete(any());
    }

    @Test
    void add_poi_redirects_to_detail() {
        String viewName = controller.addPoi(1L, 2L, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/exhibitions/1");
        verify(exhibitionService).addPointOfInterest(any(), any());
    }

    @Test
    void remove_poi_redirects_to_detail() {
        String viewName = controller.removePoi(1L, 2L, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/exhibitions/1");
        verify(exhibitionService).removePointOfInterest(any(), any());
    }

    @Test
    void add_subcollection_redirects_to_detail() {
        String viewName = controller.addSubcollection(1L, 3L, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/exhibitions/1");
        verify(exhibitionService).addSubcollection(any(), any());
    }

    @Test
    void remove_subcollection_redirects_to_detail() {
        String viewName = controller.removeSubcollection(1L, 4L, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/exhibitions/1");
        verify(exhibitionService).removeSubcollection(any(), any());
    }
}

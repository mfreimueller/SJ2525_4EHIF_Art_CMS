package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.service.PointOfInterestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PoiWebControllerTest extends AbstractWebControllerTest {

    @Autowired
    private PointOfInterestWebController controller;

    @MockitoBean
    private PointOfInterestService poiService;

    @Test
    void list_shows_all_pois() {
        when(poiService.getPointsOfInterest()).thenReturn(
                List.of(PointOfInterest.builder()
                        .id(new PointOfInterest.PointOfInterestId(1L))
                        .title(Map.of("en", "Test POI"))
                        .build()));

        Model model = new BindingAwareModelMap();
        String viewName = controller.list(model);

        assertThat(viewName).isEqualTo("poi/list");
        assertThat(model.getAttribute("pois")).asList().hasSize(1);
    }

    @Test
    void detail_shows_poi() {
        var poi = PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(1L))
                .title(Map.of("en", "Test POI"))
                .build();

        when(poiService.getPointOfInterest(any())).thenReturn(Optional.of(poi));

        Model model = new BindingAwareModelMap();
        String viewName = controller.detail(1L, model);

        assertThat(viewName).isEqualTo("poi/detail");
        assertThat(model.getAttribute("poi")).isNotNull();
    }

    @Test
    void create_form_returns_create_view() {
        String viewName = controller.createForm();
        assertThat(viewName).isEqualTo("poi/create");
    }

    @Test
    void create_creates_poi_and_redirects() {
        var poi = PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(1L))
                .title(Map.of("en", "New POI"))
                .build();

        when(poiService.create(any())).thenReturn(poi);

        String viewName = controller.create("New POI", null, null, null, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/pois/1");
        verify(poiService).create(any());
    }

    @Test
    void edit_form_shows_poi() {
        var poi = PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(1L))
                .title(Map.of("en", "Edit POI"))
                .build();

        when(poiService.getPointOfInterest(any())).thenReturn(Optional.of(poi));

        Model model = new BindingAwareModelMap();
        String viewName = controller.editForm(1L, model);

        assertThat(viewName).isEqualTo("poi/edit");
        assertThat(model.getAttribute("poi")).isNotNull();
    }

    @Test
    void edit_updates_poi_and_redirects() {
        String viewName = controller.edit(1L, "Updated", null, null, null, adminAuth());

        assertThat(viewName).isEqualTo("redirect:/web/pois/1");
        verify(poiService).replace(any(), any());
    }

    @Test
    void delete_confirm_shows_poi() {
        var poi = PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(1L))
                .title(Map.of("en", "Delete POI"))
                .build();

        when(poiService.getPointOfInterest(any())).thenReturn(Optional.of(poi));

        Model model = new BindingAwareModelMap();
        String viewName = controller.deleteConfirm(1L, model);

        assertThat(viewName).isEqualTo("poi/confirm-delete");
        assertThat(model.getAttribute("poi")).isNotNull();
    }

    @Test
    void delete_removes_poi_and_redirects() {
        String viewName = controller.delete(1L);

        assertThat(viewName).isEqualTo("redirect:/web/pois");
        verify(poiService).delete(any());
    }

    @Test
    void pool_shows_unassigned_pois() {
        when(poiService.getUnassignedPointsOfInterest()).thenReturn(List.of());

        Model model = new BindingAwareModelMap();
        String viewName = controller.pool(model);

        assertThat(viewName).isEqualTo("poi/pool");
        assertThat(model.getAttribute("pois")).isNotNull();
    }
}

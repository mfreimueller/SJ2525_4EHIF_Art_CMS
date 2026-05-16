package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.service.VisitHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class VisitHistoryWebControllerTest extends AbstractWebControllerTest {

    @Autowired
    private VisitHistoryWebController controller;

    @MockitoBean
    private VisitHistoryService visitHistoryService;

    @Test
    void list_shows_all_visit_histories() {
        when(visitHistoryService.getVisitHistories(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of()));

        Model model = new BindingAwareModelMap();
        String viewName = controller.list(model);

        assertThat(viewName).isEqualTo("visit-history/list");
        assertThat(model.getAttribute("histories")).isNotNull();
    }

    @Test
    void detail_shows_visit_history() {
        var history = VisitHistory.builder()
                .id(new VisitHistory.VisitHistoryId(1L))
                .build();

        when(visitHistoryService.getByReference(any())).thenReturn(history);

        Model model = new BindingAwareModelMap();
        String viewName = controller.detail(1L, model);

        assertThat(viewName).isEqualTo("visit-history/detail");
        assertThat(model.getAttribute("history")).isNotNull();
    }
}

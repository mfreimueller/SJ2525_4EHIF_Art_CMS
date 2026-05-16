package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.service.VisitorService;
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

class VisitorWebControllerTest extends AbstractWebControllerTest {

    @Autowired
    private VisitorWebController controller;

    @MockitoBean
    private VisitorService visitorService;

    @Test
    void list_shows_all_visitors() {
        when(visitorService.getVisitors(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of()));

        Model model = new BindingAwareModelMap();
        String viewName = controller.list(model);

        assertThat(viewName).isEqualTo("visitor/list");
        assertThat(model.getAttribute("visitors")).isNotNull();
    }

    @Test
    void detail_shows_visitor() {
        var visitor = Visitor.builder()
                .id(new Visitor.VisitorId(1L))
                .username("testvisitor")
                .emailAddress("test@example.com")
                .build();

        when(visitorService.getByReference(any())).thenReturn(visitor);

        Model model = new BindingAwareModelMap();
        String viewName = controller.detail(1L, model);

        assertThat(viewName).isEqualTo("visitor/detail");
        assertThat(model.getAttribute("visitor")).isNotNull();
    }
}

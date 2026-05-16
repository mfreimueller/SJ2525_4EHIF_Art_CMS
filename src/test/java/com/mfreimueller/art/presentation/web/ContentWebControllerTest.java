package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.domain.TextContent;
import com.mfreimueller.art.service.ContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ContentWebControllerTest extends AbstractWebControllerTest {

    @Autowired
    private ContentWebController controller;

    @MockitoBean
    private ContentService contentService;

    @Test
    void list_shows_all_content() {
        when(contentService.getPaged(null, 1000))
                .thenReturn(new SliceImpl<>(
                        List.of(TextContent.builder().id(1L).description(Map.of("de", "Test")).build()),
                        PageRequest.of(0, 1000),
                        false));

        Model model = new BindingAwareModelMap();
        String viewName = controller.list(model);

        assertThat(viewName).isEqualTo("content/list");
        assertThat(model.getAttribute("contents")).asList().hasSize(1);
    }

    @Test
    void detail_shows_content() {
        var content = TextContent.builder()
                .id(1L)
                .description(Map.of("de", "Eine Beschreibung"))
                .shortText(Map.of("de", "Kurz"))
                .longText(Map.of("de", "Lang"))
                .build();

        when(contentService.findById(anyLong())).thenReturn(Optional.of(content));

        Model model = new BindingAwareModelMap();
        String viewName = controller.detail(1L, model, adminAuth());

        assertThat(viewName).isEqualTo("content/detail");
        assertThat(model.getAttribute("content")).isNotNull();
    }
}

package com.mfreimueller.art.presentation;

import com.mfreimueller.art.MapperTestConfig;
import com.mfreimueller.art.domain.TextContent;
import com.mfreimueller.art.mappers.ContentMapper;
import com.mfreimueller.art.mappers.CreatorMapper;
import com.mfreimueller.art.presentation.assembler.ContentModelAssembler;
import com.mfreimueller.art.service.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import({MapperTestConfig.class, ContentModelAssembler.class})
class ContentControllerTest extends AbstractDocumentationControllerTest {
    private @MockitoBean ContentService service;

    private @MockitoSpyBean ContentMapper mapper;
    private @MockitoSpyBean CreatorMapper creatorMapper;
    private @MockitoSpyBean ContentModelAssembler assembler;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        super.setUp(webApplicationContext, restDocumentation);
    }

    @Test
    void can_fetch_existing_content() throws Exception {
        var content = TextContent.builder()
                .id(1L)
                .description(Map.of("de", "Eine Beschreibung"))
                .shortText(Map.of("de", "Kurz"))
                .longText(Map.of("de", "Langtext"))
                .build();

        when(service.getByReference(anyLong())).thenReturn(content);

        mockMvc
                .perform(get("/api/content/1"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$.description.de").value("Eine Beschreibung"))
                .andExpect(jsonPath("$.id").value(1))
                .andDo(print())
                .andDo(document("fetch-content-1"));

        verify(service).getByReference(anyLong());
    }

    @Test
    void returns_proper_status_code_when_fetching_non_existing_content() throws Exception {
        when(service.getByReference(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/content/999"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("fetch-content-1-not-found"));
    }

    @Test
    void can_fetch_content_slice() throws Exception {
        var content = TextContent.builder()
                .id(1L)
                .description(Map.of("de", "Eine Beschreibung"))
                .build();

        when(service.getPaged(any(), anyInt()))
                .thenReturn(new SliceImpl<>(List.of(content), PageRequest.of(0, 20), false));

        mockMvc
                .perform(get("/api/content"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.textContentDtoList[0].description.de").value("Eine Beschreibung"))
                .andExpect(jsonPath("$._embedded.textContentDtoList[0].id").value(1))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andDo(print())
                .andDo(document("fetch-content-slice"));

        verify(service).getPaged(any(), anyInt());
    }

    @Test
    void can_fetch_content_with_keyset_pagination_params() throws Exception {
        var content = TextContent.builder()
                .id(2L)
                .description(Map.of("de", "Nächste Seite"))
                .build();

        when(service.getPaged(eq(1L), eq(5)))
                .thenReturn(new SliceImpl<>(List.of(content), PageRequest.of(0, 5), false));

        mockMvc
                .perform(get("/api/content").param("lastId", "1").param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.textContentDtoList[0].id").value(2))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(0))
                .andDo(print())
                .andDo(document("fetch-content-keyset"));

        verify(service).getPaged(eq(1L), eq(5));
    }

    @Test
    void content_slice_includes_next_link_when_available() throws Exception {
        var content = TextContent.builder()
                .id(1L)
                .description(Map.of("de", "Erste Seite"))
                .build();

        when(service.getPaged(any(), anyInt()))
                .thenReturn(new SliceImpl<>(List.of(content), PageRequest.of(0, 20), true));

        mockMvc
                .perform(get("/api/content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.next").exists())
                .andDo(print())
                .andDo(document("fetch-content-next-link"));

        verify(service).getPaged(any(), anyInt());
    }

    @Test
    void content_slice_includes_prev_link_when_available() throws Exception {
        var content = TextContent.builder()
                .id(5L)
                .description(Map.of("de", "Zweite Seite"))
                .build();

        when(service.getPaged(any(), anyInt()))
                .thenReturn(new SliceImpl<>(List.of(content), PageRequest.of(1, 20), false));

        mockMvc
                .perform(get("/api/content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.prev").exists())
                .andDo(print())
                .andDo(document("fetch-content-prev-link"));

        verify(service).getPaged(any(), anyInt());
    }

    @Test
    void returns_proper_status_code_on_empty_content_database() throws Exception {
        when(service.getPaged(any(), anyInt()))
                .thenReturn(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        mockMvc.perform(get("/api/content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andDo(print())
                .andDo(document("fetch-content-empty"));

        verify(service).getPaged(any(), anyInt());
    }
}

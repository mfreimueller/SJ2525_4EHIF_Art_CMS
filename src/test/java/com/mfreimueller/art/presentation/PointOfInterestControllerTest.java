package com.mfreimueller.art.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mfreimueller.art.RawResponseBodySnippet;
import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.commands.UpdatePointOfInterestCommand;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.mappers.*;
import com.mfreimueller.art.presentation.assembler.PointOfInterestModelAssembler;
import com.mfreimueller.art.service.PointOfInterestService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointOfInterestController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import({ PointOfInterestMapperImpl.class, ContentMapperImpl.class, CreatorMapperImpl.class, PointOfInterestModelAssembler.class })
class PointOfInterestControllerTest extends AbstractDocumentationControllerTest {
    private @MockitoBean PointOfInterestService service;

    private @MockitoSpyBean PointOfInterestMapper mapper;
    private @MockitoSpyBean CreatorMapper creatorMapper;
    private @MockitoSpyBean PointOfInterestModelAssembler assembler;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        super.setUp(webApplicationContext, restDocumentation);
    }

    @Test
    void can_create_point_of_interest() throws Exception {
        var title = "Das Bildnis des Dorian Gray";
        var titleMap = Map.of("de", title);
        var description = "Nicht ganz ein Kunstwerk, aber immerhin klassische Literatur.";
        var descriptionMap = Map.of("de", description);
        var creatorId = new Creator.CreatorId(1L);
        var creator = Creator.builder().username("idefix").id(creatorId).build();

        var id = new PointOfInterest.PointOfInterestId(1L);
        var poi = PointOfInterest
                .builder()
                .title(titleMap)
                .description(descriptionMap)
                .id(id)
                .createdBy(creator)
                .build();

        when(service.create(any())).thenReturn(poi);

        var cmd = CreatePointOfInterestCommand.builder().title(titleMap).creatorId(creatorId).build();

        mockMvc
                .perform(
                        post("/api/pois")
                                .header("Accept", "application/json")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsBytes(cmd))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/pois/1")
                ))
                .andExpect(jsonPath("$.title.de").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print())
                .andDo(document("create-poi-1"));

        verify(service).create(any());
    }

    @Test
    void can_search_slice() throws Exception {
        var title = "Das Bildnis des Dorian Gray";
        var titleMap = Map.of("de", title);
        var id = new PointOfInterest.PointOfInterestId(1L);

        var poi = PointOfInterest.builder()
                .id(id)
                .title(titleMap)
                .build();

        when(service.search(any(), any(), any(Pageable.class))).thenReturn(new SliceImpl<>(List.of(poi), PageRequest.of(1, 5), true));

        mockMvc
                .perform(get("/api/pois/search/de/Bildnis"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.pointOfInterestDtoList[0].title.de").value(title))
                .andExpect(jsonPath("$._embedded.pointOfInterestDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(1))
                .andDo(print())
                .andDo(document("search-poi-slice-1")) ;

        verify(service).search(any(), any(), any(Pageable.class));
    }

    @Test
    void can_fetch_existing_pois() throws Exception {
        var title = "Das Bildnis des Dorian Gray";
        var titleMap = Map.of("de", title);
        var id = new PointOfInterest.PointOfInterestId(1L);

        var poi = PointOfInterest.builder()
                .id(id)
                .title(titleMap)
                .build();

        when(service.getPointsOfInterest(any(Pageable.class))).thenReturn(new SliceImpl<PointOfInterest>(List.of(poi), PageRequest.of(0, 20), false));

        mockMvc
                .perform(get("/api/pois"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.pointOfInterestDtoList[0].title.de").value(title))
                .andExpect(jsonPath("$._embedded.pointOfInterestDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andDo(print())
                .andDo(document("fetch-pois"));

        verify(service).getPointsOfInterest(any(Pageable.class));
    }

    @Test
    void can_fetch_existing_pois_slice() throws Exception {
        var title = "Das Bildnis des Dorian Gray";
        var titleMap = Map.of("de", title);
        var id = new PointOfInterest.PointOfInterestId(1L);

        var poi = PointOfInterest.builder()
                .id(id)
                .title(titleMap)
                .build();

        when(service.getPointsOfInterest(any(Pageable.class))).thenReturn(new SliceImpl<PointOfInterest>(List.of(poi), PageRequest.of(1, 5), true));

        mockMvc
                .perform(get("/api/pois").param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.pointOfInterestDtoList[0].title.de").value(title))
                .andExpect(jsonPath("$._embedded.pointOfInterestDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(1))
                .andDo(print())
                .andDo(document("fetch-pois-slice"));

        verify(service).getPointsOfInterest(any(Pageable.class));
    }

    @Test
    void returns_proper_status_code_on_empty_database() throws Exception {
        when(service.getPointsOfInterest(any(Pageable.class))).thenReturn(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        mockMvc.perform(get("/api/pois"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("fetch-pois-empty"));

        verify(service).getPointsOfInterest(any(Pageable.class));
    }

    @Test
    void can_fetch_one_existing_point_of_interest() throws Exception {
        var title = "Das Bildnis des Dorian Gray";
        var titleMap = Map.of("de", title);
        var id = new PointOfInterest.PointOfInterestId(1L);

        var dto = PointOfInterest.builder().id(id).title(titleMap).build();

        when(service.getPointOfInterest(any())).thenReturn(Optional.of(dto));

        mockMvc
                .perform(get("/api/pois/1"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$.title.de").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print())
                .andDo(document("fetch-poi-1"));

        verify(service).getPointOfInterest(any());
    }

    @Test
    void returns_proper_status_code_when_fetching_non_existing_point_of_interest() throws Exception {
        when(service.getPointOfInterest(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pois/1"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("fetch-poi-1-not-found"));
    }

    @Test
    void can_delete_existing_point_of_interest() throws Exception {
        when(service.delete(any())).thenReturn(true);

        mockMvc.perform(delete("/api/pois/1"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delete-poi-1"));

        verify(service).delete(any());
    }

    @Test
    void cant_delete_missing_data() throws Exception {
        when(service.delete(any())).thenReturn(false);

        mockMvc.perform(delete("/api/pois/1"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("delete-poi-1-not-found"));

        verify(service).delete(any());
    }

    @Test
    void can_replace_existing_point_of_interest() throws Exception {
        var title = "Frank Herbert's Dune";
        var titleMap = Map.of("en", title);

        var key = 1L;

        var cmd = UpdatePointOfInterestCommand.builder().title(titleMap).creatorId(new Creator.CreatorId(1L)).build();

        var id = new PointOfInterest.PointOfInterestId(key);
        var poi = PointOfInterest.builder().id(new PointOfInterest.PointOfInterestId(key)).title(titleMap).build();

        when(service.replace(any(), any())).thenReturn(Optional.of(poi));

        mockMvc
                .perform(
                        put("/api/pois/{key}", key)
                                .contentType("application/json")
                                .accept("application/json")
                                .content(objectMapper.writeValueAsBytes(cmd))
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/pois/1")))
                .andExpect(jsonPath("$.title.en").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print())
                .andDo(document("replace-poi-1"));

        verify(service).replace(any(), any());
    }

    @Test
    void returns_proper_status_code_when_replacing_non_exiting_point_of_interest() throws Exception {
        var key = 1L;

        var cmd = UpdatePointOfInterestCommand.builder().title(Map.of("en", "Some title")).creatorId(new Creator.CreatorId(1L)).build();

        when(service.replace(any(), any())).thenReturn(Optional.empty());

        mockMvc
                .perform(
                        put("/api/pois/{key}", key)
                                .contentType("application/json")
                                .accept("application/json")
                                .content(objectMapper.writeValueAsBytes(cmd))
                )
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("replace-poi-1-not-found"));

        verify(service).replace(any(), any());
    }

    @Test
    void can_update_existing_point_of_interest() throws Exception {
        var title = "Frank Herbert's Dune";
        var titleMap = Map.of("en", title);

        var key = 1L;

        var cmd = UpdatePointOfInterestCommand.builder().title(titleMap).creatorId(new Creator.CreatorId(1L)).build();

        var id = new PointOfInterest.PointOfInterestId(key);
        var poi = PointOfInterest.builder().id(new PointOfInterest.PointOfInterestId(key)).title(titleMap).build();

        when(service.update(any(), any())).thenReturn(Optional.of(poi));

        mockMvc
                .perform(
                        patch("/api/pois/{key}", key)
                                .contentType("application/json")
                                .accept("application/json")
                                .content(objectMapper.writeValueAsBytes(cmd))
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/pois/1")
                ))
                .andExpect(jsonPath("$.title.en").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print())
                .andDo(document("update-poi-1"));

        verify(service).update(any(), any());
    }

    @Test
    void returns_proper_status_code_when_updating_non_exiting_point_of_interest() throws Exception {
        var key = 1L;

        var cmd = UpdatePointOfInterestCommand.builder().title(Map.of("en", "Some title")).creatorId(new Creator.CreatorId(1L)).build();

        when(service.update(any(), any())).thenReturn(Optional.empty());

        mockMvc
                .perform(
                        patch("/api/pois/{key}", key)
                                .contentType("application/json")
                                .accept("application/json")
                                .content(objectMapper.writeValueAsBytes(cmd))
                )
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("update-poi-1-not-found"));

        verify(service).update(any(), any());
    }
}
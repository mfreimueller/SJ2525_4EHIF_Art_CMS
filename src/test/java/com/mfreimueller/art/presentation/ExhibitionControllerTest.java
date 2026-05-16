package com.mfreimueller.art.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mfreimueller.art.MapperTestConfig;
import com.mfreimueller.art.commands.AddPointOfInterestCommand;
import com.mfreimueller.art.commands.AddSubcollectionCommand;
import com.mfreimueller.art.commands.CreateExhibitionCommand;
import com.mfreimueller.art.commands.UpdateExhibitionCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.mappers.CollectionMapper;
import com.mfreimueller.art.mappers.CreatorMapper;
import com.mfreimueller.art.presentation.assembler.ExhibitionModelAssembler;
import com.mfreimueller.art.service.ExhibitionService;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExhibitionController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import({MapperTestConfig.class, ExhibitionModelAssembler.class})
class ExhibitionControllerTest extends AbstractDocumentationControllerTest {
    private @MockitoBean ExhibitionService service;

    private @MockitoSpyBean CollectionMapper mapper;
    private @MockitoSpyBean CreatorMapper creatorMapper;
    private @MockitoSpyBean ExhibitionModelAssembler assembler;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        super.setUp(webApplicationContext, restDocumentation);
    }

    @Test
    void can_create_exhibition() throws Exception {
        var title = "Weltausstellung";
        var titleMap = Map.of("de", title);
        var languages = Set.of("de", "en", "fr");
        var creatorId = new Creator.CreatorId(1L);
        var creator = Creator.builder().username("admin").id(creatorId).build();
        var id = new Collection.CollectionId(1L);
        var exhibition = Exhibition.builder()
                .id(id)
                .title(titleMap)
                .languages(languages)
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .createdBy(creator)
                .updatedBy(creator)
                .build();

        when(service.create(any())).thenReturn(exhibition);

        var cmd = CreateExhibitionCommand.builder()
                .title(titleMap).languages(languages).creatorId(creatorId).build();

        mockMvc
                .perform(post("/api/exhibitions")
                        .header("Accept", "application/json")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/exhibitions/1")))
                .andExpect(jsonPath("$.title.de").value(title))
                .andExpect(jsonPath("$.languages[0]").isString())
                .andDo(print())
                .andDo(document("create-exhibition-1"));

        verify(service).create(any());
    }

    @Test
    void can_fetch_existing_exhibitions() throws Exception {
        var title = "Weltausstellung";
        var titleMap = Map.of("de", title);
        var id = new Collection.CollectionId(1L);
        var exhibition = Exhibition.builder().id(id).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.getExhibitions(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(exhibition), PageRequest.of(0, 20), false));

        mockMvc
                .perform(get("/api/exhibitions"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.exhibitionDtoList[0].title.de").value(title))
                .andExpect(jsonPath("$._embedded.exhibitionDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andDo(print())
                .andDo(document("fetch-exhibitions"));

        verify(service).getExhibitions(any(Pageable.class));
    }

    @Test
    void can_fetch_existing_exhibitions_slice() throws Exception {
        var title = "Weltausstellung";
        var titleMap = Map.of("de", title);
        var id = new Collection.CollectionId(1L);
        var exhibition = Exhibition.builder().id(id).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.getExhibitions(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(exhibition), PageRequest.of(1, 5), true));

        mockMvc
                .perform(get("/api/exhibitions").param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.exhibitionDtoList[0].title.de").value(title))
                .andExpect(jsonPath("$._embedded.exhibitionDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(1))
                .andDo(print())
                .andDo(document("fetch-exhibitions-slice"));

        verify(service).getExhibitions(any(Pageable.class));
    }

    @Test
    void returns_proper_status_code_on_empty_database() throws Exception {
        when(service.getExhibitions(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        mockMvc.perform(get("/api/exhibitions"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("fetch-exhibitions-empty"));

        verify(service).getExhibitions(any(Pageable.class));
    }

    @Test
    void can_fetch_one_existing_exhibition() throws Exception {
        var title = "Weltausstellung";
        var titleMap = Map.of("de", title);
        var id = new Collection.CollectionId(1L);
        var exhibition = Exhibition.builder().id(id).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.getByReference(any())).thenReturn(exhibition);

        mockMvc
                .perform(get("/api/exhibitions/1"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$.title.de").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print())
                .andDo(document("fetch-exhibition-1"));

        verify(service).getByReference(any());
    }

    @Test
    void can_delete_existing_exhibition() throws Exception {
        mockMvc.perform(delete("/api/exhibitions/1"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delete-exhibition-1"));

        verify(service).delete(any());
    }

    @Test
    void can_replace_existing_exhibition() throws Exception {
        var title = "Neue Weltausstellung";
        var titleMap = Map.of("de", title);
        var key = 1L;
        var cmd = UpdateExhibitionCommand.builder().title(titleMap)
                .languages(Set.of("de", "en")).creatorId(new Creator.CreatorId(1L)).build();
        var exhibition = Exhibition.builder()
                .id(new Collection.CollectionId(key)).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.update(any(), any())).thenReturn(exhibition);

        mockMvc
                .perform(put("/api/exhibitions/{key}", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/exhibitions/1")))
                .andExpect(jsonPath("$.title.de").value(title))
                .andDo(print())
                .andDo(document("replace-exhibition-1"));

        verify(service).update(any(), any());
    }

    @Test
    void can_update_existing_exhibition() throws Exception {
        var title = "Neue Weltausstellung";
        var titleMap = Map.of("de", title);
        var key = 1L;
        var cmd = UpdateExhibitionCommand.builder().title(titleMap)
                .languages(Set.of("de", "en")).creatorId(new Creator.CreatorId(1L)).build();
        var exhibition = Exhibition.builder()
                .id(new Collection.CollectionId(key)).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.update(any(), any())).thenReturn(exhibition);

        mockMvc
                .perform(patch("/api/exhibitions/{key}", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/exhibitions/1")))
                .andExpect(jsonPath("$.title.de").value(title))
                .andDo(print())
                .andDo(document("patch-exhibition-1"));

        verify(service).update(any(), any());
    }

    @Test
    void can_add_point_of_interest() throws Exception {
        var title = "Weltausstellung";
        var titleMap = Map.of("de", title);
        var key = 1L;
        var poiId = new PointOfInterest.PointOfInterestId(1L);
        var creatorId = new Creator.CreatorId(1L);
        var cmd = AddPointOfInterestCommand.builder().poiId(poiId).creatorId(creatorId).build();
        var exhibition = Exhibition.builder()
                .id(new Collection.CollectionId(key)).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.addPointOfInterest(any(), any())).thenReturn(exhibition);

        mockMvc
                .perform(post("/api/exhibitions/{key}/pois", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title.de").value(title))
                .andDo(print())
                .andDo(document("add-poi-to-exhibition"));

        verify(service).addPointOfInterest(any(), any());
    }

    @Test
    void can_remove_point_of_interest() throws Exception {
        var key = 1L;
        var poiKey = 1L;
        var creatorId = 1L;

        mockMvc
                .perform(delete("/api/exhibitions/{key}/pois/{poiKey}", key, poiKey)
                        .param("creatorId", String.valueOf(creatorId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("remove-poi-from-exhibition"));

        verify(service).removePointOfInterest(any(), any());
    }

    @Test
    void can_add_subcollection() throws Exception {
        var title = "Weltausstellung";
        var titleMap = Map.of("de", title);
        var key = 1L;
        var subId = new Collection.CollectionId(2L);
        var creatorId = new Creator.CreatorId(1L);
        var cmd = AddSubcollectionCommand.builder().subcollectionId(subId).creatorId(creatorId).build();
        var exhibition = Exhibition.builder()
                .id(new Collection.CollectionId(key)).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.addSubcollection(any(), any())).thenReturn(exhibition);

        mockMvc
                .perform(post("/api/exhibitions/{key}/subcollections", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title.de").value(title))
                .andDo(print())
                .andDo(document("add-subcollection-to-exhibition"));

        verify(service).addSubcollection(any(), any());
    }

    @Test
    void can_remove_subcollection() throws Exception {
        var key = 1L;
        var subKey = 2L;
        var creatorId = 1L;

        mockMvc
                .perform(delete("/api/exhibitions/{key}/subcollections/{subKey}", key, subKey)
                        .param("creatorId", String.valueOf(creatorId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("remove-subcollection-from-exhibition"));

        verify(service).removeSubcollection(any(), any());
    }
}

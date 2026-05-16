package com.mfreimueller.art.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mfreimueller.art.MapperTestConfig;
import com.mfreimueller.art.commands.AddPointOfInterestCommand;
import com.mfreimueller.art.commands.AddSubcollectionCommand;
import com.mfreimueller.art.commands.CreateCollectionCommand;
import com.mfreimueller.art.commands.UpdateCollectionCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.mappers.CollectionMapper;
import com.mfreimueller.art.mappers.CreatorMapper;
import com.mfreimueller.art.presentation.assembler.CollectionModelAssembler;
import com.mfreimueller.art.service.CollectionService;
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

@WebMvcTest(CollectionController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import({MapperTestConfig.class, CollectionModelAssembler.class})
class CollectionControllerTest extends AbstractDocumentationControllerTest {
    private @MockitoBean CollectionService service;

    private @MockitoSpyBean CollectionMapper mapper;
    private @MockitoSpyBean CreatorMapper creatorMapper;
    private @MockitoSpyBean CollectionModelAssembler assembler;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        super.setUp(webApplicationContext, restDocumentation);
    }

    @Test
    void can_create_collection() throws Exception {
        var title = "Moderne Kunst";
        var titleMap = Map.of("de", title);
        var creatorId = new Creator.CreatorId(1L);
        var creator = Creator.builder().username("admin").id(creatorId).build();
        var id = new Collection.CollectionId(1L);
        var collection = Collection.builder()
                .id(id)
                .title(titleMap)
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .createdBy(creator)
                .updatedBy(creator)
                .build();

        when(service.create(any())).thenReturn(collection);

        var cmd = CreateCollectionCommand.builder().title(titleMap).creatorId(creatorId).build();

        mockMvc
                .perform(post("/api/collections")
                        .header("Accept", "application/json")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/collections/1")))
                .andExpect(jsonPath("$.title.de").value(title))
                .andDo(print())
                .andDo(document("create-collection-1"));

        verify(service).create(any());
    }

    @Test
    void can_fetch_existing_collections() throws Exception {
        var title = "Moderne Kunst";
        var titleMap = Map.of("de", title);
        var id = new Collection.CollectionId(1L);
        var collection = Collection.builder().id(id).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.getCollections(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(collection), PageRequest.of(0, 20), false));

        mockMvc
                .perform(get("/api/collections"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.collectionDtoList[0].title.de").value(title))
                .andExpect(jsonPath("$._embedded.collectionDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andDo(print())
                .andDo(document("fetch-collections"));

        verify(service).getCollections(any(Pageable.class));
    }

    @Test
    void can_fetch_existing_collections_slice() throws Exception {
        var title = "Moderne Kunst";
        var titleMap = Map.of("de", title);
        var id = new Collection.CollectionId(1L);
        var collection = Collection.builder().id(id).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.getCollections(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(collection), PageRequest.of(1, 5), true));

        mockMvc
                .perform(get("/api/collections").param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.collectionDtoList[0].title.de").value(title))
                .andExpect(jsonPath("$._embedded.collectionDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(1))
                .andDo(print())
                .andDo(document("fetch-collections-slice"));

        verify(service).getCollections(any(Pageable.class));
    }

    @Test
    void returns_proper_status_code_on_empty_database() throws Exception {
        when(service.getCollections(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        mockMvc.perform(get("/api/collections"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("fetch-collections-empty"));

        verify(service).getCollections(any(Pageable.class));
    }

    @Test
    void can_fetch_one_existing_collection() throws Exception {
        var title = "Moderne Kunst";
        var titleMap = Map.of("de", title);
        var id = new Collection.CollectionId(1L);
        var collection = Collection.builder().id(id).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.getByReference(any())).thenReturn(collection);

        mockMvc
                .perform(get("/api/collections/1"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$.title.de").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print())
                .andDo(document("fetch-collection-1"));

        verify(service).getByReference(any());
    }

    @Test
    void can_delete_existing_collection() throws Exception {
        mockMvc.perform(delete("/api/collections/1"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delete-collection-1"));

        verify(service).delete(any());
    }

    @Test
    void can_replace_existing_collection() throws Exception {
        var title = "Neue Moderne";
        var titleMap = Map.of("de", title);
        var key = 1L;
        var cmd = UpdateCollectionCommand.builder().title(titleMap)
                .creatorId(new Creator.CreatorId(1L)).build();
        var collection = Collection.builder()
                .id(new Collection.CollectionId(key)).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.update(any(), any())).thenReturn(collection);

        mockMvc
                .perform(put("/api/collections/{key}", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/collections/1")))
                .andExpect(jsonPath("$.title.de").value(title))
                .andDo(print())
                .andDo(document("replace-collection-1"));

        verify(service).update(any(), any());
    }

    @Test
    void can_update_existing_collection() throws Exception {
        var title = "Neue Moderne";
        var titleMap = Map.of("de", title);
        var key = 1L;
        var cmd = UpdateCollectionCommand.builder().title(titleMap)
                .creatorId(new Creator.CreatorId(1L)).build();
        var collection = Collection.builder()
                .id(new Collection.CollectionId(key)).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.update(any(), any())).thenReturn(collection);

        mockMvc
                .perform(patch("/api/collections/{key}", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/collections/1")))
                .andExpect(jsonPath("$.title.de").value(title))
                .andDo(print())
                .andDo(document("patch-collection-1"));

        verify(service).update(any(), any());
    }

    @Test
    void can_add_point_of_interest() throws Exception {
        var title = "Moderne Kunst";
        var titleMap = Map.of("de", title);
        var key = 1L;
        var poiId = new PointOfInterest.PointOfInterestId(1L);
        var creatorId = new Creator.CreatorId(1L);
        var cmd = AddPointOfInterestCommand.builder().poiId(poiId).creatorId(creatorId).build();
        var collection = Collection.builder()
                .id(new Collection.CollectionId(key)).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.addPointOfInterest(any(), any())).thenReturn(collection);

        mockMvc
                .perform(post("/api/collections/{key}/pois", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title.de").value(title))
                .andDo(print())
                .andDo(document("add-poi-to-collection"));

        verify(service).addPointOfInterest(any(), any());
    }

    @Test
    void can_remove_point_of_interest() throws Exception {
        var key = 1L;
        var poiKey = 1L;
        var creatorId = 1L;

        mockMvc
                .perform(delete("/api/collections/{key}/pois/{poiKey}", key, poiKey)
                        .param("creatorId", String.valueOf(creatorId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("remove-poi-from-collection"));

        verify(service).removePointOfInterest(any(), any());
    }

    @Test
    void can_add_subcollection() throws Exception {
        var title = "Moderne Kunst";
        var titleMap = Map.of("de", title);
        var key = 1L;
        var subId = new Collection.CollectionId(2L);
        var creatorId = new Creator.CreatorId(1L);
        var cmd = AddSubcollectionCommand.builder().subcollectionId(subId).creatorId(creatorId).build();
        var collection = Collection.builder()
                .id(new Collection.CollectionId(key)).title(titleMap).pointsOfInterest(new HashSet<>()).subCollections(new HashSet<>()).build();

        when(service.addSubcollection(any(), any())).thenReturn(collection);

        mockMvc
                .perform(post("/api/collections/{key}/subcollections", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title.de").value(title))
                .andDo(print())
                .andDo(document("add-subcollection-to-collection"));

        verify(service).addSubcollection(any(), any());
    }

    @Test
    void can_remove_subcollection() throws Exception {
        var key = 1L;
        var subKey = 2L;
        var creatorId = 1L;

        mockMvc
                .perform(delete("/api/collections/{key}/subcollections/{subKey}", key, subKey)
                        .param("creatorId", String.valueOf(creatorId)))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("remove-subcollection-from-collection"));

        verify(service).removeSubcollection(any(), any());
    }
}

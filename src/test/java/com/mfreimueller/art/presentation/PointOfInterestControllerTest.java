package com.mfreimueller.art.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.commands.UpdatePointOfInterestCommand;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.PointOfInterestDto;
import com.mfreimueller.art.service.PointOfInterestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(PointOfInterestController.class)
class PointOfInterestControllerTest {
    private @MockitoBean PointOfInterestService service;

    private @Autowired MockMvc mockMvc;

    private @Autowired ObjectMapper objectMapper;

    @Test
    void can_create_point_of_interest() throws Exception {
        var title = "Das Bildnis des Dorian Gray";
        var titleMap = Map.of("de", title);
        var creatorId = new Creator.CreatorId(1L);
        var creator = Creator.builder().username("idefix").id(creatorId).build();

        var id = new PointOfInterest.PointOfInterestId(1L);
        var poi = PointOfInterest
                .builder()
                .title(titleMap)
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
                .andExpect(header().stringValues("Location", "http://localhost/api/pois/1"))
                .andExpect(jsonPath("$.title.de").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print());

        verify(service).create(any());
    }

    @Test
    void can_fetch_all_existing_points_of_interest() throws Exception {
        var title = "Das Bildnis des Dorian Gray";
        var titleMap = Map.of("de", title);
        var id = new PointOfInterest.PointOfInterestId(1L);

        var pointOfInterestDto = new PointOfInterestDto(id, titleMap);

        when(service.getPointsOfInterest()).thenReturn(List.of(pointOfInterestDto));

        mockMvc
                .perform(get("/api/pois"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/json"))
                .andExpect(jsonPath("$[0].title.de").value(title))
                .andExpect(jsonPath("$[0].id.id").value(id.id()))
                .andDo(print());

        verify(service).getPointsOfInterest();
    }

    @Test
    void returns_proper_status_code_on_empty_database() throws Exception {
        when(service.getPointsOfInterest()).thenReturn(List.of());

        mockMvc.perform(get("/api/pois"))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(service).getPointsOfInterest();
    }

    @Test
    void can_fetch_one_existing_point_of_interest() throws Exception {
        var title = "Das Bildnis des Dorian Gray";
        var titleMap = Map.of("de", title);
        var id = new PointOfInterest.PointOfInterestId(1L);

        var dto = new PointOfInterestDto(id, titleMap);

        when(service.getPointOfInterest(any())).thenReturn(Optional.of(dto));

        mockMvc
                .perform(get("/api/pois/1"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/json"))
                .andExpect(jsonPath("$.title.de").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print());

        verify(service).getPointOfInterest(any());
    }

    @Test
    void returns_proper_status_code_when_fetching_non_existing_point_of_interest() throws Exception {
        when(service.getPointOfInterest(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pois/1"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void can_delete_existing_point_of_interest() throws Exception {
        when(service.delete(any())).thenReturn(true);

        mockMvc.perform(delete("/api/pois/1"))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(service).delete(any());
    }

    @Test
    void cant_delete_missing_data() throws Exception {
        when(service.delete(any())).thenReturn(false);

        mockMvc.perform(delete("/api/pois/1"))
                .andExpect(status().isNotFound())
                .andDo(print());

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
                .andExpect(header().string("Location", "http://localhost/api/pois/1"))
                .andExpect(jsonPath("$.title.en").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print());

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
                .andDo(print());

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
                .andExpect(header().string("Location", "http://localhost/api/pois/1"))
                .andExpect(jsonPath("$.title.en").value(title))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print());

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
                .andDo(print());

        verify(service).update(any(), any());
    }
}
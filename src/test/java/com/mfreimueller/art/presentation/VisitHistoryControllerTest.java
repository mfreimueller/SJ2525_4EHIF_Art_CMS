package com.mfreimueller.art.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mfreimueller.art.MapperTestConfig;
import com.mfreimueller.art.commands.CreateVisitHistoryCommand;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.mappers.VisitHistoryMapper;
import com.mfreimueller.art.mappers.VisitorMapper;
import com.mfreimueller.art.presentation.assembler.VisitHistoryModelAssembler;
import com.mfreimueller.art.richtypes.Duration;
import com.mfreimueller.art.service.VisitHistoryService;
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

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitHistoryController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import({MapperTestConfig.class, VisitHistoryModelAssembler.class})
class VisitHistoryControllerTest extends AbstractDocumentationControllerTest {
    private @MockitoBean VisitHistoryService service;

    private @MockitoSpyBean VisitHistoryMapper mapper;
    private @MockitoSpyBean VisitorMapper visitorMapper;
    private @MockitoSpyBean VisitHistoryModelAssembler assembler;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        super.setUp(webApplicationContext, restDocumentation);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void can_create_visit_history() throws Exception {
        var poiId = new PointOfInterest.PointOfInterestId(1L);
        var poi = PointOfInterest.builder().id(poiId).build();
        var visitorId = new Visitor.VisitorId(1L);
        var duration = new Duration(30);
        var visitedOn = ZonedDateTime.parse("2024-06-15T10:00:00+02:00");
        var id = new VisitHistory.VisitHistoryId(1L);

        var visitHistory = VisitHistory.builder()
                .id(id).duration(duration).visitedOn(visitedOn)
                .pointsOfInterest(List.of(poi))
                .visitor(Visitor.builder().id(visitorId).build())
                .build();

        when(service.create(any())).thenReturn(visitHistory);

        var cmd = CreateVisitHistoryCommand.builder()
                .pointsOfInterest(List.of(poi))
                .duration(duration)
                .visitedOn(visitedOn)
                .visitorId(visitorId)
                .build();

        mockMvc
                .perform(post("/api/visit-histories")
                        .header("Accept", "application/json")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.matchesPattern("http://localhost(?::8080)?/api/visit-histories/1")))
                .andExpect(jsonPath("$.duration.value").value(30))
                .andDo(print())
                .andDo(document("create-visit-history-1"));

        verify(service).create(any());
    }

    @Test
    void can_fetch_existing_visit_histories() throws Exception {
        var poiId = new PointOfInterest.PointOfInterestId(1L);
        var poi = PointOfInterest.builder().id(poiId).build();
        var visitorId = new Visitor.VisitorId(1L);
        var visitor = Visitor.builder().id(visitorId).username("jdoe").build();
        var id = new VisitHistory.VisitHistoryId(1L);
        var duration = new Duration(30);
        var visitedOn = ZonedDateTime.parse("2024-06-15T10:00:00+02:00");

        var visitHistory = VisitHistory.builder()
                .id(id).duration(duration).visitedOn(visitedOn)
                .pointsOfInterest(List.of(poi))
                .visitor(visitor)
                .build();

        when(service.getVisitHistories(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(visitHistory), PageRequest.of(0, 20), false));

        mockMvc
                .perform(get("/api/visit-histories"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.visitHistoryDtoList[0].duration.value").value(30))
                .andExpect(jsonPath("$._embedded.visitHistoryDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andDo(print())
                .andDo(document("fetch-visit-histories"));

        verify(service).getVisitHistories(any(Pageable.class));
    }

    @Test
    void can_fetch_existing_visit_histories_slice() throws Exception {
        var visitorId = new Visitor.VisitorId(1L);
        var visitor = Visitor.builder().id(visitorId).username("jdoe").build();
        var id = new VisitHistory.VisitHistoryId(1L);
        var duration = new Duration(30);
        var visitHistory = VisitHistory.builder()
                .id(id).duration(duration).visitor(visitor).build();

        when(service.getVisitHistories(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(visitHistory), PageRequest.of(1, 5), true));

        mockMvc
                .perform(get("/api/visit-histories").param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.visitHistoryDtoList[0].duration.value").value(30))
                .andExpect(jsonPath("$._embedded.visitHistoryDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(1))
                .andDo(print())
                .andDo(document("fetch-visit-histories-slice"));

        verify(service).getVisitHistories(any(Pageable.class));
    }

    @Test
    void returns_proper_status_code_on_empty_database() throws Exception {
        when(service.getVisitHistories(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        mockMvc.perform(get("/api/visit-histories"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("fetch-visit-histories-empty"));

        verify(service).getVisitHistories(any(Pageable.class));
    }

    @Test
    void can_fetch_one_existing_visit_history() throws Exception {
        var poiId = new PointOfInterest.PointOfInterestId(1L);
        var poi = PointOfInterest.builder().id(poiId).build();
        var visitorId = new Visitor.VisitorId(1L);
        var visitor = Visitor.builder().id(visitorId).username("jdoe").build();
        var id = new VisitHistory.VisitHistoryId(1L);
        var duration = new Duration(30);
        var visitedOn = ZonedDateTime.parse("2024-06-15T10:00:00+02:00");

        var visitHistory = VisitHistory.builder()
                .id(id).duration(duration).visitedOn(visitedOn)
                .pointsOfInterest(List.of(poi))
                .visitor(visitor)
                .build();

        when(service.getByReference(any())).thenReturn(visitHistory);

        mockMvc
                .perform(get("/api/visit-histories/1"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$.duration.value").value(30))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print())
                .andDo(document("fetch-visit-history-1"));

        verify(service).getByReference(any());
    }


}

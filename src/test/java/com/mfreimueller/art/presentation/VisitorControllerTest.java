package com.mfreimueller.art.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mfreimueller.art.MapperTestConfig;
import com.mfreimueller.art.commands.CreateVisitorCommand;
import com.mfreimueller.art.commands.UpdateVisitorCommand;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.mappers.VisitorMapper;
import com.mfreimueller.art.presentation.assembler.VisitorModelAssembler;
import com.mfreimueller.art.service.VisitorService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitorController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import({MapperTestConfig.class, VisitorModelAssembler.class})
class VisitorControllerTest extends AbstractDocumentationControllerTest {
    private @MockitoBean VisitorService service;

    private @MockitoSpyBean VisitorMapper mapper;
    private @MockitoSpyBean VisitorModelAssembler assembler;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        super.setUp(webApplicationContext, restDocumentation);
    }

    @Test
    void can_create_visitor() throws Exception {
        var username = "jdoe";
        var email = "jdoe@example.com";
        var id = new Visitor.VisitorId(1L);
        var visitor = Visitor.builder()
                .id(id).username(username).emailAddress(email).build();

        when(service.create(any())).thenReturn(visitor);

        var cmd = CreateVisitorCommand.builder().username(username).emailAddress(email).build();

        mockMvc
                .perform(post("/api/visitors")
                        .header("Accept", "application/json")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/visitors/1")))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.emailAddress").value(email))
                .andDo(print())
                .andDo(document("create-visitor-1"));

        verify(service).create(any());
    }

    @Test
    void can_fetch_existing_visitors() throws Exception {
        var username = "jdoe";
        var email = "jdoe@example.com";
        var id = new Visitor.VisitorId(1L);
        var visitor = Visitor.builder().id(id).username(username).emailAddress(email).build();

        when(service.getVisitors(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(visitor), PageRequest.of(0, 20), false));

        mockMvc
                .perform(get("/api/visitors"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.visitorDtoList[0].username").value(username))
                .andExpect(jsonPath("$._embedded.visitorDtoList[0].emailAddress").value(email))
                .andExpect(jsonPath("$._embedded.visitorDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andDo(print())
                .andDo(document("fetch-visitors"));

        verify(service).getVisitors(any(Pageable.class));
    }

    @Test
    void can_fetch_existing_visitors_slice() throws Exception {
        var username = "jdoe";
        var email = "jdoe@example.com";
        var id = new Visitor.VisitorId(1L);
        var visitor = Visitor.builder().id(id).username(username).emailAddress(email).build();

        when(service.getVisitors(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of(visitor), PageRequest.of(1, 5), true));

        mockMvc
                .perform(get("/api/visitors").param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.visitorDtoList[0].username").value(username))
                .andExpect(jsonPath("$._embedded.visitorDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(1))
                .andDo(print())
                .andDo(document("fetch-visitors-slice"));

        verify(service).getVisitors(any(Pageable.class));
    }

    @Test
    void returns_proper_status_code_on_empty_database() throws Exception {
        when(service.getVisitors(any(Pageable.class)))
                .thenReturn(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        mockMvc.perform(get("/api/visitors"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("fetch-visitors-empty"));

        verify(service).getVisitors(any(Pageable.class));
    }

    @Test
    void can_fetch_one_existing_visitor() throws Exception {
        var username = "jdoe";
        var email = "jdoe@example.com";
        var id = new Visitor.VisitorId(1L);
        var visitor = Visitor.builder().id(id).username(username).emailAddress(email).build();

        when(service.getByReference(any())).thenReturn(visitor);

        mockMvc
                .perform(get("/api/visitors/1"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print())
                .andDo(document("fetch-visitor-1"));

        verify(service).getByReference(any());
    }

    @Test
    void can_delete_existing_visitor() throws Exception {
        mockMvc.perform(delete("/api/visitors/1"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delete-visitor-1"));

        verify(service).delete(any());
    }

    @Test
    void can_replace_existing_visitor() throws Exception {
        var username = "jdoe2";
        var email = "jdoe2@example.com";
        var key = 1L;
        var cmd = UpdateVisitorCommand.builder().username(username).email(email).build();
        var visitor = Visitor.builder()
                .id(new Visitor.VisitorId(key)).username(username).emailAddress(email).build();

        when(service.update(any(), any())).thenReturn(visitor);

        mockMvc
                .perform(put("/api/visitors/{key}", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/visitors/1")))
                .andExpect(jsonPath("$.username").value(username))
                .andDo(print())
                .andDo(document("replace-visitor-1"));

        verify(service).update(any(), any());
    }

    @Test
    void can_update_existing_visitor() throws Exception {
        var username = "jdoe2";
        var email = "jdoe2@example.com";
        var key = 1L;
        var cmd = UpdateVisitorCommand.builder().username(username).email(email).build();
        var visitor = Visitor.builder()
                .id(new Visitor.VisitorId(key)).username(username).emailAddress(email).build();

        when(service.update(any(), any())).thenReturn(visitor);

        mockMvc
                .perform(patch("/api/visitors/{key}", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/visitors/1")))
                .andExpect(jsonPath("$.username").value(username))
                .andDo(print())
                .andDo(document("patch-visitor-1"));

        verify(service).update(any(), any());
    }
}

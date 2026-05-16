package com.mfreimueller.art.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mfreimueller.art.MapperTestConfig;
import com.mfreimueller.art.commands.CreateCreatorCommand;
import com.mfreimueller.art.commands.UpdateCreatorCommand;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.mappers.CreatorMapper;
import com.mfreimueller.art.presentation.assembler.CreatorModelAssembler;
import com.mfreimueller.art.service.CreatorService;
import org.hamcrest.Matchers;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreatorController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import({MapperTestConfig.class, CreatorModelAssembler.class})
class CreatorControllerTest extends AbstractDocumentationControllerTest {

    private @MockitoBean CreatorService service;

    private @MockitoSpyBean CreatorMapper mapper;
    private @MockitoSpyBean CreatorModelAssembler assembler;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        super.setUp(webApplicationContext, restDocumentation);
    }

    @Test
    void can_create_creator() throws Exception {
        var username = "idefix";
        var password = "secret";
        var role = Creator.Role.EDITOR;
        var id = new Creator.CreatorId(1L);
        var creator = Creator.builder()
                .id(id)
                .username(username)
                .password(password)
                .role(role)
                .build();

        when(service.create(any())).thenReturn(creator);

        var cmd = CreateCreatorCommand.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        mockMvc
                .perform(post("/api/creators")
                        .header("Accept", "application/json")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/creators/1")))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value(role.name()))
                .andDo(print())
                .andDo(document("create-creator-1"));

        verify(service).create(any());
    }

    @Test
    void can_fetch_existing_creators() throws Exception {
        var username = "idefix";
        var role = Creator.Role.EDITOR;
        var id = new Creator.CreatorId(1L);
        var creator = Creator.builder()
                .id(id)
                .username(username)
                .role(role)
                .build();

        when(service.getCreators(any()))
                .thenReturn(new SliceImpl<>(List.of(creator), PageRequest.of(0, 20), false));

        mockMvc
                .perform(get("/api/creators"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.creatorDtoList[0].username").value(username))
                .andExpect(jsonPath("$._embedded.creatorDtoList[0].role").value(role.name()))
                .andExpect(jsonPath("$._embedded.creatorDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andDo(print())
                .andDo(document("fetch-creators"));

        verify(service).getCreators(any());
    }

    @Test
    void can_fetch_existing_creators_slice() throws Exception {
        var username = "asterix";
        var role = Creator.Role.ADMIN;
        var id = new Creator.CreatorId(1L);
        var creator = Creator.builder()
                .id(id)
                .username(username)
                .role(role)
                .build();

        when(service.getCreators(any()))
                .thenReturn(new SliceImpl<>(List.of(creator), PageRequest.of(1, 5), true));

        mockMvc
                .perform(get("/api/creators").param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$._embedded.creatorDtoList[0].username").value(username))
                .andExpect(jsonPath("$._embedded.creatorDtoList[0].role").value(role.name()))
                .andExpect(jsonPath("$._embedded.creatorDtoList[0].id.id").value(id.id()))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(1))
                .andDo(print())
                .andDo(document("fetch-creators-slice"));

        verify(service).getCreators(any());
    }

    @Test
    void returns_proper_status_code_on_empty_database() throws Exception {
        when(service.getCreators(any()))
                .thenReturn(new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, 20), false));

        mockMvc.perform(get("/api/creators"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("fetch-creators-empty"));

        verify(service).getCreators(any());
    }

    @Test
    void can_fetch_one_existing_creator() throws Exception {
        var username = "idefix";
        var role = Creator.Role.VIEWER;
        var id = new Creator.CreatorId(1L);
        var creator = Creator.builder()
                .id(id)
                .username(username)
                .role(role)
                .build();

        when(service.getByReference(any())).thenReturn(creator);

        mockMvc
                .perform(get("/api/creators/1"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/hal+json"))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value(role.name()))
                .andExpect(jsonPath("$.id.id").value(id.id()))
                .andDo(print())
                .andDo(document("fetch-creator-1"));

        verify(service).getByReference(any());
    }

    @Test
    void can_delete_existing_creator() throws Exception {
        mockMvc.perform(delete("/api/creators/1"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delete-creator-1"));

        verify(service).delete(any());
    }

    @Test
    void can_replace_existing_creator() throws Exception {
        var username = "newuser";
        var password = "newpass";
        var role = Creator.Role.EDITOR;
        var key = 1L;
        var cmd = UpdateCreatorCommand.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
        var creator = Creator.builder()
                .id(new Creator.CreatorId(key))
                .username(username)
                .password(password)
                .role(role)
                .build();

        when(service.update(any(), any())).thenReturn(creator);

        mockMvc
                .perform(put("/api/creators/{key}", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/creators/1")))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value(role.name()))
                .andDo(print())
                .andDo(document("replace-creator-1"));

        verify(service).update(any(), any());
    }

    @Test
    void can_update_existing_creator() throws Exception {
        var username = "patcheduser";
        var password = "patchedpass";
        var role = Creator.Role.VIEWER;
        var key = 1L;
        var cmd = UpdateCreatorCommand.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
        var creator = Creator.builder()
                .id(new Creator.CreatorId(key))
                .username(username)
                .password(password)
                .role(role)
                .build();

        when(service.update(any(), any())).thenReturn(creator);

        mockMvc
                .perform(patch("/api/creators/{key}", key)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsBytes(cmd)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern("http://localhost(?::8080)?/api/creators/1")))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value(role.name()))
                .andDo(print())
                .andDo(document("patch-creator-1"));

        verify(service).update(any(), any());
    }
}

package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.service.CreatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreatorWebControllerTest extends AbstractWebControllerTest {

    @Autowired
    private CreatorWebController controller;

    @MockitoBean
    private CreatorService creatorService;

    @Test
    void list_shows_all_creators() {
        when(creatorService.getCreators(any())).thenReturn(new org.springframework.data.domain.SliceImpl<>(java.util.List.of()));

        Model model = new BindingAwareModelMap();
        String viewName = controller.list(model);

        assertThat(viewName).isEqualTo("creator/list");
        assertThat(model.getAttribute("creators")).isNotNull();
    }

    @Test
    void detail_shows_creator() {
        var creator = Creator.builder()
                .id(new Creator.CreatorId(1L))
                .username("testuser")
                .role(Creator.Role.VIEWER)
                .build();

        when(creatorService.getByReference(any())).thenReturn(creator);

        Model model = new BindingAwareModelMap();
        String viewName = controller.detail(1L, model);

        assertThat(viewName).isEqualTo("creator/detail");
        assertThat(model.getAttribute("creator")).isNotNull();
    }

    @Test
    void create_form_returns_create_view() {
        String viewName = controller.createForm();
        assertThat(viewName).isEqualTo("creator/create");
    }

    @Test
    void create_creates_creator_and_redirects() {
        var creator = Creator.builder()
                .id(new Creator.CreatorId(1L))
                .username("newuser")
                .role(Creator.Role.VIEWER)
                .build();

        when(creatorService.create(any())).thenReturn(creator);

        String viewName = controller.create("newuser", "pass", Creator.Role.VIEWER);

        assertThat(viewName).isEqualTo("redirect:/web/creators/1");
        verify(creatorService).create(any());
    }

    @Test
    void edit_form_shows_creator() {
        var creator = Creator.builder()
                .id(new Creator.CreatorId(1L))
                .username("edituser")
                .role(Creator.Role.EDITOR)
                .build();

        when(creatorService.getByReference(any())).thenReturn(creator);

        Model model = new BindingAwareModelMap();
        String viewName = controller.editForm(1L, model);

        assertThat(viewName).isEqualTo("creator/edit");
        assertThat(model.getAttribute("creator")).isNotNull();
    }

    @Test
    void edit_updates_creator_and_redirects() {
        String viewName = controller.edit(1L, "updated", "pass", Creator.Role.ADMIN);

        assertThat(viewName).isEqualTo("redirect:/web/creators/1");
        verify(creatorService).update(any(), any());
    }

    @Test
    void delete_confirm_shows_creator() {
        var creator = Creator.builder()
                .id(new Creator.CreatorId(1L))
                .username("deleteuser")
                .role(Creator.Role.VIEWER)
                .build();

        when(creatorService.getByReference(any())).thenReturn(creator);

        Model model = new BindingAwareModelMap();
        String viewName = controller.deleteConfirm(1L, model);

        assertThat(viewName).isEqualTo("creator/confirm-delete");
        assertThat(model.getAttribute("creator")).isNotNull();
    }

    @Test
    void delete_removes_creator_and_redirects() {
        String viewName = controller.delete(1L);

        assertThat(viewName).isEqualTo("redirect:/web/creators");
        verify(creatorService).delete(any());
    }
}

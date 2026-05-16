package com.mfreimueller.art.presentation;

import com.mfreimueller.art.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@ExtendWith(SpringExtension.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileStorageService fileStorageService;

    @Test
    void serves_existing_file() throws Exception {
        var resource = new ByteArrayResource("hello".getBytes());
        when(fileStorageService.load("test.txt")).thenReturn(resource);

        mockMvc.perform(get("/api/files/test.txt"))
                .andExpect(status().isOk())
                .andExpect(content().bytes("hello".getBytes()));
    }

    @Test
    void returns_404_for_missing_file() throws Exception {
        when(fileStorageService.load("missing.txt")).thenThrow(new RuntimeException("not found"));

        mockMvc.perform(get("/api/files/missing.txt"))
                .andExpect(status().isNotFound());
    }
}

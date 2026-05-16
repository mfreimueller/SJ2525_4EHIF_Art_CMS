package com.mfreimueller.art.service;

import com.mfreimueller.art.foundation.UploadProperties;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @Test
    void stores_file_and_returns_uuid_filename() {
        var props = new UploadProperties(tempDir, 10_485_760);
        var service = new FileStorageService(props);
        service.init();

        var file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "hello".getBytes());
        var stored = service.store(file);

        assertThat(stored).endsWith(".jpg");
        assertThat(stored).hasSize(40); // UUID (36) + ".jpg" (4)
        assertThat(tempDir.resolve(stored)).exists();
    }

    @Test
    void rejects_empty_file() {
        var props = new UploadProperties(tempDir, 10_485_760);
        var service = new FileStorageService(props);
        service.init();

        var file = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> service.store(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void rejects_unsupported_extension() {
        var props = new UploadProperties(tempDir, 10_485_760);
        var service = new FileStorageService(props);
        service.init();

        var file = new MockMultipartFile("file", "malware.exe", "application/octet-stream", "bad".getBytes());

        assertThatThrownBy(() -> service.store(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not allowed");
    }

    @Test
    void loads_stored_file() {
        var props = new UploadProperties(tempDir, 10_485_760);
        var service = new FileStorageService(props);
        service.init();

        var file = new MockMultipartFile("file", "test.png", "image/png", "data".getBytes());
        var stored = service.store(file);

        var resource = service.load(stored);
        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
    }

    @Test
    void throws_on_missing_file() {
        var props = new UploadProperties(tempDir, 10_485_760);
        var service = new FileStorageService(props);
        service.init();

        assertThatThrownBy(() -> service.load("nonexistent.jpg"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deletes_existing_file() {
        var props = new UploadProperties(tempDir, 10_485_760);
        var service = new FileStorageService(props);
        service.init();

        var file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());
        var stored = service.store(file);

        service.delete(stored);
        assertThat(tempDir.resolve(stored)).doesNotExist();
    }

    @Test
    void delete_nonexistent_file_is_noop() {
        var props = new UploadProperties(tempDir, 10_485_760);
        var service = new FileStorageService(props);
        service.init();

        service.delete("nonexistent.txt");
    }
}

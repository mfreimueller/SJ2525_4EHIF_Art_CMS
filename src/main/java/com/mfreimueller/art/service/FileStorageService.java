package com.mfreimueller.art.service;

import com.mfreimueller.art.foundation.UploadProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "svg", "bmp",
            "mp3", "wav", "ogg", "aac", "flac",
            "mp4", "webm", "avi", "mov", "mkv",
            "pdf", "txt", "json", "xml", "csv"
    );

    private final UploadProperties uploadProperties;

    private Path uploadDir;

    @PostConstruct
    void init() {
        uploadDir = uploadProperties.dir().toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadDir);
            log.debug("Upload directory created at {}", uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        var originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("File must have a name");
        }

        var extension = "";
        var dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("File extension '%s' is not allowed".formatted(extension));
        }

        var storedFilename = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        var targetPath = uploadDir.resolve(storedFilename);

        try {
            file.transferTo(targetPath);
            log.debug("Stored file {} as {}", originalFilename, storedFilename);
            return storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + originalFilename, e);
        }
    }

    public Resource load(String filename) {
        try {
            var filePath = uploadDir.resolve(filename).normalize();
            if (!filePath.startsWith(uploadDir)) {
                throw new IllegalArgumentException("Cannot access file outside upload directory");
            }
            var resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + filename, e);
        }
    }

    public void delete(String filename) {
        try {
            var filePath = uploadDir.resolve(filename).normalize();
            if (!filePath.startsWith(uploadDir)) {
                throw new IllegalArgumentException("Cannot delete file outside upload directory");
            }
            Files.deleteIfExists(filePath);
            log.debug("Deleted file {}", filename);
        } catch (IOException e) {
            log.warn("Failed to delete file {}: {}", filename, e.getMessage());
        }
    }
}

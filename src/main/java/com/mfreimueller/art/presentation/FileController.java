package com.mfreimueller.art.presentation;

import com.mfreimueller.art.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.mfreimueller.art.util.LogHelper.logEnter;
import static com.mfreimueller.art.util.LogHelper.logExit;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        logEnter(log);

        try {
            var resource = fileStorageService.load(filename);
            var contentType = MediaTypeFactory.getMediaType(resource)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);

            logExit(log);
            return ResponseEntity.ok()
                    .contentType(contentType)
                    .body(resource);
        } catch (RuntimeException e) {
            log.warn("File not found: {}", filename);
            logExit(log);
            return ResponseEntity.notFound().build();
        }
    }
}

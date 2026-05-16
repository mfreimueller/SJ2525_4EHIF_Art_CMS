package com.mfreimueller.art.presentation;

import com.mfreimueller.art.domain.Source;
import com.mfreimueller.art.service.FileStorageService;
import com.mfreimueller.art.service.ImageContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.mfreimueller.art.util.LogHelper.logEnter;
import static com.mfreimueller.art.util.LogHelper.logExit;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/content/image")
public class ImageContentController {

    private final ImageContentService imageContentService;
    private final FileStorageService fileStorageService;

    @PostMapping("/{id}/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        logEnter(log);

        var storedFilename = fileStorageService.store(file);
        var source = new Source(storedFilename, Source.LinkType.Relative);
        imageContentService.updateSource(id, source);

        var fileUrl = "/api/files/" + storedFilename;

        logExit(log);
        return ResponseEntity.ok(Map.of(
                "filename", storedFilename,
                "fileUrl", fileUrl,
                "contentId", id
        ));
    }
}

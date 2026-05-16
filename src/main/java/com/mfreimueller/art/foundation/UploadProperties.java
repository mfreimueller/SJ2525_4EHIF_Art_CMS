package com.mfreimueller.art.foundation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.upload")
public record UploadProperties(Path dir, long maxFileSize) {

    public static final String DEFAULT_DIR = "uploads";
    public static final long DEFAULT_MAX_FILE_SIZE = 10_485_760;

    public UploadProperties {
        if (dir == null) {
            dir = Path.of(DEFAULT_DIR);
        }
        if (maxFileSize <= 0) {
            maxFileSize = DEFAULT_MAX_FILE_SIZE;
        }
    }
}

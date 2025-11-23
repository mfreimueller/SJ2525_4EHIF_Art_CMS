package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.PutImageContentCommand;
import com.mfreimueller.art.commands.PutTextContentCommand;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.ImageContent;
import com.mfreimueller.art.domain.TextContent;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.ImageContentRepository;
import com.mfreimueller.art.persistence.TextContentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
@Slf4j
public class ImageContentService {

    private final ImageContentRepository imageContentRepository;
    private final CreatorService creatorService;
    private final DateTimeFactory dateTimeFactory;

    @Transactional(readOnly = false)
    public ImageContent create(@NotNull @Valid PutImageContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var imageContent = ImageContent.builder()
                .description(cmd.description())
                .source(cmd.source())
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        var saved = imageContentRepository.save(imageContent);
        log.debug("Created new image content with id {}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = false)
    public ImageContent update(@NotNull Content.ContentId id, @NotNull @Valid PutImageContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var imageContent = imageContentRepository.getReferenceById(id); // TODO: handle exception
        imageContent.setDescription(cmd.description());
        imageContent.setSource(cmd.source());
        imageContent.setUpdatedAt(dateTimeFactory.now());
        imageContent.setUpdatedBy(creator);

        var saved = imageContentRepository.save(imageContent);
        log.debug("Updated image content with id {}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Content.ContentId id) {
        imageContentRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
        log.debug("Deleted image content with id {}", id.id());
    }

    public ImageContent getByReference(@NotNull Content.ContentId id) {
        return imageContentRepository.getReferenceById(id);
    }

}

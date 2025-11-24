package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.PutTextContentCommand;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.TextContent;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.TextContentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class TextContentService {

    private final TextContentRepository textContentRepository;
    private final CreatorService creatorService;
    private final DateTimeFactory dateTimeFactory;

    @Transactional(readOnly = false)
    public TextContent create(@NotNull @Valid PutTextContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var textContent = TextContent.builder()
                .description(cmd.description())
                .shortText(cmd.shortText())
                .longText(cmd.longText())
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        return textContentRepository.save(textContent);
    }

    @Transactional(readOnly = false)
    public TextContent update(@NotNull Content.ContentId id, @NotNull @Valid PutTextContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var textContent = textContentRepository.getReferenceById(id);
        textContent.setDescription(cmd.description());
        textContent.setShortText(cmd.shortText());
        textContent.setLongText(cmd.longText());
        textContent.setUpdatedAt(dateTimeFactory.now());
        textContent.setUpdatedBy(creator);

        return textContentRepository.save(textContent);
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Content.ContentId id) {
        textContentRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
    }

    public TextContent getByReference(@NotNull Content.ContentId id) {
        return textContentRepository.getReferenceById(id);
    }

}

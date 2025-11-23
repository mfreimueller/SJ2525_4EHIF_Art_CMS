package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateUpdateTextContentCommand;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.TextContent;
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

    @Transactional(readOnly = false)
    public TextContent create(@NotNull @Valid CreateUpdateTextContentCommand cmd) {
        var textContent = TextContent.builder()
                .description(cmd.description())
                .shortText(cmd.shortText())
                .longText(cmd.longText())
                .build();

        return textContentRepository.save(textContent);
    }

    @Transactional(readOnly = false)
    public TextContent update(@NotNull Content.ContentId id, @NotNull @Valid CreateUpdateTextContentCommand cmd) {
        var creator = textContentRepository.getReferenceById(id); // TODO: handle exception
        creator.setDescription(cmd.description());
        creator.setShortText(cmd.shortText());
        creator.setLongText(cmd.longText());

        return textContentRepository.save(creator);
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Content.ContentId id) {
        textContentRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
    }

    public TextContent getByReference(@NotNull Content.ContentId id) {
        return textContentRepository.getReferenceById(id);
    }

}

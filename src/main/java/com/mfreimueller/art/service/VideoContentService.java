package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.PutVideoContentCommand;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.VideoContent;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.VideoContentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class VideoContentService {

    private final VideoContentRepository videoContentRepository;
    private final CreatorService creatorService;
    private final DateTimeFactory dateTimeFactory;

    @Transactional(readOnly = false)
    public VideoContent create(@NotNull @Valid PutVideoContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var textContent = VideoContent.builder()
                .description(cmd.description())
                .duration(cmd.duration())
                .source(cmd.source())
                .subtitles(cmd.subtitles())
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        return videoContentRepository.save(textContent);
    }

    @Transactional(readOnly = false)
    public VideoContent update(@NotNull Content.ContentId id, @NotNull @Valid PutVideoContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var audioContent = videoContentRepository.getReferenceById(id);
        audioContent.setDescription(cmd.description());
        audioContent.setSource(cmd.source());
        audioContent.setDuration(cmd.duration());
        audioContent.setSubtitles(cmd.subtitles());
        audioContent.setUpdatedAt(dateTimeFactory.now());
        audioContent.setUpdatedBy(creator);

        return videoContentRepository.save(audioContent);
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Content.ContentId id) {
        videoContentRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
    }

    public VideoContent getByReference(@NotNull Content.ContentId id) {
        return videoContentRepository.getReferenceById(id);
    }

}

package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.PutAudioContentCommand;
import com.mfreimueller.art.domain.AudioContent;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.AudioContentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class AudioContentService {

    private final AudioContentRepository audioContentRepository;
    private final CreatorService creatorService;
    private final DateTimeFactory dateTimeFactory;

    @Transactional(readOnly = false)
    public AudioContent create(@NotNull @Valid PutAudioContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var textContent = AudioContent.builder()
                .description(cmd.description())
                .duration(cmd.duration())
                .source(cmd.source())
                .transcriptions(cmd.transcriptions())
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        return audioContentRepository.save(textContent);
    }

    @Transactional(readOnly = false)
    public AudioContent update(@NotNull Content.ContentId id, @NotNull @Valid PutAudioContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var audioContent = audioContentRepository.getReferenceById(id);
        audioContent.setDescription(cmd.description());
        audioContent.setSource(cmd.source());
        audioContent.setDuration(cmd.duration());
        audioContent.setTranscriptions(cmd.transcriptions());
        audioContent.setUpdatedAt(dateTimeFactory.now());
        audioContent.setUpdatedBy(creator);

        return audioContentRepository.save(audioContent);
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Content.ContentId id) {
        audioContentRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
    }

    public AudioContent getByReference(@NotNull Content.ContentId id) {
        return audioContentRepository.getReferenceById(id);
    }

}

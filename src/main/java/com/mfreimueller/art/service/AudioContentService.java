package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.PutAudioContentCommand;
import com.mfreimueller.art.domain.AudioContent;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.AudioContentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
@Slf4j
public class AudioContentService {

    private final AudioContentRepository audioContentRepository;
    private final CreatorService creatorService;
    private final DateTimeFactory dateTimeFactory;

    @Transactional(readOnly = false)
    public AudioContent create(@NotNull @Valid PutAudioContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var audioContent = AudioContent.builder()
                .description(cmd.description())
                .duration(cmd.duration())
                .source(cmd.source())
                .transcriptions(cmd.transcriptions())
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        var saved = audioContentRepository.save(audioContent);
        log.debug("Created new audio content with id {}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = false)
    public AudioContent update(@NotNull Content.ContentId id, @NotNull @Valid PutAudioContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());

        var audioContent = audioContentRepository.getReferenceById(id); // TODO: handle exception
        audioContent.setDescription(cmd.description());
        audioContent.setSource(cmd.source());
        audioContent.setDuration(cmd.duration());
        audioContent.setTranscriptions(cmd.transcriptions());
        audioContent.setUpdatedAt(dateTimeFactory.now());
        audioContent.setUpdatedBy(creator);

        var saved = audioContentRepository.save(audioContent);
        log.debug("Updated audio content with id {}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Content.ContentId id) {
        audioContentRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
        log.debug("Deleted audio content with id {}", id.id());
    }

    public AudioContent getByReference(@NotNull Content.ContentId id) {
        return audioContentRepository.getReferenceById(id);
    }

}

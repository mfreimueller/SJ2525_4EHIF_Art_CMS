package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.PutSlideshowContentCommand;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.SlideshowContent;
import com.mfreimueller.art.foundation.DataConstraintException;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.ContentRepository;
import com.mfreimueller.art.persistence.SlideshowContentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class SlideshowContentService {

    private final SlideshowContentRepository slideshowContentRepository;
    private final ContentRepository contentRepository;
    private final CreatorService creatorService;
    private final DateTimeFactory dateTimeFactory;

    @Transactional(readOnly = false)
    public SlideshowContent create(@NotNull @Valid PutSlideshowContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());
        var slides = cmd.slides().stream().map(contentRepository::getReferenceById).toList();

        var slideshow = SlideshowContent.builder()
                .description(cmd.description())
                .mode(cmd.mode())
                .speed(cmd.speed())
                .slides(slides)
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .build();

        return slideshowContentRepository.save(slideshow);
    }

    @Transactional(readOnly = false)
    public SlideshowContent update(@NotNull Content.ContentId id, @NotNull @Valid PutSlideshowContentCommand cmd) {
        var creator = creatorService.getByReference(cmd.creatorId());
        var slides = cmd.slides().stream().map(contentRepository::getReferenceById).toList();

        var slideshow = slideshowContentRepository.getReferenceById(id); // TODO: handle exception

        // we need to manually check for a circular reference here
        if (containsSlideshow(slides, slideshow)) {
            throw DataConstraintException.forCircularReference(SlideshowContent.class, slideshow.getId().id());
        }

        slideshow.setDescription(cmd.description());
        slideshow.setMode(cmd.mode());
        slideshow.setSpeed(cmd.speed());
        slideshow.setSlides(slides);

        slideshow.setUpdatedAt(dateTimeFactory.now());
        slideshow.setUpdatedBy(creator);

        return slideshowContentRepository.save(slideshow);
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Content.ContentId id) {
        slideshowContentRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
    }

    public SlideshowContent getByReference(@NotNull Content.ContentId id) {
        return slideshowContentRepository.getReferenceById(id);
    }

    private boolean containsSlideshow(List<Content> slides, SlideshowContent slideshow) {
        return slides.stream().anyMatch(c -> {
            if (c instanceof SlideshowContent sc) {
                if (sc.equals(slideshow)) {
                    return true;
                } else {
                    return containsSlideshow(sc.getSlides(), slideshow);
                }
            }

            return false;
        });
    }

}

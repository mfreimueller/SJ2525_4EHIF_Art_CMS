package com.mfreimueller.art.service;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.persistence.ContentRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    public Content getByReference(@NotNull Content.ContentId id) {
        return contentRepository.getReferenceById(id);
    }

}

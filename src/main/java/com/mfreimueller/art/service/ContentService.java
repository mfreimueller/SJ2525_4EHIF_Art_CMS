package com.mfreimueller.art.service;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.persistence.ContentRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    public Content getByReference(@NotNull Long id) {
        return contentRepository.getReferenceById(id);
    }

    public Slice<Content> getPaged(Long lastId, int pageSize) {
        var pageable = Pageable.ofSize(pageSize);
        // return contentRepository.findSliceWithKeysetPaging(lastId, pageable);
        return contentRepository.findAll(pageable);
    }

}

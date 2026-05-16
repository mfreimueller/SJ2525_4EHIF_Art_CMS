package com.mfreimueller.art.security;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.persistence.CreatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CurrentCreator {

    private final CreatorRepository creatorRepository;

    public Creator.CreatorId getId(Authentication auth) {
        var creator = creatorRepository.findByUsername(auth.getName()).orElseThrow();
        return creator.getId();
    }

    public Creator getCreator(Authentication auth) {
        return creatorRepository.findByUsername(auth.getName()).orElseThrow();
    }
}

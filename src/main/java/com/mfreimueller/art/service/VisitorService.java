package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateVisitorCommand;
import com.mfreimueller.art.commands.UpdateVisitorCommand;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.persistence.VisitorRepository;
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
public class VisitorService {

    private final VisitorRepository visitorRepository;

    @Transactional(readOnly = false)
    public Visitor create(@NotNull @Valid CreateVisitorCommand cmd) {
        var visitor = Visitor.builder()
                .username(cmd.username())
                .emailAddress(cmd.emailAddress())
                .build();

        var saved = visitorRepository.save(visitor);
        log.debug("Created new visitor with id {}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = false)
    public Visitor update(@NotNull Visitor.VisitorId id, @NotNull @Valid UpdateVisitorCommand cmd) {
        var visitor = visitorRepository.getReferenceById(id);
        visitor.setUsername(cmd.username());
        visitor.setEmailAddress(cmd.email());

        var saved = visitorRepository.save(visitor);
        log.debug("Updated visitor with id {}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Visitor.VisitorId id) {
        visitorRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
        log.debug("Deleted visitor with id {}", id.id()); // TODO: simply set deleted-flag
    }

    public Visitor getByReference(@NotNull Visitor.VisitorId id) {
        return visitorRepository.getReferenceById(id);
    }

}

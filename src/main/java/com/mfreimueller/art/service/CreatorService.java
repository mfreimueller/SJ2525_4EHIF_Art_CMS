package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateCreatorCommand;
import com.mfreimueller.art.commands.UpdateCreatorCommand;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.CreatorRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class CreatorService {

    private final CreatorRepository creatorRepository;

    @Transactional(readOnly = false)
    public Creator create(@NotNull @Valid CreateCreatorCommand cmd) {
        var creator = Creator.builder()
                .username(cmd.username())
                .role(cmd.role())
                .build();

        return creatorRepository.save(creator);
    }

    @Transactional(readOnly = false)
    public Creator update(@NotNull Creator.CreatorId id, @NotNull @Valid UpdateCreatorCommand cmd) {
        var creator = creatorRepository.getReferenceById(id); // TODO: handle exception
        creator.setUsername(cmd.username());
        creator.setRole(cmd.role());

        return creatorRepository.save(creator);
    }

    @Transactional(readOnly = false)
    public void delete(@NotNull Creator.CreatorId id) {
        creatorRepository.deleteById(id); // NOTE: this doesn't fail on entity not found
    }

    public Creator getByReference(@NotNull Creator.CreatorId id) {
        return creatorRepository.getReferenceById(id);
    }

}

package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Creator.CreatorId> {
    Optional<Creator> findByUsername(String username);
}

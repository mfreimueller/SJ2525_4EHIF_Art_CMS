package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Creator.CreatorId> {
}

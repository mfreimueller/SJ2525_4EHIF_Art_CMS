package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

}

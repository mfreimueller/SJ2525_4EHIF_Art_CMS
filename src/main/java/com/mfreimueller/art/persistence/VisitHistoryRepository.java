package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.VisitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitHistoryRepository extends JpaRepository<VisitHistory, Long> {

}

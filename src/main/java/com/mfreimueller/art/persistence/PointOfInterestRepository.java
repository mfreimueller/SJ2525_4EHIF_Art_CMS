package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, PointOfInterest.PointOfInterestId> {

}

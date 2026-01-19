package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, PointOfInterest.PointOfInterestId> {

    <T> Optional<T> findProjectedBy(Class<?> proj, PointOfInterest.PointOfInterestId id);

    <T> List<T> findAllProjectedBy(Class<?> proj);

}

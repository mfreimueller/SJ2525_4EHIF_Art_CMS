package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.PointOfInterest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, PointOfInterest.PointOfInterestId> {

    <T> Optional<T> findProjectedBy(Class<?> proj, PointOfInterest.PointOfInterestId id);

    <T> List<T> findAllProjectedBy(Class<?> proj);

    @Query("""
           select distinct p
           from PointOfInterest p
           join p.title t
           where key(t) = :lang
             and lower(value(t)) like lower(concat('%', :term, '%'))
           """)
    <T> Slice<T> findByTitleInLanguage(@Param("lang") String lang, @Param("term") String title, Pageable pageable);

}

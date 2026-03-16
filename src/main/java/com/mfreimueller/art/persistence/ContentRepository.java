package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Content;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("""
        SELECT c
        FROM Content c
        WHERE (:lastKey IS NULL OR c.id > :lastKey)
        ORDER BY c.id ASC
    """)
    List<Content> findSliceWithKeysetPaging(long lastKey, Pageable pageable);

}

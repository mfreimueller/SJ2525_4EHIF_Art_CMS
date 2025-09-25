package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.SlideshowContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideshowContentRepository extends JpaRepository<SlideshowContent, Long> {

}

package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.ImageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageContentRepository extends JpaRepository<ImageContent, Long> {

}

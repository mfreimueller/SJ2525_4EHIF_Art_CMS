package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.ImageContent;
import com.mfreimueller.art.domain.VideoContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoContentRepository extends JpaRepository<VideoContent, Long> {

}

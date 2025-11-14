package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.AudioContent;
import com.mfreimueller.art.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioContentRepository extends JpaRepository<AudioContent, AudioContent.ContentId> {

}

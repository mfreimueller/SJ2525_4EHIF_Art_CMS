package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.TextContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextContentRepository extends JpaRepository<TextContent, Content.ContentId> {

}

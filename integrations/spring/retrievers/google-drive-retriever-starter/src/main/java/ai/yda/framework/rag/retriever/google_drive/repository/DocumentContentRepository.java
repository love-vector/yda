package ai.yda.framework.rag.retriever.google_drive.repository;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentContentRepository extends JpaRepository<DocumentContentEntity, Long> {

    // Custom queries can be defined here.
    // For instance, if you want to find all content rows by documentId:
    // List<DocumentContentEntity> findByDocumentId(String documentId);

}
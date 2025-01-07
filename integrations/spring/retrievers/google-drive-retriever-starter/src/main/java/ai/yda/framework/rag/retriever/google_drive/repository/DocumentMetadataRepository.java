package ai.yda.framework.rag.retriever.google_drive.repository;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadataEntity, String> {
}
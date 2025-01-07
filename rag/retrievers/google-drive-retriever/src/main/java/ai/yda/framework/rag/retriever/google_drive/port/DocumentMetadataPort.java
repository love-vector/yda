package ai.yda.framework.rag.retriever.google_drive.port;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;

import java.util.Optional;

public interface DocumentMetadataPort {
    Optional<DocumentMetadataEntity> findById(String documentId);

    DocumentMetadataEntity save(DocumentMetadataEntity entity);

}

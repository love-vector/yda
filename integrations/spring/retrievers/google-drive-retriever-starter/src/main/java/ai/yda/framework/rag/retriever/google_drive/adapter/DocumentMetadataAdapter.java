package ai.yda.framework.rag.retriever.google_drive.adapter;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
import ai.yda.framework.rag.retriever.google_drive.repository.DocumentMetadataRepository;

import java.util.Optional;

public class DocumentMetadataAdapter implements DocumentMetadataPort {

    private final DocumentMetadataRepository repository;

    public DocumentMetadataAdapter(DocumentMetadataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<DocumentMetadataEntity> findById(String documentId) {
        return repository.findById(documentId);
    }

    @Override
    public DocumentMetadataEntity save(DocumentMetadataEntity entity) {
        return repository.save(entity);
    }
}

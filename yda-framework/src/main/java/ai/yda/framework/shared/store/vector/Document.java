package ai.yda.framework.shared.store.vector;

import java.util.List;

/**
 * Data structure that contains an identifier and an embedding.
 */
public interface Document {

    /**
     * Retrieves the unique identifier of the document.
     *
     * @return the unique identifier of the document
     */
    String getId();

    /**
     * Retrieves the embedding of the document.
     *
     * @return the embedding of the document
     */
    List<Double> getEmbedding();
}

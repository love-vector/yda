package ai.yda.framework.retriever.knowledge;

/**
 * Data structure that contains a name and a description.
 */
public interface Knowledge<ID> {

    /**
     * Retrieves the unique identifier of the knowledge.
     *
     * @return the unique identifier of the knowledge
     */
    ID getId();

    /**
     * Retrieves the knowledge name.
     *
     * @return the knowledge name
     */
    String getName();

    /**
     * Retrieves the knowledge description.
     *
     * @return the knowledge description
     */
    String getDescription();
}

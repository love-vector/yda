package ai.yda.framework.retriever.intent;

/**
 * Data structure that contains an identifier, name, definition, description, and vector ID.
 *
 * @param <ID> the type of the unique identifier
 */
public interface Intent<ID> {

    /**
     * Retrieves the unique identifier of the intent.
     *
     * @return the unique identifier of the intent
     */
    ID getId();

    /**
     * Retrieves the name of the intent.
     *
     * @return the name of the intent
     */
    String getName();

    /**
     * Retrieves the definition of the intent.
     *
     * @return the definition of the intent
     */
    String getDefinition();

    /**
     * Retrieves the description of the intent.
     *
     * @return the description of the intent
     */
    String getDescription();

    /**
     * Retrieves the UUID of the vector associated with the intent.
     *
     * @return the UUID of the vector associated with the intent
     */
    String getVectorId();

    /**
     * Sets the UUID of the vector associated with the intent.
     *
     * @param vectorId the UUID of the vector to be associated with the intent
     */
    void setVectorId(String vectorId);
}

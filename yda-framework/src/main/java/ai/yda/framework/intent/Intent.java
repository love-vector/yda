package ai.yda.framework.intent;

import java.util.UUID;

/**
 * Data structure that contains an identifier, name, definition, description, and vector ID.
 */
public interface Intent {

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
    UUID getVectorId();

    /**
     * Sets the UUID of the vector associated with the intent.
     */
    void setVectorId(UUID vectorId);
}

package ai.yda.channels.internal;

/**
 * The {@code CommunicationChannel} interface provides an abstraction for a communication
 * channel that accepts requests from various external sources.
 *
 * This interface defines a method for sending requests to a core processing unit which then
 * returns processed responses. Using this interface allows for uniform handling of communications
 * across different platforms.
 */
public interface CommunicationChannel {

    /**
     * Sends a communication request to the core processing system and retrieves a response.
     *
     * @param request The communication request containing the necessary information
     *                to be processed, such as message content and sender details.
     * @return a {@link CommunicationResponse} object containing the response
     *         from the core processing system. The response includes the outcome of
     *         the processing.
     */
    CommunicationResponse sendRequest(CommunicationRequest request);
}
